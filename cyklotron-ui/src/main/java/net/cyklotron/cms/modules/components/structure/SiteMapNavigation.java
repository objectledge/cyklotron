package net.cyklotron.cms.modules.components.structure;

import java.util.Locale;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.table.ExtendedTableModel;
import org.objectledge.table.TableFilter;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableRowSet;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.generic.GenericListRowSet;
import org.objectledge.table.generic.GenericTreeRowSet;
import org.objectledge.templating.Templating;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.NavigationConfiguration;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;
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
 * @version $Id: SiteMapNavigation.java,v 1.2 2005-01-26 03:52:35 pablo Exp $
 */

public class SiteMapNavigation extends CacheableNavigation
{
    
    
    public SiteMapNavigation(Context context, Logger logger, Templating templating,
        CmsDataFactory cmsDataFactory, SkinService skinService, MVCFinder mvcFinder,
        TableStateManager tableStateManager, StructureService structureService)
    {
        super(context, logger, templating, cmsDataFactory, skinService, mvcFinder,
                        tableStateManager, structureService);
        // TODO Auto-generated constructor stub
    }
    protected void setConfigParameters(TableState state, NavigationConfiguration naviConf,
                                      NavigationNodeResource currentNode)
    {
        // - - - - - - - CONFIGURATION parameters

        // PARAMETER: ViewType
        state.setTreeView(naviConf.getViewType());
    }

    protected TableModel getTableModel(CoralSession coralSession, I18nContext i18nContext,
        NavigationConfiguration naviConf, NavigationNodeResource currentNode)
    {
        return new SiteMapTableModel(coralSession, i18nContext.getLocale());
    }

    public class SiteMapTableModel extends NavigationTableModel implements ExtendedTableModel
    {
        public SiteMapTableModel(CoralSession coralSession, Locale locale)
        {
            super(coralSession, locale);
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
            if(state.getTreeView() == false)
            {
                return new GenericListRowSet(state, filters, this);
            }
            else
            {
                return new SiteMapTreeRowSet(state, filters, this);
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
        public SiteMapTreeRowSet(TableState state, TableFilter[] filters, ExtendedTableModel model)
        {
            super(state, filters, model);
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
