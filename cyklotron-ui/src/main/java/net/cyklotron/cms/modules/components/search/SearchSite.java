package net.cyklotron.cms.modules.components.search;

import java.util.ArrayList;
import java.util.List;

import net.labeo.Labeo;
import net.labeo.services.ServiceBroker;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.StoreService;
import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Configuration;
import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.components.SkinableCMSComponent;
import net.cyklotron.cms.search.RootResource;
import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * Search site component.
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SearchSite.java,v 1.2 2005-01-25 11:24:25 pablo Exp $
 */
public class SearchSite
    extends SkinableCMSComponent
{
    /** search service */
    protected SearchService searchService;

    public SearchSite()
    {
        ServiceBroker broker = Labeo.getBroker();
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).
            getFacility(SearchService.LOGGING_FACILITY);
        searchService = (SearchService)broker.getService(SearchService.SERVICE_NAME);
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        if(getSite(context) == null)
        {
            componentError(context, "No site selected");
            return;
        }
        SiteResource site = getSite(context);

        // get search node for redirecting the search results view
        try
        {
            RootResource searchRoot = searchService.getSearchRoot(site);
            NavigationNodeResource searchNode = searchRoot.getSearchNode();
            if(searchNode == null)
            {
                componentError(context, "no search node defined for site "
                    +site.getName());
                return;
            }
            else if(searchNode.getSite() != site)
            {
                componentError(context, "search node for site "+site.getName()
                    +" defined in site "+searchNode.getSite().getName());
                return;
            }
            templatingContext.put("search_node", searchNode);
        }
        catch(SearchException e)
        {
            componentError(context, "cannot get search root for site "+site.getName());
            return;
        }

        // get index pools available for this site
        Resource poolsParent = null;
        try
        {
            poolsParent = searchService.getPoolsRoot(site);
        }
        catch(SearchException e)
        {
            componentError(context, "cannot get pools root for site "+site.getName());
            return;
        }

        Parameters componentConfig = getConfiguration();
        Parameter[] poolNames = componentConfig.getArray("poolNames");

        StoreService storeService = coralSession.getStore();
        List pools = new ArrayList();
        for(int i = 0; i < poolNames.length; i++)
        {
            String poolName = poolNames[i].asString("");
            Resource[] ress = storeService.getResource(poolsParent, poolName);
            if(ress.length == 1)
            {
                // TODO: maybe we should check the resource class
                pools.add(ress[0]);
            }
            else if(ress.length > 1)
            {
                componentError(context, "multiple pools named "+poolName);
                return;
            }
        }
        templatingContext.put("pools",pools);
    }
}
