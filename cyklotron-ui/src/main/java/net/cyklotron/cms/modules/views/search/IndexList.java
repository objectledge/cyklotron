package net.cyklotron.cms.modules.views.search;

import net.labeo.services.resource.Resource;
import net.labeo.services.table.TableState;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.site.SiteResource;

/**
 * A list of indexes defined for the site.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: IndexList.java,v 1.1 2005-01-24 04:35:07 pablo Exp $
 */
public class IndexList extends BaseListScreen
{
    protected String getTableStateName(SiteResource site)
    {
        return "cms.search.indexeslist."+site.getName();
    }

    protected Resource getTableRoot(SiteResource site) throws SearchException
    {
        return searchService.getIndexesRoot(site);
    }

    protected void setupTableState(TableState state, RunData data)
    {
        // do nothing
    }
}
