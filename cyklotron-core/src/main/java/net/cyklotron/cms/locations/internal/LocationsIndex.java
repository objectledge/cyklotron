package net.cyklotron.cms.locations.internal;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.TermQuery;
import org.jcontainer.dna.Logger;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.utils.Timer;

import net.cyklotron.cms.locations.Location;
import net.cyklotron.cms.locations.LocationsProvider;
import net.cyklotron.cms.locations.LocationsProvider.FieldOptions;
import net.cyklotron.cms.search.util.AbstractIndex;

public class LocationsIndex
    extends AbstractIndex<Location>
{
    private static final String INDEX_PATH = "ngo/locations/index";

    private static final int MAX_RESULTS = 200000;

    private final LocationsProvider provider;

    public LocationsIndex(LocationsProvider provider, FileSystem fileSystem, Logger logger)
        throws IOException
    {
        super(fileSystem, logger, INDEX_PATH);
        this.provider = provider;
    }

    @Override
    protected Document toDocument(Location item)
    {
        Document document = new Document();
        for(String field : provider.getFields())
        {
            Set<FieldOptions> options = provider.getOptions(field);
            final String value = item.get(field);
            if(value != null)
            {
                if(options.contains(FieldOptions.INTEGER))
                {
                    document.add(new IntField(field, Integer.parseInt(value), Field.Store.YES));
                }
                else
                {
                    document.add(new Field(field, value, Field.Store.YES, options
                        .contains(FieldOptions.NOT_ANALYZED) ? Field.Index.NOT_ANALYZED
                        : Field.Index.ANALYZED));
                }
            }
        }
        return document;
    }

    @Override
    protected Location fromDocument(Document doc)
    {
        Map<String, String> entries = new HashMap<>();
        for(String field : provider.getFields())
        {
            entries.put(field, doc.get(field));
        }
        return new Location(provider.getFields(), entries);
    }

    /**
     * Searches for locations with specified characteristics.
     * <p>
     * The field selected with requestedField parameter is searched using prefix match. All other
     * fields are either matched exactly, or ignored when empty.
     * </p>
     * <p>
     * This method logs and quenches IOExceptions and returns empty results, when index access
     * problems occur.
     * </p>
     * 
     * @param requestedField one of "province", "district", "commune", "city", "area", "street",
     *        "postCode".
     * @param province user supplied value.
     * @param district user supplied value.
     * @param commune user supplied value.
     * @param city user supplied value.
     * @param area user supplied value.
     * @param street user supplied value.
     * @param postCode user supplied value.
     * @return list of locations sorted by relevance.
     */
    public List<Location> getLocations(String requestedField, Map<String, String> fieldValues)
    {
        try
        {
            BooleanQuery query = new BooleanQuery();
            for(String field : provider.getFields())
            {
                addClause(query, requestedField, field, fieldValues.get(field));
            }
            if(provider.getFineGrainedLocationMarker() != null)
            {
                query.add(new TermQuery(provider.getFineGrainedLocationMarker()), Occur.MUST);
            }
            Timer timer = new Timer();
            List<Location> results = results(getSearcher().search(query, MAX_RESULTS));
            logger.debug("query: " + query.toString() + " " + results.size() + " in "
                + timer.getElapsedMillis() + "ms");
            return results;
        }
        catch(Exception e)
        {
            logger.error("search error", e);
            return Collections.emptyList();
        }
    }

    public List<Location> getAreas(String areaName, int limit)
    {
        try
        {
            BooleanQuery query = new BooleanQuery();
            if(provider.getFineGrainedLocationMarker() != null)
            {
                query.add(new TermQuery(provider.getFineGrainedLocationMarker()), Occur.MUST_NOT);
            }
            List<Term> terms = analyze("areaName", areaName);
            for(Term term : terms)
            {
                query.add(new PrefixQuery(term), BooleanClause.Occur.SHOULD);
            }
            Timer timer = new Timer();
            List<Location> results;
            if(provider.getCoarseGrainedLocationSort() != null)
            {
                results = results(getSearcher().search(query, Math.min(MAX_RESULTS, limit),
                    provider.getCoarseGrainedLocationSort()));
            }
            else
            {
                results = results(getSearcher().search(query, Math.min(MAX_RESULTS, limit)));
            }
            logger.debug("query: " + query.toString() + " " + results.size() + " in "
                + timer.getElapsedMillis() + "ms");
            return results;
        }
        catch(Exception e)
        {
            logger.error("search error", e);
            return Collections.emptyList();
        }
    }

    private void addClause(BooleanQuery query, String requestedField, String field, String value)
        throws IOException
    {
        if(value != null && value.length() > 0)
        {
            List<Term> terms;
            if(provider.getOptions(field).contains(FieldOptions.NOT_ANALYZED))
            {
                terms = Collections.singletonList(new Term(field, value));
            }
            else
            {
                terms = analyze(field, value);
            }
            if(provider.getOptions(field).contains(FieldOptions.MULTI_TERM_SUBQUERY))
            {
                BooleanQuery subQuery = new BooleanQuery();
                for(Term term : terms)
                {
                    subQuery.add(field.equals(requestedField) ? new PrefixQuery(term)
                        : new TermQuery(term), BooleanClause.Occur.SHOULD);
                }
                query.add(subQuery, BooleanClause.Occur.MUST);
            }
            else
            {
                for(Term term : terms)
                {
                    query.add(field.equals(requestedField) ? new PrefixQuery(term) : new TermQuery(
                        term), BooleanClause.Occur.MUST);
                }
            }
        }
    }
}
