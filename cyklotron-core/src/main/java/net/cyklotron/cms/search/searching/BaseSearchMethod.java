package net.cyklotron.cms.search.searching;

import java.util.Locale;

import net.cyklotron.cms.search.SearchConstants;
import net.cyklotron.cms.search.SearchService;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableState;
import net.labeo.util.configuration.ParameterContainer;
import net.labeo.webcore.RunData;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;

/**
 * Base search method implementation.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseSearchMethod.java,v 1.1 2005-01-12 20:44:40 pablo Exp $
 */
public abstract class BaseSearchMethod implements SearchMethod
{
    public static final String[] DEFAULT_FIELD_NAMES = { SearchConstants.FIELD_INDEX_TITLE,
                            SearchConstants.FIELD_INDEX_ABBREVIATION,
                            SearchConstants.FIELD_INDEX_CONTENT };
    
    protected SearchService searchService;
    protected ParameterContainer parameters;
    protected Locale locale;
                           
    public BaseSearchMethod(
        SearchService searchService,
        ParameterContainer parameters,
        Locale locale)
    {
        this.searchService = searchService;
        this.parameters = parameters;
        this.locale = locale;
    }
                            
    public abstract Query getQuery()
    throws Exception;
    
    public abstract String getQueryString();
    
    public abstract String getErrorQueryString();
    
    public void setupTableState(TableState state)
    {
        if(state.isNew())
        {
            state.setRootId(null);
            state.setViewType(TableConstants.VIEW_AS_LIST);
            state.setPageSize(10);
        }

        // WARN: duplicate setPage action
        if(parameters.get(TableConstants.TABLE_ID_PARAM_KEY).isDefined() &&
           parameters.get(TableConstants.TABLE_ID_PARAM_KEY).asInt() == state.getId())
        {
            state.setCurrentPage( parameters.get(TableConstants.PAGE_NO_PARAM_KEY).asInt(1) );
        }
        else
        {
            state.setCurrentPage(1);
        }
    }
    
    public SortField[] getSortFields()
    {
        String sortField = parameters.get("sort_field").asString("score");
        String sortOrder = parameters.get("sort_order").asString("desc");
        if(!sortField.equals("score"))
        {
            // TODO: Add sort field factory for better caching on multiple searches on multiple indexes
            // TODO: Add sort field type hinting (how?)
            SortField field = new SortField(sortField, sortOrder.equals("desc"));
            return new SortField[] { field };
        }
        else if(sortOrder.equals("asc"))
        {
            SortField field = new SortField((String)null, SortField.SCORE, true);
            return new SortField[] { field };
        }
        return null;
    }
}
