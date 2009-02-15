package net.cyklotron.cms.search.searching;

import java.util.Locale;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;

import net.cyklotron.cms.search.SearchService;

/**
 * Simple search method implementation.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SimpleSearchMethod.java,v 1.4 2007-01-30 23:55:02 rafal Exp $
 */
public class SimpleSearchMethod extends BaseSearchMethod
{
    protected String query;
    
    public SimpleSearchMethod(
        SearchService searchService,
        Parameters parameters,
        Locale locale)
    {
        super(searchService, parameters, locale);
    }
                            
    public Query getQuery(CoralSession coralSession)
    throws Exception
    {
        query = parameters.get("query","");
        if(query.length() > 0)
        {
            Analyzer analyzer = searchService.getAnalyzer(locale);
            QueryParser parser = new MultiFieldQueryParser(DEFAULT_FIELD_NAMES, analyzer); 
            return parser.parse(query);
        }
        else
        {
            return null;
        }
    }
    
    public String getQueryString(CoralSession coralSessio)
    {
        return query;
    }
    
    public String getErrorQueryString()
    {
        return query;
    }
}
