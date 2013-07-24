package net.cyklotron.cms.search.util;

import org.objectledge.coral.query.MalformedQueryException;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.session.CoralSessionFactory;

public class ResourceProvider
{

    private final CoralSessionFactory coralSessionFactory;

    private final String resourceName;

    private final String predicate;

    public ResourceProvider(CoralSessionFactory coralSessionFactory, String resourceName, String predicate)
    {
        this.coralSessionFactory = coralSessionFactory;
        this.resourceName = resourceName;
        this.predicate = predicate;
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
        StringBuilder query = new StringBuilder();
        query.append("FIND RESOURCE FROM ").append(resourceName);
        if(predicate != null)
        {
            query.append(" WHERE ").append(predicate);
        }
        return coralSessionFactory.getCurrentSession().getQuery().executeQuery(query.toString());
    }
}
