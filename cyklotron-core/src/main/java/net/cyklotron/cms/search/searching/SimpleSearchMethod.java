package net.cyklotron.cms.search.searching;

import java.util.Locale;

import net.cyklotron.cms.search.SearchService;
import net.labeo.util.configuration.Parameter;
import net.labeo.util.configuration.ParameterContainer;
import net.labeo.webcore.RunData;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;

/**
 * Simple search method implementation.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SimpleSearchMethod.java,v 1.1 2005-01-12 20:44:40 pablo Exp $
 */
public class SimpleSearchMethod extends BaseSearchMethod
{
    protected String query;
    
    public SimpleSearchMethod(
        SearchService searchService,
        ParameterContainer parameters,
        Locale locale)
    {
        super(searchService, parameters, locale);
    }
                            
    public Query getQuery()
    throws Exception
    {
        query = parameters.get("query").asString("");
        if(query.length() > 0)
        {
            Analyzer analyzer = searchService.getAnalyzer(locale);
            return MultiFieldQueryParser.parse(query, DEFAULT_FIELD_NAMES, analyzer);
        }
        else
        {
            return null;
        }
    }
    
    public String getQueryString()
    {
        return query;
    }
    
    public String getErrorQueryString()
    {
        return query;
    }
}
