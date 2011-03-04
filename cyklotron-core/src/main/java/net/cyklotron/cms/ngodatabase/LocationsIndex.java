package net.cyklotron.cms.ngodatabase;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.jcontainer.dna.Logger;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.utils.Timer;

public class LocationsIndex
    extends AbstractIndex<Location>
{
    private static final String INDEX_PATH = "ngo/locations/index";

    private static final int MAX_RESULTS = 200000;

    public LocationsIndex(FileSystem fileSystem, Logger logger)
        throws IOException
    {
        super(fileSystem, logger, INDEX_PATH);
    }

    @Override
    protected Document toDocument(Location item)
    {
        Document document = new Document();
        document.add(new Field("province", item.getProvince(), Field.Store.YES,
            Field.Index.ANALYZED));
        document.add(new Field("city", item.getCity(), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("street", item.getStreet(), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("postCode", item.getPostCode(), Field.Store.YES,
            Field.Index.NOT_ANALYZED));
        return document;
    }

    @Override
    protected Location fromDocument(Document doc)
    {
        String province = doc.get("province");
        String city = doc.get("city");
        String street = doc.get("street");
        String postCode = doc.get("postCode");
        return new Location(province, city, street, postCode);
    }

    /**
     * Searches for locations with specified characteristics.
     * <p>
     * The field selected with requestedField parameter is searched using prefix match. All other
     * fields are either matched exactly, or ignored when empty.
     * </p>
     * <p>
     * This method logs and quenches IOExceptions and returns empty results, when index acess
     * problems occur.
     * </p>
     * 
     * @param requestedField one of "province", "city", "street", "postCode".
     * @param province user supplied value.
     * @param city user supplied value.
     * @param street user supplied value.
     * @param postCode user supplied value.
     * @return list of locations sorted by relevance.
     */
    public List<Location> getLocations(String requestedField, String province, String city,
        String street, String postCode)
    {
        try
        {
            BooleanQuery query = new BooleanQuery();
            addClause(query, requestedField, "province", province, false);
            addClause(query, requestedField, "city", city, false);
            addClause(query, requestedField, "street", street, true);
            addClause(query, requestedField, "postCode", postCode, false);
            Timer timer = new Timer();
            List<Location> results = results(getSearcher().search(query, MAX_RESULTS));
            logger.debug("query: " + query.toString() + " " + results.size() + " in "
                + timer.getElapsedMillis() + "ms");
            return results;
        }
        catch(IOException e)
        {
            logger.error("search error", e);
            return Collections.emptyList();
        }
    }

    private void addClause(BooleanQuery query, String requestedField, String field, String value, boolean useSubquery)
        throws IOException
    {
        if(field.equals(requestedField))
        {
            if(value.length() > 0)
            {
                List<Term> terms = analyze(field, value);
                if(useSubquery)
                {
                    BooleanQuery subQuery = new BooleanQuery();
                    for(Term term : terms)
                    {
                        subQuery.add(new PrefixQuery(term), BooleanClause.Occur.SHOULD);
                    }
                    query.add(subQuery, BooleanClause.Occur.MUST);
                }
                else
                {
                    for(Term term : terms)
                    {
                        query.add(new PrefixQuery(term), BooleanClause.Occur.MUST);
                    }
                }
            }
        }
        else
        {
            if(value.length() > 0)
            {
                List<Term> terms = analyze(field, value);
                if(useSubquery)
                {
                    BooleanQuery subQuery = new BooleanQuery();
                    for(Term term : terms)
                    {
                        subQuery.add(new TermQuery(term), BooleanClause.Occur.SHOULD);
                    }
                    query.add(subQuery, BooleanClause.Occur.MUST);
                }
                else
                {
                    for(Term term : terms)
                    {
                        query.add(new TermQuery(term), BooleanClause.Occur.MUST);
                    }
                }
            }
        }
    }
}
