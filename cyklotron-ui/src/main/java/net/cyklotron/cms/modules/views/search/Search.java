package net.cyklotron.cms.modules.views.search;

import java.util.Arrays;

import net.labeo.services.resource.Resource;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.search.searching.HitsViewPermissionFilter;
import net.cyklotron.cms.search.searching.SearchScreen;
import net.cyklotron.cms.site.SiteResource;

/**
 * Searching screen for administrators.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: Search.java,v 1.2 2005-01-25 11:24:16 pablo Exp $
 */
public class Search extends BaseSearchScreen
{
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        // get pool list
        SiteResource site = getSite();
        Resource poolsParent;
        try
        {
            poolsParent = searchService.getPoolsRoot(site);
        }
        catch(SearchException e)
        {
            throw new ProcessingException("could not get pools parent for site "+site.getName(), e);
        }
        
        Resource[] pools = coralSession.getStore().getResource(poolsParent);
        templatingContext.put("pools", Arrays.asList(pools));

        // search
        SearchScreen sScreen = new SearchScreen(broker,
            new HitsViewPermissionFilter(coralSession.getUserSubject(), coralSession));
        sScreen.prepare(data, context);
    }
}
