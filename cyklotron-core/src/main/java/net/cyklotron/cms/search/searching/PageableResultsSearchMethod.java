package net.cyklotron.cms.search.searching;

import java.util.Locale;

import net.cyklotron.cms.search.SearchService;

import org.objectledge.table.TableState;

/**
 * Advanced search method implementation.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: PageableResultsSearchMethod.java,v 1.2 2005-01-13 11:46:34 pablo Exp $
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
