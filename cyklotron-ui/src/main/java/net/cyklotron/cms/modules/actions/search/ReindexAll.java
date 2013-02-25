package net.cyklotron.cms.modules.actions.search;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.objectledge.threads.ThreadPool;
import org.jcontainer.dna.Logger;
import org.objectledge.authentication.AuthenticationException;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.coral.query.MalformedQueryException;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;
import org.objectledge.longops.LongRunningOperation;
import org.objectledge.longops.LongRunningOperationRegistry;
import org.objectledge.longops.LongRunningOperationSecurityCallback;
import org.objectledge.longops.OperationCancelledException;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;
import org.objectledge.threads.Task;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.security.SecurityChecking;

import net.cyklotron.cms.search.IndexResource;
import net.cyklotron.cms.search.IndexingFacility;

public class ReindexAll
    implements Valve, SecurityChecking
{

    protected static final String OPERATION_CODE = "bazy.search.ReindexAll";

    protected static final String OPERATION_DESC = "Bazy: Reindeksacja dokumentów";

    private final CoralSessionFactory coralSessionFactory;

    private final Logger logger;

    private final IndexingFacility indexingFacility;

    private final LongRunningOperationRegistry longRunningOperationRegistry;

    private final UserManager userManager;

    private final ThreadPool threadPool;

    public ReindexAll(CoralSessionFactory coralSessionFactory, Logger logger,
        IndexingFacility indexingFacility,
        LongRunningOperationRegistry longRunningOperationRegistry, UserManager userManager,
        ThreadPool threadPool)
    {
        super();
        this.coralSessionFactory = coralSessionFactory;
        this.logger = logger;
        this.indexingFacility = indexingFacility;
        this.longRunningOperationRegistry = longRunningOperationRegistry;
        this.userManager = userManager;
        this.threadPool = threadPool;
    }

    @Override
    public boolean requiresSecureChannel(Context context)
        throws Exception
    {
        return false;
    }

    @Override
    public boolean requiresAuthenticatedUser(Context context)
        throws Exception
    {
        return true;
    }

    @Override
    public boolean checkAccessRights(Context context)
        throws Exception
    {
        CoralSession coralSession = coralSessionFactory.getCurrentSession();
        Role cmsAdmin = coralSession.getSecurity().getRole("cms.administrator")[0];
        return coralSession.getUserSubject().hasRole(cmsAdmin);
    }

    @Override
    public void process(Context context)
        throws ProcessingException
    {
        final Principal requestor = context.getAttribute(CoralSession.class).getUserPrincipal();
        if(alreadyRunning(requestor))
        {
            HttpServletResponse response = HttpContext.getHttpContext(context).getResponse();
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
        else
        {               
        Task task = new Task()
            {
                private int counter = 1;

                @Override
                public void process(Context context)
                    throws ProcessingException
                {

                    try (CoralSession session = coralSessionFactory.getRootSession())
                    {
                        logger.info("ReindexAll job has started at " + new Date().toString());
                        QueryResults results = session.getQuery().executeQuery(
                            "FIND RESOURCE FROM " + IndexResource.CLASS_NAME);
                        final LongRunningOperation operation = longRunningOperationRegistry
                            .register(OPERATION_CODE, OPERATION_DESC, requestor,
                                results.rowCount(), new SecurityCall(requestor, userManager,
                                    coralSessionFactory));
                        try
                        {
                            Resource[] resources = results.getArray(1);
                            logger.info("preparing to reindex " + resources.length + " indexes");

                            Collection<IndexResource> indexes = new ArrayList<>();
                            for(Resource resource : resources)
                            {
                                indexes.add((IndexResource)resource);
                            }
                            Collection<IndexResource> failed = new ArrayList<>();
                            for(IndexResource index : indexes)
                            {
                                if(!operation.isCanceled())
                                {
                                    counter++;
                                    try
                                    {
                                        indexingFacility.reindex(session, index);                                       
                                    }
                                    catch(Exception e)
                                    {
                                        logger.error("Reindexing of resource: " + index.toString()
                                            + " has failed", e);
                                        failed.add(index);
                                    }
                                    longRunningOperationRegistry.update(operation, counter);
                                }
                                else{
                                    longRunningOperationRegistry.unregister(operation);
                                    break;
                                }
                            }
                            if(failed.size() > 0)
                            {
                                StringBuilder builder = new StringBuilder();
                                for(IndexResource ir : failed)
                                {
                                    builder.append(ir.toString());
                                    builder.append("\n");
                                }
                                logger.error("Failed reindexing " + failed.size()
                                    + " indexes and they were as follows: " + builder.toString());
                                logger.info("ReindexAll job has ended with errors at "
                                    + new Date().toString());
                            }
                            else
                            {
                                logger
                                    .info("ReindexAll job has ended successfully with no errors at "
                                        + new Date().toString());
                            }
                        }
                        catch(OperationCancelledException e)
                        {
                            logger.info("Operation " + OPERATION_CODE +" canceled", e);
                        }
                        finally
                        {
                            longRunningOperationRegistry.unregister(operation);
                        }
                    }
                    catch(MalformedQueryException e1)
                    {
                        throw new RuntimeException("Query syntax is wrong, Fix it", e1);
                    }
                }

            };
        threadPool.runWorker(task);
    }
    }
    private static class SecurityCall
        implements LongRunningOperationSecurityCallback
    {
        private final Principal requestor;

        private final UserManager userManager;

        private final CoralSessionFactory coralSessionFactory;

        public SecurityCall(Principal requestor, UserManager userManager,
            CoralSessionFactory coralSessionFactory)
        {
            super();
            this.requestor = requestor;
            this.userManager = userManager;
            this.coralSessionFactory = coralSessionFactory;
        }

        @Override
        public boolean canView(LongRunningOperation operation, Principal viewRequestor)
        {
            return true;
        }

        @Override
        public boolean canCancel(LongRunningOperation operation, Principal cancelRequestor)
        {
            try
            {
                return cancelRequestor.equals(requestor) || checkAdministrator()
                    || cancelRequestor.equals(userManager.getSuperuserAccount());
            }
            catch(AuthenticationException e)
            {
                throw new RuntimeException("Unexpected AuthenticationException", e);
            }
        }

        public boolean checkAdministrator()
        {
            CoralSession coralSession = coralSessionFactory.getCurrentSession();
            Role cmsAdmin = coralSession.getSecurity().getRole("cms.administrator")[0];
            return coralSession.getUserSubject().hasRole(cmsAdmin);
        }
    }
    
    private boolean alreadyRunning(Principal requestor)
    {
        Collection<LongRunningOperation> activeOperations = longRunningOperationRegistry
            .getActiveOperations(requestor);
        for(LongRunningOperation operation : activeOperations)
        {
            if(operation.getCode().equals(OPERATION_CODE))
            {
                return true;
            }
        }
        return false;
    }
}
