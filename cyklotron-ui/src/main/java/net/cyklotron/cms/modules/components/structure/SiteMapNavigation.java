package net.cyklotron.cms.modules.components.structure;

import java.util.Locale;

import net.labeo.services.table.ExtendedTableModel;
import net.labeo.services.table.GenericListRowSet;
import net.labeo.services.table.GenericTreeRowSet;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableFilter;
import net.labeo.services.table.TableModel;
import net.labeo.services.table.TableRowSet;
import net.labeo.services.table.TableState;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.structure.NavigationConfiguration;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.table.NavigationTableModel;

/**
 * Site map navigation component, which shows the whole site/section structure.
 * ViewType parameter is configured:
 * <ul>
 * <li><b>tree view</b> - for conventional tree site map,</li>
 * <li><b>list view</b> - for list of all pages on in the site (kind of page index).
 * </ul>
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: SiteMapNavigation.java,v 1.1 2005-01-24 04:35:20 pablo Exp $
 */

public class SiteMapNavigation extends CacheableNavigation
{
    protected void setConfigParameters(TableState state, NavigationConfiguration naviConf,
                                      NavigationNodeResource currentNode)
    {
        // - - - - - - - CONFIGURATION parameters

        // PARAMETER: ViewType
        state.setViewType(naviConf.getViewType());
    }

    protected TableModel getTableModel(RunData data, NavigationConfiguration naviConf,
                                               NavigationNodeResource currentNode)
    {
        return new SiteMapTableModel(i18nContext.getLocale()());
    }

    public class SiteMapTableModel extends NavigationTableModel implements ExtendedTableModel
    {
        public SiteMapTableModel(Locale locale)
        {
            super(locale);
        }

        /**
         * Returns a {@link TableRowSet} object initialised by this model
         * and a given {@link TableState}.
         *
         * @param state the parent
         * @return table of children
         */
        public TableRowSet getRowSet(TableState state, TableFilter[] filters)
        {
            if(state.getViewType() == TableConstants.VIEW_AS_LIST)
            {
                return new GenericListRowSet(state, this, filters);
            }
            else
            {
                return new SiteMapTreeRowSet(state, this, filters);
            }
        }
    }

    /**
     * This class provides an implementation of a TableRowSet interface.
     * It ensures that rows collection is built only once. It also ensures that all of the nodes in
     * SiteMap will be expanded.
     *
     * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
     */
    public class SiteMapTreeRowSet extends GenericTreeRowSet
    {
        /**
         * construct the object
         *
         * @param state the state of the table instance
         * @param model the table model
         */
        public SiteMapTreeRowSet(TableState state, ExtendedTableModel model, TableFilter[] filters)
        {
            super(state, model, filters);
        }

        /**
         * Check whether the object with a given id is expanded.
         * <i>WARNING: Because this is SiteMapTreeRowSet - <b>every</b> row is expanded</i>.
         *
         * @param id the id of an object to be check for being expaned.
         * @return <code>true</code> if expanded.
         */
        protected boolean expanded(String id)
        {
            return true;
        }
    }
}
