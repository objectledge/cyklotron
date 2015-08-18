package net.cyklotron.cms.sitemap.documents;

import java.util.Iterator;

import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.query.QueryResults.Row;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;

import bak.pcj.LongIterator;
import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;

/**
 * A composite iterator over results of an attribute based and category based queries.
 */
public class DocumentSetIterator
    implements Iterator<QueryResults.Row>
{
    private Iterator<Row> coralQueryResults;

    private LongIterator categoryQueryResults;

    private LongSet seen = new LongOpenHashSet();

    private CoralSession coralSession;

    /**
     * Creates a new instance of DocumentSetIterator
     * 
     * @param coralQueryResults results of a attribute
     * @param categoryQueryResults
     * @param coralSession
     */
    public DocumentSetIterator(Iterator<QueryResults.Row> coralQueryResults,
        LongSet categoryQueryResults, CoralSession coralSession)
    {
        this.coralQueryResults = coralQueryResults;
        this.coralSession = coralSession;
        this.categoryQueryResults = categoryQueryResults.iterator();
    }

    @Override
    public boolean hasNext()
    {
        return coralQueryResults.hasNext() || categoryQueryResults.hasNext();
    }

    @Override
    public Row next()
    {
        while(coralQueryResults.hasNext())
        {
            final Row row = coralQueryResults.next();
            if(!seen.contains(row.getId()))
            {
                seen.add(row.getId());
                return row;
            }
        }
        while(categoryQueryResults.hasNext())
        {
            final long id = categoryQueryResults.next();
            if(!seen.contains(id))
            {
                seen.add(id);
                try
                {
                    final Resource res = coralSession.getStore().getResource(id);
                    return new Row()
                        {

                            @Override
                            public Resource get(String name)
                                throws IllegalArgumentException
                            {
                                return res;
                            }

                            @Override
                            public long getId(String name)
                                throws IllegalArgumentException
                            {
                                return id;
                            }

                            @Override
                            public Resource get(int index)
                                throws IndexOutOfBoundsException
                            {
                                return res;
                            }

                            @Override
                            public long getId(int index)
                                throws IndexOutOfBoundsException
                            {
                                return id;
                            }

                            @Override
                            public Resource get()
                                throws IllegalStateException
                            {
                                return res;
                            }

                            @Override
                            public long getId()
                                throws IllegalStateException
                            {
                                return id;
                            }

                            @Override
                            public Resource[] getArray()
                            {
                                return new Resource[] { res };
                            }

                            @Override
                            public long[] getIdArray()
                            {
                                return new long[] { id };
                            }
                        };
                }
                catch(EntityDoesNotExistException e)
                {
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException();
    }

}
