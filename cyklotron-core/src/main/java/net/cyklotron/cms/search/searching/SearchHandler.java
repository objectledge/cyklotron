package net.cyklotron.cms.search.searching;

import java.util.List;

import net.labeo.services.resource.Resource;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * An interface for diferent search handler inmplementations. For instance:
 * - external search handler
 * - internal search handler
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SearchHandler.java,v 1.1 2005-01-12 20:44:40 pablo Exp $
 */
public interface SearchHandler
{
    public TableTool search(Resource[] searchPools, SearchMethod method, TableState state, List tableFilters, RunData data)
        throws SearchingException;
}
