package net.cyklotron.cms.modules.views.search;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.site.SiteResource;

/**
 * A list of index pools defined fo the site.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: PoolList.java,v 1.3 2005-03-08 11:08:42 pablo Exp $
 */
public class PoolList extends BaseListScreen
{
    public PoolList(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        SearchService searchService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, searchService);
        
    }
    
    protected String getTableStateName(CoralSession coralSession, SiteResource site)
    {
        return "cms.search.poolslist."+site.getName();
    }

    protected Resource getTableRoot(CoralSession coralSession, SiteResource site) throws SearchException
    {
        return searchService.getPoolsRoot(coralSession, site);
    }

    protected void setupTableState(TableState state)
    {
        // do nothing
    }
}
