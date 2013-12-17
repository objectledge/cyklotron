package net.cyklotron.cms.sitemap;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.query.QueryResults.Row;
import org.objectledge.coral.store.Resource;

public abstract class SitemapResourceIterator<T extends Resource>
    implements Iterator<SitemapItem>
{
    private final Iterator<Row> rowIterator;

    private final Logger log;

    private final Class<T> repr;

    public SitemapResourceIterator(Iterator<QueryResults.Row> rowIterator, Class<T> repr, Logger log)
    {
        this.rowIterator = rowIterator;
        this.repr = repr;
        this.log = log;
    }

    private SitemapItem next;

    private boolean done = false;

    protected abstract SitemapItem item(T doc)
        throws RetrievalException;

    protected SitemapItem fetchNext()
    {
        while(rowIterator.hasNext())
        {
            Resource res = rowIterator.next().get();
            try
            {
                SitemapItem item = item(repr.cast(res));
                if(item != null)
                {
                    return item;
                }
            }
            catch(RetrievalException e)
            {
                log.error("failed to generate site map item for resource #" + res.getIdString());
            }
        }
        done = true;
        return null;
    }

    @Override
    public boolean hasNext()
    {
        if(next == null && !done)
        {
            next = fetchNext();
        }
        return next != null;
    }

    @Override
    public SitemapItem next()
    {
        if(next == null && !done)
        {
            next = fetchNext();
        }
        if(next == null)
        {
            throw new NoSuchElementException();
        }
        else
        {
            SitemapItem tmp = next;
            next = null;
            return tmp;
        }
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}
