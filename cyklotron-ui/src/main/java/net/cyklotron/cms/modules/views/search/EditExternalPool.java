package net.cyklotron.cms.modules.views.search;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.search.ExternalPoolResource;
import net.cyklotron.cms.search.ExternalPoolResourceData;
import net.cyklotron.cms.search.SearchService;

/**
 * A screen for editing external search pools.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: EditExternalPool.java,v 1.4 2005-01-26 09:00:39 pablo Exp $
 */
public class EditExternalPool extends BaseSearchScreen
{
    
    public EditExternalPool(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, SearchService searchService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, searchService);
        // TODO Auto-generated constructor stub
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        // get pool if it is defined
        ExternalPoolResource pool = null;
        if(parameters.isDefined("pool_id"))
        {
            pool = getExternalPool(coralSession, parameters);
            templatingContext.put("pool", pool);
        }
        // get pool resource data
        if(parameters.getBoolean("from_list",false))
        {
            ExternalPoolResourceData.removeData(httpContext, pool);
        }
        ExternalPoolResourceData poolData = ExternalPoolResourceData.getData(httpContext, pool);
        templatingContext.put("pool_data", poolData);
        
        // setup pool data
        if(poolData.isNew())
        {
            poolData.init(pool);
        }
        else
        {
            poolData.update(parameters);
        }
    }
    
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);
        if(parameters.isDefined("pool_id"))
        {
            return checkPermission(context, coralSession, "cms.search.external.pool.modify");
        }
        else
        {
            return checkPermission(context, coralSession, "cms.search.external.pool.add");
        }
    }
}
