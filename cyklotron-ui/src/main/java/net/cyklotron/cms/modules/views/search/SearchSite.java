package net.cyklotron.cms.modules.views.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.Instantiator;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.comparator.NameComparator;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.views.BaseSkinableScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.search.searching.HitsViewPermissionFilter;
import net.cyklotron.cms.search.searching.SearchScreen;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SearchSite.java,v 1.8 2008-10-30 17:54:29 rafal Exp $
 */
public class SearchSite
    extends BaseSkinableScreen
{
    /** search serivce for analyzer nad searcher getting. */
    protected SearchService searchService;

    protected Instantiator instantiator;
    
    protected IntegrationService integrationService;


    public SearchSite(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        StructureService structureService, StyleService styleService, SkinService skinService,
        MVCFinder mvcFinder, TableStateManager tableStateManager,
        SearchService searchService,
        Instantiator instantiator, IntegrationService integrationService)
    {
        super(context, logger, preferencesService, cmsDataFactory, structureService, styleService,
                        skinService, mvcFinder, tableStateManager);
        this.searchService = searchService;
        this.instantiator = instantiator;
        this.integrationService = integrationService;
    }

    public String getState()
        throws ProcessingException
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        if(parameters.isDefined("query")
           || parameters.isDefined("field"))
        {
            return "Results";
        }
        return super.getState();
    }

    public void prepareDefault(Context context)
        throws ProcessingException
    {
        if(!preparePools(context))
        {
            return;
        }
    }

    public void prepareResults(Context context)
        throws ProcessingException
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        HttpContext httpContext = HttpContext.getHttpContext(context);
        MVCContext mvcContext = MVCContext.getMVCContext(context);
        I18nContext i18nContext = I18nContext.getI18nContext(context);
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
        if(!preparePools(context))
        {
            return;
        }
        SearchScreen sScreen = new SearchScreen(context, logger, tableStateManager,
            searchService, integrationService, cmsDataFactory, 
            new HitsViewPermissionFilter(coralSession.getUserSubject(), coralSession),
            instantiator);
        sScreen.process(parameters, templatingContext, mvcContext, i18nContext, coralSession);
    }
    
    private boolean preparePools(Context context)
        throws ProcessingException
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        HttpContext httpContext = HttpContext.getHttpContext(context);
        I18nContext i18nContext = I18nContext.getI18nContext(context);
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
        SiteResource site = getSite();
        
        Resource poolsParent;
        try
        {
            poolsParent = searchService.getPoolsRoot(coralSession, site);
        }
        catch(SearchException e)
        {
            screenError(getNode(), context, "could not get pools parent for site "+
                site.getName(), e);
            return false;
        }
        
        Parameters screenConfig = getScreenConfig();
        String[] poolNames = screenConfig.getStrings("poolNames");
        List pools = new ArrayList();
        for(int i = 0; i < poolNames.length; i++)
        {
            String poolName = poolNames[i];
            Resource[] ress = coralSession.getStore().getResource(poolsParent, poolName);
            if(ress.length == 1)
            {
                // TODO: maybe we should check the resource class
                pools.add(ress[0]);
            }
            else if(ress.length > 1)
            {
                screenError(getNode(), context, "multiple pools named "+poolName);
                return false;
            }
        }
        Collections.sort(pools, new NameComparator(i18nContext.getLocale()));
        templatingContext.put("pools",pools);
        return true;
    }
}
