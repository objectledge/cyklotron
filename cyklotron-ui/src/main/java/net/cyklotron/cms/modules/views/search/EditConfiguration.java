package net.cyklotron.cms.modules.views.search;

import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.search.RootResource;
import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * A screen for search application configuration.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: EditConfiguration.java,v 1.2 2005-01-25 11:24:16 pablo Exp $
 */
public class EditConfiguration extends PoolList
{
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        super.prepare(data, context);
        
        try 
        {
            SiteResource site = getSite();
            RootResource searchRoot = searchService.getSearchRoot(site);
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
