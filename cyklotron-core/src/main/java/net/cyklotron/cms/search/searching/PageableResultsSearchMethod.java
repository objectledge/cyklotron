package net.cyklotron.cms.search.searching;

import java.util.Date;
import java.util.Locale;

import net.cyklotron.cms.search.SearchConstants;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.search.SearchUtil;
import net.labeo.services.table.TableState;
import net.labeo.util.configuration.ParameterContainer;
import net.labeo.webcore.RunData;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeQuery;
import org.apache.lucene.search.SortField;

/**
 * Advanced search method implementation.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: PageableResultsSearchMethod.java,v 1.1 2005-01-12 20:44:40 pablo Exp $
 */
public abstract class PageableResultsSearchMethod extends BaseSearchMethod
{
    public PageableResultsSearchMethod(
        SearchService searchService,
        ParameterContainer parameters,
        Locale locale)
    {
        super(searchService, parameters, locale);
    }

    public void setupTableState(TableState state)
    {
        super.setupTableState(state);
        
        // set the page size initially, remove the parameter afterwards to allow changes using
        // table actions
        if(parameters.get("res_num").isDefined())
        {
            state.setPageSize(parameters.get("res_num").asInt());
            // try to remove from request parameters
            try
            {
                parameters.remove("res_num");
            }
            catch(UnsupportedOperationException e)
            {
                // WARN: silent failure for non implementing classes
            }
        }
    }
}
