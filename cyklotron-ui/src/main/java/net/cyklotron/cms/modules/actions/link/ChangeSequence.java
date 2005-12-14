package net.cyklotron.cms.modules.actions.link;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.datatypes.ResourceList;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.link.BaseLinkResource;
import net.cyklotron.cms.link.BaseLinkResourceImpl;
import net.cyklotron.cms.link.LinkService;
import net.cyklotron.cms.link.PoolResource;
import net.cyklotron.cms.link.PoolResourceImpl;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.workflow.WorkflowService;


/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: ChangeSequence.java,v 1.5 2005-12-14 11:44:10 pablo Exp $
 */
public class ChangeSequence
    extends BaseLinkAction
{
    private CoralSessionFactory coralSessionFactory;
    
    public ChangeSequence(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, LinkService linkService, WorkflowService workflowService,
        CoralSessionFactory coralSessionFactory)
    {
        super(logger, structureService, cmsDataFactory, linkService, workflowService);
        this.coralSessionFactory = coralSessionFactory;
        
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Subject subject = coralSession.getUserSubject();

        int lid = parameters.getInt("lid", -1);
        int pid = parameters.getInt("pid", -1);
        int offset = parameters.getInt("offset", 0);
        if(lid == -1 || pid == -1 || offset == 0)
        {
            throw new ProcessingException("pool id nor link id nor offset not found");
        }

        try
        {
            PoolResource poolResource = PoolResourceImpl.getPoolResource(coralSession, pid);
            BaseLinkResource linkResource = BaseLinkResourceImpl.getBaseLinkResource(coralSession, lid);
            ResourceList links = poolResource.getLinks();
            int position = links.indexOf(linkResource);
            if(position+offset < 0 || position+offset >= links.size())
            {
                templatingContext.put("result","illegal_sequence");
                return;
            }
            links.remove(position);
            links.add(position+offset,linkResource);
            poolResource.setLinks(new ResourceList(coralSessionFactory, links));
            poolResource.update();
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            logger.error("LinkException: ",e);
            return;
        }
        templatingContext.put("result","changed_successfully");
    }
}


