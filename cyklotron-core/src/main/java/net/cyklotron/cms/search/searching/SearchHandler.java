package net.cyklotron.cms.search.searching;

import java.util.List;

import org.objectledge.coral.store.Resource;
import org.objectledge.table.TableState;
import org.objectledge.table.TableTool;

/**
 * An interface for diferent search handler inmplementations. For instance:
 * - external search handler
 * - internal search handler
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SearchHandler.java,v 1.2 2005-01-19 08:22:56 pablo Exp $
 */
public interface SearchHandler
{
    public TableTool search(Resource[] searchPools, SearchMethod method, TableState state, List tableFilters, RunData data)
        throws SearchingException;
}
