package net.cyklotron.cms.modules.actions.search;

import net.cyklotron.cms.search.ExternalPoolResource;
import net.cyklotron.cms.search.ExternalPoolResourceData;
import net.cyklotron.cms.search.ExternalPoolResourceImpl;
import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.site.SiteResource;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.services.templating.Context;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * External search pool adding action.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: AddExternalPool.java,v 1.2 2005-01-24 10:27:13 pablo Exp $
 */
public class AddExternalPool extends BaseSearchAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();

        ExternalPoolResourceData poolData = ExternalPoolResourceData.getData(data, null);
        poolData.update(data);
        
        if(poolData.getName().equals(""))
        {
            templatingContext.put("result", "name_empty");
            return;
        }
        
        SiteResource site = getSite(context);
        try
        {
            Resource root = searchService.getPoolsRoot(site);

            if(coralSession.getStore().getResource(root, poolData.getName()).length > 0)
            {
                templatingContext.put("result","cannot_add_pools_with_the_same_name");
                return;
            }
            
            ExternalPoolResource pool = ExternalPoolResourceImpl
                .createExternalPoolResource(coralSession,
                    poolData.getName(), root, poolData.getSearchHandler(), subject);
            
            pool.setDescription(poolData.getDescription());
            
            // TODO: get URL template from handler
            pool.setUrlTemplate(poolData.getUrlTemplate());
                        
            pool.update(subject);
        }
        catch(SearchException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            log.error("problem adding an external search pool for site '"+site.getName()+"'", e);
            return;
        }
        catch(ValueRequiredException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace", new StackTrace(e));
            log.error("problem adding an external search pool for site '"+site.getName()+"'", e);
            return;
        }

        ExternalPoolResourceData.removeData(data, null);
        try
        {
            mvcContext.setView("search,PoolList");
        }
        catch(NotFoundException e)
        {
            throw new ProcessingException("cannot redirect to pool list", e);
        }
        templatingContext.put("result","added_successfully");
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return checkPermission(context, coralSession, "cms.search.external.pool.add");
    }
}
