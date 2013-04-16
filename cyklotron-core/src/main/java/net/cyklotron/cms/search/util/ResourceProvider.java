package net.cyklotron.cms.search.util;

import org.objectledge.coral.query.MalformedQueryException;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.session.CoralSessionFactory;

public class ResourceProvider
{

    private final CoralSessionFactory coralSessionFactory;

    private final String resourceName;

    public ResourceProvider(CoralSessionFactory coralSessionFactory, String resourceName)
    {
        this.coralSessionFactory = coralSessionFactory;
        this.resourceName = resourceName;
    }

    /**
     * Execute a query that provides a Resource set.
     * 
     * @return QueryResults
     * @throws IllegalStateException when calling thread does not own an open CoralSession.
     * @throws MalformedQueryException when the executed query is incorrect (eg. null or empty
     *         resourceName)
     */
    public QueryResults runQuery()
        throws IllegalStateException, MalformedQueryException
    {
        return coralSessionFactory.getCurrentSession().getQuery()
            .executeQuery("FIND RESOURCE FROM " + resourceName);
    }
}
