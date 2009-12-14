package net.cyklotron.cms.search.searching;

import java.util.List;

import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.table.TableState;
import org.objectledge.table.TableTool;

/**
 * An interface for diferent search handler inmplementations. For instance:
 * - external search handler
 * - internal search handler
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SearchHandler.java,v 1.3 2005-01-20 06:52:40 pablo Exp $
 */
public interface SearchHandler
{
    public TableTool search(CoralSession coralSession, Resource[] searchPools, SearchMethod method, TableState state, List tableFilters, Parameters parameters, I18nContext i18nContext)
        throws SearchingException;
}
