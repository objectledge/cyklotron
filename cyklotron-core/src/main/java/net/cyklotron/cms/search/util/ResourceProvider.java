package net.cyklotron.cms.search.util;

import java.util.Iterator;

import org.objectledge.coral.BackendException;
import org.objectledge.coral.query.MalformedQueryException;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.query.QueryResults.Row;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;

public class ResourceProvider<T extends Resource>
    implements Iterable<T>
{

    private final CoralSessionFactory coralSessionFactory;

    private final String resourceName;

    public ResourceProvider(CoralSessionFactory coralSessionFactory, String resourceName)
    {
        this.coralSessionFactory = coralSessionFactory;
        this.resourceName = resourceName;
    }

    @Override
    public Iterator<T> iterator()
    {
        try(CoralSession coralSession = coralSessionFactory.getRootSession())
        {
            final QueryResults result = coralSession.getQuery().executeQuery(
                "FIND RESOURCE FROM " + resourceName);
            final Iterator<Row> iterator = result.iterator();
            return new Iterator<T>()
                {
                    @Override
                    public void remove()
                    {
                        iterator.remove();
                    }

                    @Override
                    public T next()
                    {
                        Row next = iterator.next();

                        return (T)next.get();
                    }

                    @Override
                    public boolean hasNext()
                    {
                        return iterator.hasNext();
                    }
                };
        }
        catch(MalformedQueryException e)
        {
            throw new BackendException("Malformed query", e);
        }
    }

}
