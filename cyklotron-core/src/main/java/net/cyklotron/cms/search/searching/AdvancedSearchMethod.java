package net.cyklotron.cms.search.searching;

import java.util.Date;
import java.util.Locale;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.util.Version;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;

import net.cyklotron.cms.search.SearchConstants;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.search.SearchUtil;

/**
 * Advanced search method implementation.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: AdvancedSearchMethod.java,v 1.8 2007-01-31 00:18:44 rafal Exp $
 */
public class AdvancedSearchMethod extends PageableResultsSearchMethod
{
    /**
     * Maximum number of clauses in a query. Date range queries use quite a few.
     */
    private static final int MAX_CALUSE_COUNT = 10240;

    public AdvancedSearchMethod(
        SearchService searchService,
        Parameters parameters,
        Locale locale)
    {
        super(searchService, parameters, locale);
    }

    @Override
    public Query getQuery(CoralSession coralSession)
    throws Exception
    {
        String[] fieldNames = DEFAULT_FIELD_NAMES;
        String qField = parameters.get("field","any");
        if(!qField.equals("any"))
        {
            fieldNames = new String[1];
            fieldNames[0] = qField;
        }

        return getQuery(fieldNames);
    }
    
    @Override
    public String getQueryString(CoralSession coralSession)
    {
        String[] fieldNames = new String[1];
        fieldNames[0] = "";
        String qField = parameters.get("field","any");
        if(!qField.equals("any"))
        {
            fieldNames = new String[1];
            fieldNames[0] = qField;
        }
        try
        {
            Query query = getQuery(fieldNames);
            return query.toString();
        }
        catch(Exception e)
        {
            return "";
        }
    }

    private Query getQuery(String[] fieldNames)
    throws Exception
    {
        Analyzer analyzer = searchService.getAnalyzer(locale);
        BooleanQuery.setMaxClauseCount(MAX_CALUSE_COUNT);
        BooleanQuery aQuery = new BooleanQuery();

        String qAnd = parameters.get("q_and","");
        if(qAnd.length() > 0)
        {
            for(String fieldName : fieldNames)
            {
                QueryParser parser = new QueryParser(Version.LUCENE_30, fieldName, analyzer);
                parser.setDateResolution(DateTools.Resolution.SECOND);
                Query q = parser.parse(qAnd);
                makeAllRequired(q);
                aQuery.add(q, BooleanClause.Occur.SHOULD);
            }
        }

        QueryParser parser = new MultiFieldQueryParser(Version.LUCENE_30, fieldNames, analyzer);
        parser.setDateResolution(DateTools.Resolution.SECOND);
        String qExpr = parameters.get("q_expr","");
        if(qExpr.length() > 0)
        {
            aQuery.add(parser.parse("\""+qExpr+"\""), BooleanClause.Occur.MUST);
        }

        String qOr = parameters.get("q_or","");
        if(qOr.length() > 0)
        {
            aQuery.add(parser.parse(qOr), BooleanClause.Occur.MUST);
        }
        
        String qNot = parameters.get("q_not","");
        if(qNot.length() > 0)
        {
            aQuery.add(parser.parse(qNot), BooleanClause.Occur.MUST_NOT);
        }
        
        BooleanQuery outQuery = new BooleanQuery();
        outQuery.add(aQuery, BooleanClause.Occur.MUST);
        
        String qTime = parameters.get("q_time","all");
        BooleanClause clause = getDateRangeClause(SearchConstants.FIELD_MODIFICATION_TIME, qTime);
        if(clause != null)
        {
            outQuery.add(clause);
        }

        String vTime = parameters.get("v_time","all");
        clause = getDateRangeClause("validity_start", vTime);
        if(clause != null)
        {
            outQuery.add(clause);
        }

        return outQuery;
    }

    private void makeAllRequired(Query query)
    {
        if(query instanceof BooleanQuery)
        {
            BooleanQuery bQuery = (BooleanQuery)query;
            for(BooleanClause clause : bQuery.getClauses())
            {
                clause.setOccur(BooleanClause.Occur.MUST);
                makeAllRequired(clause.getQuery());
            }
        }
    }

    private BooleanClause getDateRangeClause(String fieldName, String paramValue)
    {
        BooleanClause clause = null;
        if(!paramValue.equals("all"))
        {
            long days = Long.parseLong(paramValue);
            Date date = new Date(System.currentTimeMillis() - (days * 1000L * 60L * 60L * 24L));
            Term lowerDate = new Term(fieldName, SearchUtil.dateToString(date));
            
            TermRangeQuery dateRange = new TermRangeQuery(fieldName, lowerDate.text(),
                null, true, false);
            clause = new BooleanClause(dateRange, BooleanClause.Occur.MUST);
        }
        return clause;
    }
    
    @Override
    public String getErrorQueryString()
    {
        return "";
    }
}
