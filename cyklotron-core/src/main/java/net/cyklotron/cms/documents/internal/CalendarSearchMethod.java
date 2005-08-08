package net.cyklotron.cms.documents.internal;

import java.util.Date;
import java.util.Locale;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeQuery;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.table.TableState;

import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.search.SearchConstants;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.search.SearchUtil;
import net.cyklotron.cms.search.searching.PageableResultsSearchMethod;

/**
 * Calendar search method implementation.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CalendarSearchMethod.java,v 1.6.2.1 2005-08-08 08:18:23 rafal Exp $
 */
public class CalendarSearchMethod extends PageableResultsSearchMethod
{
    private Logger log;
    private Date startDate;
    private Date endDate;
    
    private String[] fieldNames;
    private Query query;
    
    public CalendarSearchMethod(
        SearchService searchService,
        Parameters parameters,
        Locale locale,
        Logger log,
        Date startDate,
        Date endDate)
    {
        super(searchService, parameters, locale);
        this.startDate = startDate;
        this.endDate = endDate;
        this.log = log;
    }

    public Query getQuery(CoralSession coralSession)
    throws Exception
    {
        return getQuery(coralSession, getFieldNames());
    }
    
    public String getQueryString(CoralSession coralSession)
    {
    	try
    	{
        	Query query = getQuery(coralSession, getFieldNames());
        	return query.toString();
    	}
    	catch(Exception e)
    	{
    		return "";
    	}
    }

    private String[] getFieldNames()
    {
        if(fieldNames == null)
        {
            fieldNames = DEFAULT_FIELD_NAMES;
            String qField = parameters.get("field","any");
            if(!qField.equals("any"))
            {
                fieldNames = new String[1];
                fieldNames[0] = qField;
            }
        }
        return fieldNames;
    }
    
    public void setupTableState(TableState state)
    {
        super.setupTableState(state);
        // block page changes ??
        //state.setCurrentPage(1);
    }

    private Query getQuery(CoralSession coralSession, String[] fieldNames)
 	   throws Exception
    {
        if(query == null)
        {
    		long firstCatId = parameters.getLong("category_id_1",-1);
    		long secondCatId = parameters.getLong("category_id_2",-1);
    		long[] categoriesIds = new long[]{firstCatId, secondCatId};
    		String range = parameters.get("range","all");
		
		    query = getQuery(coralSession, startDate, endDate, range, categoriesIds);
        }
        return query;
    }
    
    private Query getQuery(CoralSession coralSession, Date startDate, Date endDate, String range, long[] categoriesIds)
        throws Exception
    {
        Analyzer analyzer = searchService.getAnalyzer(locale);
        BooleanQuery aQuery = new BooleanQuery();
        
        Term lowerEndDate = new Term("event_end", SearchUtil.dateToString(startDate));
        Term upperStartDate = new Term("event_start", SearchUtil.dateToString(endDate));
        Term lowerStartDate = new Term("event_start", SearchUtil.dateToString(startDate));
        Term upperEndDate = new Term("event_end", SearchUtil.dateToString(endDate));

        if(range.equals("all"))
        {
            CalendarAllRangeQuery calQuery = new CalendarAllRangeQuery(log, startDate, endDate);
            aQuery.add(new BooleanClause(calQuery, true, false));
        }
        else
        if(range.equals("in"))
        {
            RangeQuery dateRange = new RangeQuery(lowerEndDate, upperEndDate, true);
            RangeQuery dateRange2 = new RangeQuery(lowerStartDate, upperStartDate, true);

            aQuery.add(new BooleanClause(dateRange, true, false));
            aQuery.add(new BooleanClause(dateRange2, true, false)); 
        }
        else
        if(range.equals("ending"))
        {
            RangeQuery dateRange = new RangeQuery(lowerEndDate, upperEndDate, true);

            aQuery.add(new BooleanClause(dateRange, true, false));
        }
        else
        if(range.equals("starting"))
        {
            RangeQuery dateRange2 = new RangeQuery(lowerStartDate, upperStartDate, true);

            aQuery.add(new BooleanClause(dateRange2, true, false)); 
        }   
        
        for(int i = 0; i < categoriesIds.length; i++)
        {
            if(categoriesIds[i] != -1)
            {
                Resource category = coralSession.getStore().getResource(categoriesIds[i]);
                Query categoryQuery = getQueryForCategory(coralSession, category);
                aQuery.add(new BooleanClause(categoryQuery, true, false));
            }
        }
        aQuery.add(new BooleanClause(new TermQuery(new Term("title_calendar", DocumentNodeResource.EMPTY_TITLE)),false,true));
        return aQuery;
    }

    public String getErrorQueryString()
    {
        return "";
    }
    
    private Query getQueryForCategory(CoralSession coralSession, Resource category)
    {
        BooleanQuery query = new BooleanQuery();
        addQueriesForCategories(coralSession, query, category);
        return query;
    }

    private void addQueriesForCategories(CoralSession coralSession, BooleanQuery query, Resource parentCategory)
    {
        TermQuery oneCategoryQuery = 
            new TermQuery(new Term(SearchConstants.FIELD_CATEGORY, parentCategory.getPath()));
        query.add(new BooleanClause(oneCategoryQuery, false, false));
        
        Resource[] children = coralSession.getStore().getResource(parentCategory);
        for (int i = 0; i < children.length; i++)
        {
            addQueriesForCategories(coralSession, query, children[i]);
        }
    }
    
    public SortField[] getSortFields()
    {
        if(parameters.isDefined("sort_field") && 
           parameters.isDefined("sort_order"))
        {
            return super.getSortFields();
        }
        else
        {
            //SortField field = new SortField("eventStart", "desc".equals("desc"));
            SortField field2= new SortField(SearchConstants.FIELD_ID, "desc".equals("desc"));
            return new SortField[] { field2};
        }
    }
}
