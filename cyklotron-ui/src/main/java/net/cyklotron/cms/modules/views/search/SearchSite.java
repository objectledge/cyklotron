package net.cyklotron.cms.modules.views.search;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jcontainer.dna.Logger;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.StoreService;
import net.labeo.services.table.TableService;
import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Configuration;
import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.views.BaseSkinableScreen;
import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.search.searching.HitsProtectedFilter;
import net.cyklotron.cms.search.searching.SearchScreen;
import net.cyklotron.cms.site.SiteResource;

/**
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SearchSite.java,v 1.1 2005-01-24 04:35:07 pablo Exp $
 */
public class SearchSite
    extends BaseSkinableScreen
{
    /** search serivce for analyzer nad searcher getting. */
    protected SearchService searchService;

    /** table service for hit list display. */
    TableService tableService;

    /** logging facility */
    protected Logger log;

    public SearchSite()
    {
        super();
        searchService = (SearchService)broker.getService(SearchService.SERVICE_NAME);
        tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).
            getFacility(SearchService.LOGGING_FACILITY);
    }

    public String getState(RunData data)
        throws ProcessingException
    {
        if(parameters.get("query").isDefined()
           || parameters.get("field").isDefined())
        {
            return "Results";
        }
        return super.getState(data);
    }

    public void prepareDefault(RunData data, Context context)
        throws ProcessingException
    {
        if(!preparePools(data, context))
        {
            return;
        }
    }

    public void prepareResults(RunData data, Context context)
        throws ProcessingException
    {
        if(!preparePools(data, context))
        {
            return;
        }
        
        SearchScreen sScreen = new SearchScreen(broker,
            new HitsProtectedFilter(coralSession.getUserSubject(), new Date(), coralSession));
        sScreen.prepare(data, context);
    }
    
    private boolean preparePools(RunData data, Context context)
        throws ProcessingException
    {
        SiteResource site = getSite();
        
        Resource poolsParent;
        try
        {
            poolsParent = searchService.getPoolsRoot(site);
        }
        catch(SearchException e)
        {
            screenError(getNode(), context, "could not get pools parent for site "+
                site.getName());
            return false;
        }
        
        Parameters screenConfig = getConfiguration();
        Parameter[] poolNames = screenConfig.getArray("poolNames");

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
                screenError(getNode(), context, "multiple pools named "+poolName);
                return false;
            }
        }
        templatingContext.put("pools",pools);
        return true;
    }
}
