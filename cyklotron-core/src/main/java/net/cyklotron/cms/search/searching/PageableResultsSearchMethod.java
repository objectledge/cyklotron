package net.cyklotron.cms.search.searching;

import java.util.Locale;

import org.objectledge.parameters.Parameters;
import org.objectledge.table.TableState;

import net.cyklotron.cms.search.SearchService;

/**
 * Advanced search method implementation.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: PageableResultsSearchMethod.java,v 1.4 2005-02-09 22:20:46 rafal Exp $
 */
public abstract class PageableResultsSearchMethod extends BaseSearchMethod
{
    public PageableResultsSearchMethod(
        SearchService searchService,
        Parameters parameters,
        Locale locale)
    {
        super(searchService, parameters, locale);
    }

    public void setupTableState(TableState state)
    {
        super.setupTableState(state);
        
        // set the page size initially, remove the parameter afterwards to allow changes using
        // table actions
        if(parameters.isDefined("res_num"))
        {
            state.setPageSize(parameters.getInt("res_num"));
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
