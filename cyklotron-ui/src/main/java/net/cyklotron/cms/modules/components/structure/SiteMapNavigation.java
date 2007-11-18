package net.cyklotron.cms.modules.components.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.table.ExtendedTableModel;
import org.objectledge.table.TableColumn;
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
 * @version $Id: SiteMapNavigation.java,v 1.6 2007-11-18 21:25:59 rafal Exp $
 */

public class SiteMapNavigation extends CacheableNavigation
{
    
    
    public SiteMapNavigation(Context context, Logger logger, Templating templating,
        CmsDataFactory cmsDataFactory, SkinService skinService, MVCFinder mvcFinder,
        TableStateManager tableStateManager, StructureService structureService)
    {
        super(context, logger, templating, cmsDataFactory, skinService, mvcFinder,
                        tableStateManager, structureService);
        
    }
    protected void setConfigParameters(TableState state, NavigationConfiguration naviConf,
                                      NavigationNodeResource currentNode)
    {
        // - - - - - - - CONFIGURATION parameters

        // PARAMETER: ViewType
        state.setTreeView(naviConf.getViewType());
    }

    protected TableModel getTableModel(CoralSession coralSession, I18nContext i18nContext,
        NavigationConfiguration naviConf, NavigationNodeResource currentNode,
        NavigationNodeResource naviRoot)
    {
        return new SiteMapTableModel(coralSession, i18nContext.getLocale(), naviConf, naviRoot);
    }

    public class SiteMapTableModel extends NavigationTableModel implements ExtendedTableModel
    {
        private final NavigationConfiguration naviConf;
        private final NavigationNodeResource naviRoot;

        public SiteMapTableModel(CoralSession coralSession, Locale locale,
            NavigationConfiguration naviConf, NavigationNodeResource naviRoot)
        {
            super(coralSession, locale);
            this.naviConf = naviConf;
            this.naviRoot = naviRoot;
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
            if(naviConf.getNumColumns() != 1)
            {
                List<Resource> headers = getBranchHeaders(state, filters, naviConf.getNumColumns(), naviConf
                    .getShowColumn());
                TableFilter filter = new ColumnSplitterFilter(naviRoot, headers);
                filters = addFilter(filters, filter);
            }
            if(state.getTreeView() == false)
            {
                return new GenericListRowSet(state, filters, this);
            }
            else
            {
                return new SiteMapTreeRowSet(state, filters, this);
            }
        }
        
        private List<Resource> getBranchHeaders(TableState state, TableFilter[] filters,
            int numColumns, int showColumn)
        {
            List<Resource> headers = new ArrayList<Resource>();
            headers.addAll(Arrays.asList(naviRoot.getChildren()));
            apply(headers, filters);
            Collections.sort(headers, getComparator(state));
            List<Integer> counts = new ArrayList<Integer>(headers.size());
            int depth = state.getMaxVisibleDepth();
            if(depth == 0)
            {
                depth = -1;
            }
            for(Resource r : headers)
            {
                counts.add(countRows(r, depth, filters));
            }
            return splice(headers, counts, numColumns, showColumn);
        }
        
        private List<Resource> splice(List<Resource> in, List<Integer> counts, int splices, int spliceNum)
        {
           int sum = 0;
           for(Integer i : counts)
           {
               sum += i;
           }
           int spliceSize = sum / splices;
           int start = 0;
           int end = 1;
           int curSplice = 1;
           while(end < in.size())
           {
               while(sum(counts, 0, end) < spliceSize*curSplice && end < in.size())
               {
                   end++;
               }
               if(curSplice == spliceNum)
               {
                   return in.subList(start, end);                   
               }
               else
               {
                   curSplice++;
               }
               start = end;
               end = start + 1;
           }
           return Collections.EMPTY_LIST;
        }
        
        private int sum(List<Integer> counts, int start, int end)
        {
            int sum = 0;
            for(int i = start; i < end; i++)
            {
                sum += counts.get(i);
            }
            return sum;
        }
        
        private int countRows(Resource r, int maxVisibleDepth, TableFilter[] filters)
        {
            int count = 1;
            if(maxVisibleDepth != 2)
            {
                for(Resource c : r.getChildren())
                {
                    if(accept(c, filters))
                    {
                        count += countRows(c, maxVisibleDepth - 1, filters);
                    }
                }
            }
            return count;
        }

        private Comparator getComparator(TableState state)
        {
            TableColumn[] columns = getColumns();
            String sortColumnName = state.getSortColumnName();
            for(TableColumn col : columns)
            {
                if(col.getName().equals(sortColumnName))
                {
                    if(state.getAscSort())
                    {
                        return col.getComparator();
                    }
                    else
                    {
                        return col.getReverseComparator();
                    }
                }
            }
            throw new IllegalStateException("sort column "+sortColumnName+" not found in the model");
        }
        
        private void apply(List<Resource> l, TableFilter[] filters)
        {
            for(TableFilter f : filters)
            {
                Iterator<Resource> i = l.iterator();
                while(i.hasNext())
                {
                    if(!f.accept(i.next()))
                    {
                        i.remove();
                    }
                }
            }
        }
        
        private boolean accept(Resource r, TableFilter[] filters)
        {
            for(TableFilter f : filters)
            {
                if(!f.accept(r))
                {
                    return false;
                }
            }
            return true;
        }
        
        private TableFilter[] addFilter(TableFilter[] in, TableFilter additional)
        {
            TableFilter[] out = new TableFilter[in.length + 1];
            System.arraycopy(in, 0, out, 0, in.length);
            out[in.length] = additional;
            return out;
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
    
    private class ColumnSplitterFilter implements TableFilter
    {        
        private final Collection branchHeaders;
        private final Resource root;

        public ColumnSplitterFilter(Resource root, Collection branchHeaders)
        {
            this.root = root;
            this.branchHeaders = branchHeaders;            
        }

        /**
         * {@inheritDoc}
         */
        public boolean accept(Object object)
        {
            Resource r = (Resource)object;
            while(r != null && !r.equals(root))
            {
                if(branchHeaders.contains(r))
                {
                    return true;
                }
                r = r.getParent();
            }
            return false;
        }        
    }
}
