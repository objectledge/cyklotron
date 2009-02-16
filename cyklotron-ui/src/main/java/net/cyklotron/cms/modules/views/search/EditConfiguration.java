package net.cyklotron.cms.modules.views.search;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.search.RootResource;
import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * A screen for search application configuration.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: EditConfiguration.java,v 1.4 2005-03-08 11:08:42 pablo Exp $
 */
public class EditConfiguration extends PoolList
{

    public EditConfiguration(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        SearchService searchService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, searchService);
        
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        super.process(parameters, mvcContext,  templatingContext, httpContext,  i18nContext,coralSession);
        try 
        {
            SiteResource site = getSite();
            RootResource searchRoot = searchService.getSearchRoot(coralSession, site);
            NavigationNodeResource searchNode = searchRoot.getSearchNode();
            if(searchNode != null)
            {
                templatingContext.put("search_node", searchNode);
            }
        }
        catch(SearchException e)
        {
            throw new ProcessingException("cannot get search root", e);
        }
    }
}
