package net.cyklotron.cms.locations.internal;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.NumericUtils;
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

    public List<Location> getAreas(String areaName, String enclosingArea, int lmin, int lmax,
        int limit)
    {
        try
        {
            BooleanQuery query = new BooleanQuery();
            if(provider.getFineGrainedLocationMarker() != null)
            {
                query.add(new TermQuery(provider.getFineGrainedLocationMarker()), Occur.MUST_NOT);
            }
            List<Term> terms = analyze("areaName", areaName);
            if(terms.size() == 1)
            {
                query.add(new PrefixQuery(terms.get(0)), BooleanClause.Occur.SHOULD);
            }
            else
            {
                BooleanQuery prefixQuery = new BooleanQuery();
                for(Term term : terms)
                {
                    query.add(new PrefixQuery(term), BooleanClause.Occur.SHOULD);
                }
                prefixQuery.setBoost(0.25f);
                query.add(prefixQuery, BooleanClause.Occur.SHOULD);
                SpanQuery[] spans = new SpanQuery[terms.size()];
                for(int i = 0; i < spans.length; i++)
                {
                    spans[i] = new SpanTermQuery(terms.get(i));
                }
                Query spanQuery = new SpanNearQuery(spans, 0, true);
                spanQuery.setBoost(0.75f);
                query.add(spanQuery, BooleanClause.Occur.SHOULD);
            }
            if(lmin > 0 || lmax > 0)
            {
                BooleanQuery levelLimit = new BooleanQuery();
                levelLimit.add(query, BooleanClause.Occur.MUST);
                levelLimit.add(NumericRangeQuery.newIntRange("areaLevel", lmin, lmax, true, true),
                    BooleanClause.Occur.MUST);
                query = levelLimit;
            }
            if(enclosingArea.length() > 0)
            {
                BooleanQuery enclosingAreaQuery = new BooleanQuery();
                enclosingAreaQuery.add(query, BooleanClause.Occur.MUST);
                enclosingAreaQuery.add(new PrefixQuery(new Term("terc", enclosingArea)),
                    BooleanClause.Occur.MUST);
                query = enclosingAreaQuery;
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

    public Location merge(Location location1, Location location2)
    {
        return new Location(provider.getFields(), getMatchingEntries(location1, location2));
    }

    /**
     * Return non empty matching fields map
     * 
     * @param location1 Location class
     * @param location2 Location class
     * @return <code>Map<String, String></code>
     * @author lukasz
     */
    private Map<String, String> getMatchingEntries(Location location1, Location location2)
    {
        Map<String, String> matching = new HashMap<String, String>();
        if(location1 != null)
        {
            Iterator<Entry<String, String>> i = location1.iterator();
            if(location2 == null)
            {
                while(i.hasNext())
                {
                    Entry<String, String> e = i.next();
                    if(e.getValue() != null && e.getValue().length() > 0)
                    {
                        matching.put(e.getKey(), e.getValue());
                    }
                }
            }
            else
            {
                while(i.hasNext())
                {
                    Entry<String, String> e = i.next();
                    final String value1 = e.getValue();
                    final String value2 = location2.get(e.getKey());
                    if(value1 != null && value2 != null)
                    {
                        provider.merge(e.getKey(), value1, value2, matching);
                    }
                }
            }
        }
        return matching;
    }

    public Location getExactMatch(String field, String value)
    {
        return search(new TermQuery(new Term(field, value)));
    }

    public Location getExactMatch(Map<String, String> fieldValues)
    {
        BooleanQuery booleanQuery = new BooleanQuery();
        for(Entry<String, String> entry : fieldValues.entrySet())
        {
            final String field = entry.getKey();
            Term term;
            if(provider.getOptions(field).contains(FieldOptions.INTEGER))
            {
                BytesRef bytes = new BytesRef(4);
                NumericUtils.intToPrefixCoded(Integer.parseInt(entry.getValue()), 0, bytes);
                term = new Term(field, bytes);
            }
            else
            {
                term = new Term(field, entry.getValue());
            }
            booleanQuery.add(new BooleanClause(new TermQuery(term), Occur.MUST));
        }
        return search(booleanQuery);
    }

    private Location search(Query query)
    {
        try
        {
            List<Location> locs = results(getSearcher().search(query, 2));
            return locs.size() == 1 ? locs.get(0) : null;
        }
        catch(Exception e)
        {
            logger.error("search error", e);
            return null;
        }
    }
}
