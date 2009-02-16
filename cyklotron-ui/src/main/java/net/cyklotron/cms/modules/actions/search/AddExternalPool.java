package net.cyklotron.cms.modules.actions.search;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.search.ExternalPoolResource;
import net.cyklotron.cms.search.ExternalPoolResourceData;
import net.cyklotron.cms.search.ExternalPoolResourceImpl;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.StructureService;

/**
 * External search pool adding action.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: AddExternalPool.java,v 1.9 2007-11-18 21:25:05 rafal Exp $
 */
public class AddExternalPool extends BaseSearchAction
{
    
    public AddExternalPool(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SearchService searchService)
    {
        super(logger, structureService, cmsDataFactory, searchService);
        
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Subject subject = coralSession.getUserSubject();
        ExternalPoolResourceData poolData = ExternalPoolResourceData.getData(httpContext, null);
        poolData.update(parameters);
        if(poolData.getName().equals(""))
        {
            templatingContext.put("result", "name_empty");
            return;
        }
        if(!coralSession.getStore().isValidResourceName(poolData.getName()))
        {
            templatingContext.put("result", "name_invalid");
            return;
        }
        
        SiteResource site = getSite(context);
        try
        {
            Resource root = searchService.getPoolsRoot(coralSession, site);

            if(coralSession.getStore().getResource(root, poolData.getName()).length > 0)
            {
                templatingContext.put("result","cannot_add_pools_with_the_same_name");
                return;
            }
            
            ExternalPoolResource pool = ExternalPoolResourceImpl
                .createExternalPoolResource(coralSession,
                    poolData.getName(), root, poolData.getSearchHandler());
            
            pool.setDescription(poolData.getDescription());
            
            // TODO: get URL template from handler
            pool.setUrlTemplate(poolData.getUrlTemplate());
                        
            pool.update();
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace", new StackTrace(e));
            logger.error("problem adding an external search pool for site '"+site.getName()+"'", e);
            return;
        }

        ExternalPoolResourceData.removeData(httpContext, null);
        mvcContext.setView("search.PoolList");
        templatingContext.put("result","added_successfully");
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        if(!cmsData.isApplicationEnabled("search"))
        {
            logger.debug("Application 'search' not enabled in site");
            return false;
        }
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return checkPermission(context, coralSession, "cms.search.external.pool.add");
    }
}
