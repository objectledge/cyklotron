package net.cyklotron.cms.modules.actions.category;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.structure.StructureService;

/**
 * Base action for all category actions dealing with categorized resource.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: BaseCategorizationAction.java,v 1.2 2005-01-24 10:27:04 pablo Exp $
 */
public abstract class BaseCategorizationAction
    extends BaseCategoryAction
{
    public BaseCategorizationAction(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, CategoryService categoryService,
        IntegrationService integrationService)
    {
        super(logger, structureService, cmsDataFactory, categoryService, integrationService);
        // TODO Auto-generated constructor stub
    }
    /**
     * Returns a resource to be operated on. Relies on <code>res_id</code> parameter.
     */
    protected Resource getResource(CoralSession coralSession, Parameters parameters)
        throws ProcessingException
    {
        // prepare categorized resource
        long res_id = parameters.getLong("res_id", -1);
        Resource resource;
        if(res_id == -1)
        {
            throw new ProcessingException("Parameter res_id is not defined");
        }
        else
        {
            try
            {
                resource = coralSession.getStore().getResource(res_id);
                if(resource instanceof CategoryResource)
                {
                    throw new ProcessingException("Cannot categorize categories");
                }
            }
            catch(EntityDoesNotExistException e)
            {
                throw new ProcessingException("resource does not exist",e);
            }
        }
        return resource;
    }

    /**
     * Checks the current user's privileges to running this action.
     *
     * <p>This action requires that the current user has "cms.category.categorize"
     * on a categorized resource specified by the "res_id" request parameter.</p>
     * 
     * @param data the RunData.
     * @throws ProcessingException if the privileges could not be determined.
     */
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        try
        {
            // prepare categorized resource
            Resource res = getResource(coralSession, parameters);
            Permission perm = coralSession.getSecurity().
                getUniquePermission("cms.category.categorize");
            return coralSession.getUserSubject().hasPermission(res, perm);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to check access privileges", e);
        }
    }
}
