package net.cyklotron.cms.modules.actions.search;

import java.util.ArrayList;
import java.util.List;

import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.services.templating.Context;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.search.PoolResource;
import net.cyklotron.cms.search.PoolResourceData;
import net.cyklotron.cms.search.PoolResourceImpl;
import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.site.SiteResource;

/**
 * Index pool adding action.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: AddPool.java,v 1.1 2005-01-24 04:34:07 pablo Exp $
 */
public class AddPool
    extends BaseSearchAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();

        PoolResourceData poolData = PoolResourceData.getData(data, null);
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
            
            PoolResource pool = PoolResourceImpl
                .createPoolResource(coralSession, poolData.getName(), root, subject);
            
            pool.setDescription(poolData.getDescription());
            // set pool indexes
            List newIndexes = new ArrayList(poolData.getIndexesSelectionState()
                .getResources(coralSession, "selected").keySet());
            pool.setIndexes(newIndexes);
            
            pool.update(subject);
        }
        catch(SearchException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",StringUtils.stackTrace(e));
            log.error("problem adding an index pool for site '"+site.getName()+"'", e);
            return;
        }
        catch(ValueRequiredException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace", StringUtils.stackTrace(e));
            log.error("problem adding an index pool for site '"+site.getName()+"'", e);
            return;
        }

        PoolResourceData.removeData(data, null);
        try
        {
            data.setView("search,PoolList");
        }
        catch(NotFoundException e)
        {
            throw new ProcessingException("cannot redirect to pool list", e);
        }
        templatingContext.put("result","added_successfully");
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        return checkPermission(context, coralSession, "cms.search.pool.add");
    }
}
