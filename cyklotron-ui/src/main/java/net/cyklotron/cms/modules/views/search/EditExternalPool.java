package net.cyklotron.cms.modules.views.search;

import net.cyklotron.cms.search.ExternalPoolResource;
import net.cyklotron.cms.search.ExternalPoolResourceData;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * A screen for editing external search pools.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: EditExternalPool.java,v 1.1 2005-01-24 04:35:07 pablo Exp $
 */
public class EditExternalPool extends BaseSearchScreen
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        // get pool if it is defined
        ExternalPoolResource pool = null;
        if(parameters.get("pool_id").isDefined())
        {
            pool = getExternalPool(data);
            templatingContext.put("pool", pool);
        }
        // get pool resource data
        if(parameters.get("from_list").asBoolean(false))
        {
            ExternalPoolResourceData.removeData(data, pool);
        }
        ExternalPoolResourceData poolData = ExternalPoolResourceData.getData(data, pool);
        templatingContext.put("pool_data", poolData);
        
        // setup pool data
        if(poolData.isNew())
        {
            poolData.init(pool);
        }
        else
        {
            poolData.update(data);
        }
    }
    
    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        if(parameters.get("pool_id").isDefined())
        {
            return checkPermission(context, coralSession, "cms.search.external.pool.modify");
        }
        else
        {
            return checkPermission(context, coralSession, "cms.search.external.pool.add");
        }
    }
}
