package net.cyklotron.cms.modules.views.search;

import net.labeo.services.resource.Resource;
import net.labeo.services.table.TableState;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.site.SiteResource;

/**
 * A list of index pools defined fo the site.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: PoolList.java,v 1.1 2005-01-24 04:35:07 pablo Exp $
 */
public class PoolList extends BaseListScreen
{
    protected String getTableStateName(SiteResource site)
    {
        return "cms.search.poolslist."+site.getName();
    }

    protected Resource getTableRoot(SiteResource site) throws SearchException
    {
        return searchService.getPoolsRoot(site);
    }

    protected void setupTableState(TableState state, RunData data)
    {
        // do nothing
    }
}
