package net.cyklotron.cms.search.searching.netsprint;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.cyklotron.cms.search.ExternalPoolResource;
import net.cyklotron.cms.search.SearchConstants;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.search.searching.AdvancedSearchMethod;
import net.cyklotron.cms.search.searching.SearchHandler;
import net.cyklotron.cms.search.searching.SearchMethod;
import net.cyklotron.cms.search.searching.SearchingException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.table.TableException;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableTool;
import org.objectledge.utils.StringUtils;
import org.objectledge.web.HttpContext;

/**
 * SearchHandler implementation for searching using NetSprint search engine.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: NetSprintSearchHandler.java,v 1.2 2005-01-20 06:52:43 pablo Exp $
 */
public class NetSprintSearchHandler implements SearchHandler
{
    boolean initialized;

    private Map fieldNameCms2NetSprint;
    /** search service for getting searchers. */
    private SearchService searchService;
    
    public NetSprintSearchHandler(SearchService searchService)
    {
        this.searchService = searchService;
        fieldNameCms2NetSprint = new HashMap();
        fieldNameCms2NetSprint.put(SearchConstants.FIELD_INDEX_TITLE, "title");
        fieldNameCms2NetSprint.put(SearchConstants.FIELD_INDEX_ABBREVIATION, "description");
        fieldNameCms2NetSprint.put(SearchConstants.FIELD_INDEX_CONTENT, "any");
    }
    
    public TableTool search(CoralSession coralSession, Resource[] searchPools, SearchMethod method, TableState state, List filters,
        Parameters parameters, I18nContext i18nContext)
        throws SearchingException
    {
        // get URL template
          // get external search pool from chosen search pools
        ExternalPoolResource pool = null;
        for(int i=0; i<searchPools.length; i++)
        {
            Resource res = searchPools[i];
            if(res instanceof ExternalPoolResource)
            {
                if(pool == null)
                {
                    pool = (ExternalPoolResource)res;
                }
                else
                {
                    throw new SearchingException("multiple external search pools selected");
                }
            }
        }
          // get URL template
        String url = pool.getUrlTemplate();
        
        // prepare query URL
            // prepare query
        if(method instanceof AdvancedSearchMethod)
        {
            // WARN: ugly hacking
            String qField = parameters.get("field","any");
            if(!qField.equals("any"))
            {
                parameters.set("field",(String)(fieldNameCms2NetSprint.get(qField)));
                method =
                    new AdvancedSearchMethod(searchService, parameters, i18nContext.getLocale());
            }
        }
        // properly encode polish characters
        String query = method.getQueryString(coralSession);
		try
        {
            query = URLEncoder.encode(query, "ISO-8859-2");
        }
        catch (UnsupportedEncodingException e)
        {
        	// should not happen
        }

            // prepare parameters
        int nm = state.getPageSize() == 0 ? 10 : state.getPageSize();
        int st = state.getCurrentPage() == 0 ? 0 : (state.getCurrentPage() - 1) * nm;
        
           // create URL
        Map urlMacros = new HashMap();
        urlMacros.put("${QUERY}", query);
        urlMacros.put("${START_RESULT}", Integer.toString(st));
        urlMacros.put("${PAGE_SIZE}", Integer.toString(nm));
        
        url = StringUtils.expand(url, urlMacros);
          
        // create http client
        // TODO HttpClient object should be pooled and HttpState object should be used
        HttpClient client = new HttpClient();
        HttpMethod httpMethod = new GetMethod(url);
        
        // fetch XML results
        int statusCode = -1;
        try
        {
            statusCode = client.executeMethod(httpMethod);
        }
        catch(Exception e)
        {
            return null;
        }
        
        byte[] responseBody = null;
        // check if the data was properly retrieved
        if(statusCode == 200)
        {
            // get the response body as byte array
            responseBody = httpMethod.getResponseBody();
        }
        else
        {
            return null;
        }
        
        // release the connection.
        httpMethod.releaseConnection();
        
        try
        {
            // parse XML results 
            TableModel model = new NetSprintTableModel(responseBody);
            
            return new TableTool(state, filters, model);
        }
        catch(TableException e)
        {
            throw new SearchingException("problem while creating the table tool", e);
        }
        catch(Exception e)
        {
            // results parsing failed
            return null;
        }
    }
}
