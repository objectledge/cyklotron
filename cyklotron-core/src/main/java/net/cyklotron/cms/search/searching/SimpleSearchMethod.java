package net.cyklotron.cms.search.searching;

import java.util.Locale;

import net.cyklotron.cms.search.SearchService;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.search.Query;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;

/**
 * Simple search method implementation.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SimpleSearchMethod.java,v 1.2 2005-01-19 08:22:56 pablo Exp $
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
            return MultiFieldQueryParser.parse(query, DEFAULT_FIELD_NAMES, analyzer);
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
