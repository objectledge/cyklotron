package net.cyklotron.cms.modules.views.search;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.search.ExternalPoolResource;
import net.cyklotron.cms.search.IndexResource;
import net.cyklotron.cms.search.PoolResource;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.search.SearchUtil;

/**
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseSearchScreen.java,v 1.3 2007-02-25 14:18:52 pablo Exp $
 */
public abstract class BaseSearchScreen extends BaseCMSScreen
{
    /** search service */
    protected SearchService searchService;

    
    public BaseSearchScreen(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        SearchService searchService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.searchService = searchService;
    }

    public IndexResource getIndex(CoralSession coralSession, Parameters parameters)
        throws ProcessingException
    {
        return SearchUtil.getIndex(coralSession, parameters);
    }

    public PoolResource getPool(CoralSession coralSession, Parameters parameters)
        throws ProcessingException
    {
        return SearchUtil.getPool(coralSession, parameters);
    }

    public ExternalPoolResource getExternalPool(CoralSession coralSession, Parameters parameters)
        throws ProcessingException
    {
        return SearchUtil.getExternalPool(coralSession, parameters);
    }

    public boolean checkPermission(Context context, CoralSession coralSession, String permissionName)
        throws ProcessingException
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        return SearchUtil.checkPermission(coralSession, parameters, permissionName);
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
        return true;
    }
}

