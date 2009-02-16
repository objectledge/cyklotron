package net.cyklotron.cms.modules.views.search;

import java.util.Arrays;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.Instantiator;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.search.searching.HitsViewPermissionFilter;
import net.cyklotron.cms.search.searching.SearchScreen;
import net.cyklotron.cms.site.SiteResource;

/**
 * Searching screen for administrators.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: Search.java,v 1.4 2005-06-15 12:37:39 zwierzem Exp $
 */
public class Search extends BaseSearchScreen
{
    protected Instantiator instantiator;
    
    protected IntegrationService integrationService;
    
    public Search(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, SearchService searchService,
        Instantiator instantiator, IntegrationService integrationService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, searchService);
        this.instantiator = instantiator;
        this.integrationService = integrationService;
    }
    
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        // get pool list
        SiteResource site = getSite();
        Resource poolsParent;
        try
        {
            poolsParent = searchService.getPoolsRoot(coralSession, site);
        }
        catch(SearchException e)
        {
            throw new ProcessingException("could not get pools parent for site "+site.getName(), e);
        }
        
        Resource[] pools = coralSession.getStore().getResource(poolsParent);
        templatingContext.put("pools", Arrays.asList(pools));

        // search
        SearchScreen sScreen = new SearchScreen(context, logger, tableStateManager,
            searchService, integrationService, cmsDataFactory, 
            new HitsViewPermissionFilter(coralSession.getUserSubject(), coralSession),
            instantiator);
        sScreen.process(parameters, templatingContext, mvcContext, i18nContext, coralSession);
    }
}
