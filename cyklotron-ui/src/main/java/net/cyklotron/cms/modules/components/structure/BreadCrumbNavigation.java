package net.cyklotron.cms.modules.components.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.table.TableFilter;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableRow;
import org.objectledge.table.TableRowSet;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.generic.BaseRowSet;
import org.objectledge.templating.Templating;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.NavigationConfiguration;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.structure.table.NavigationTableModel;

/**
 * Bread crumb navigation component. This component uses a custom TableRowSet implementation.
 * Because of it's list-like functionality it does not depend on view type setting.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: BreadCrumbNavigation.java,v 1.6 2008-06-05 16:38:40 rafal Exp $
 */
public class BreadCrumbNavigation extends CacheableNavigation
{
    
    
    public BreadCrumbNavigation(Context context, Logger logger, Templating templating,
        CmsDataFactory cmsDataFactory, SkinService skinService, MVCFinder mvcFinder,
        TableStateManager tableStateManager,SiteService siteService, StructureService structureService)
    {
        super(context, logger, templating, cmsDataFactory, skinService, mvcFinder,
                        tableStateManager, siteService, structureService);
        
    }
    
    /**
     * The state is not changed - whole navigation logic is embedded in
     * <code>BreadCrumbRowSet</code>.
     */
    protected void setConfigParameters(TableState state, NavigationConfiguration naviConf,
                                         NavigationNodeResource currentNode)
    {
        // nothing to do
    }

    protected TableModel getTableModel(CoralSession coralSession, I18nContext i18nContext,
        NavigationConfiguration naviConf, NavigationNodeResource currentNode, NavigationNodeResource naviRoot)
    {
        return new BreadCrumbTableModel(coralSession, i18nContext.getLocale(), currentNode);
    }

    public class BreadCrumbTableModel extends NavigationTableModel
    {
        private NavigationNodeResource currentNode;

        public BreadCrumbTableModel(CoralSession coralSession, Locale locale, NavigationNodeResource currentNode)
        {
            super(coralSession, locale);
            this.currentNode = currentNode;
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
            return new BreadCrumbRowSet(state, this, currentNode, filters);
        }
    }

    /**
     * This class provides an implementation of a TableRowSet interface.
     * It ensures that rows collection is built only once.
     *
     * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
     */
    public class BreadCrumbRowSet extends BaseRowSet
    {
        private NavigationNodeResource currentNode;
        private BreadCrumbTableModel model;
        
        private TableRow rootRow;
        private TableRow[] rows;
        
        /** This map allows quick parent row lookup.
         * Used in {@link #hasMoreChildren(TableRow,TableRow)} and {@link
         * #getParentRow(TableRow)}. */
        protected Map rowsByChild = new HashMap();

        /**
         * construct the object
         *
         * @param state the state of the table instance
         * @param model the table model
         */
        public BreadCrumbRowSet(TableState state, BreadCrumbTableModel model, NavigationNodeResource currentNode, TableFilter[] filters)
        {
            super(state, filters);
            this.model = model;
            this.currentNode = currentNode;
        }

        /**
         * Returns an list of {@link TableRow} objects, which represents
         * a view of TableModel data defined by TableState object.
         *
         * @return a list of rows.
         */
        public TableRow[] getRows()
        {
            if(rows == null)
            {
                Object root = model.getObject(state.getRootId());
                List parentNodes = currentNode.getParentNavigationNodes(true);

                // add current node because it is not included
                parentNodes.add(currentNode);
                
                // look for real navigation's root
                List nodes = new ArrayList(parentNodes.size());
                boolean gotRoot = false;
                for(int i = 0; i < parentNodes.size(); i++)
                {
                    Object object = parentNodes.get(i);
                    if(object == root)
                    {
                        gotRoot = true;
                    }
                    
                    if(gotRoot)
                    {
                        nodes.add(object);
                    }
                }

                // create rows collection
                List list = new ArrayList(parentNodes.size());
                TableRow parentRow = null;
                for(int i = 0; i < nodes.size(); i++)
                {
                    Object object = nodes.get(i);
                    if(checkDepth(i) && accept(object))
                    {
                        int childCount = model.getChildren((Resource)object).length;
                        int visibleChildCount = (checkDepth(i+1) && (i < parentNodes.size()))?1:0;
                        TableRow row = new TableRow(model.getId(null,(Resource)object), object, i,
                                                    childCount, visibleChildCount);
                        list.add(row);
                        
                        if(i == 0)
                        {
                            rootRow = row;
                        }
                        else
                        {
                            rowsByChild.put(row, parentRow);
                        }
                        parentRow = row;
                    }
                }

                rows = new TableRow[list.size()];
                rows = (TableRow[])list.toArray(rows);
            }
            return rows;
        }

        /** Return the number of elements in returned array.
         *
         * @return the size of returned array
         *
         */
        public int getPageRowCount()
        {
            getRows();
            return rows.length;
        }

        /** Gets the parent of object.
         *
         */
        public TableRow getParentRow(TableRow childRow)
        {
            return (TableRow)(rowsByChild.get(childRow));
        }

        /** Gets the root node of the row set.
         *
         */
        public TableRow getRootRow()
        {
            return rootRow;
        }

        /** Return the total number of elements in this rowset.
         *
         * @return the size of this rowset
         *
         */
        public int getTotalRowCount()
        {
            getRows();
            return rows.length;
        }

        /** Checks whether the ancestor has more children
         *
         * @return <code>true</code> if has more children
         *
         */
        public boolean hasMoreChildren(TableRow ancestorRow, TableRow descendantRow)
        {
            return false;
        }
    }
}
