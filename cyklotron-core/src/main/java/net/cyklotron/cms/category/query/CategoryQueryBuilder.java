package net.cyklotron.cms.category.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.util.ResourceSelectionState;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.internal.CategoryServiceImpl;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

/**
 * A helper class that builds a category query string from category sets.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryBuilder.java,v 1.11 2007-11-18 21:23:10 rafal Exp $
 */
public class CategoryQueryBuilder
{
    private QueryPart requiredPart;

    private QueryPart optionalPart;

    private String query = "";

    public enum Operator
    {
        REQUIRED(" * "), OPTIONAL(" + ");

        private final String operator;

        Operator(String str)
        {
            operator = str;
        }

        String getOperator()
        {
            return operator;
        }
    }

    /**
     * Builds a category query from a ResourceSelectionState.
     * 
     * @param coralSession the coral session.
     * @param state categories selection state.
     * @param idsAsIdentifiers if true category resource ids are used instead of paths to build a
     *        query
     * @throws ProcessingException
     */
    public CategoryQueryBuilder(CoralSession coralSession, ResourceSelectionState state,
        boolean idsAsIdentifiers)
        throws ProcessingException
    {
        this((Set<CategoryResource>)(state.getEntities(coralSession, "required").keySet()),
                        (Set<CategoryResource>)(state.getEntities(coralSession, "optional")
                            .keySet()), idsAsIdentifiers);
    }

    /**
     * Builds a category query from a CategoryResource sets.
     * 
     * @param required the set of required categories.
     * @param optional the set of optional categories.
     * @param idsAsIdentifiers if true category resource ids are used instead of paths to build a
     *        query
     * @throws ProcessingException
     */
    public CategoryQueryBuilder(Set<CategoryResource> required, Set<CategoryResource> optional,
        boolean idsAsIdentifiers)
        throws ProcessingException
    {
        requiredPart = new QueryPart(required, "*", idsAsIdentifiers);
        optionalPart = new QueryPart(optional, "+", idsAsIdentifiers);
        if(requiredPart.length() > 0 && optionalPart.length() > 0)
        {
            query = requiredPart.getPart() + " * " + optionalPart.getPart() + ";";
        }
        else if(requiredPart.length() > 0)
        {
            query = requiredPart.getPart() + ";";
        }
        else if(optionalPart.length() > 0)
        {
            query = optionalPart.getPart() + ";";
        }
    }
    
    /**
     * Builds a category query from a CategoryQueryResource sets.
     * 
     * @param required the set of required category queries.
     * @param optional the set of optional category queries.
     * @throws ProcessingException
     */
    public CategoryQueryBuilder(Set<CategoryQueryResource> required,
        Set<CategoryQueryResource> optional)
        throws ProcessingException
    {
        requiredPart = new QueryPart(required, "*");
        optionalPart = new QueryPart(optional, "+");
        if(requiredPart.length() > 0 && optionalPart.length() > 0)
        {
            query = requiredPart.getPart() + " * " + optionalPart.getPart() + ";";
        }
        else if(requiredPart.length() > 0)
        {
            query = requiredPart.getPart() + ";";
        }
        else if(optionalPart.length() > 0)
        {
            query = optionalPart.getPart() + ";";
        }
    }
    
    public CategoryQueryBuilder(Set<CategoryQueryResource> required,
        List<Set<CategoryQueryResource>> optional)
        throws ProcessingException
    {
        requiredPart = new QueryPart(required, "*");
        List<QueryPart> optionalParts = new ArrayList<QueryPart>();
        for(Set<CategoryQueryResource> opt : optional)
        {
            optionalParts.add(new QueryPart(opt, "+"));
        }
        
        if(requiredPart.length() > 0 && optionalParts.size() > 0)
        {
            query = requiredPart.getPart();
            for(QueryPart optQueryPart : optionalParts)
            {
                query += " * " + optQueryPart.getPart();
            }
            query += ";";
        }
        else if(requiredPart.length() > 0)
        {
            query = requiredPart.getPart() + ";";
        }
        else if(optionalParts.size() > 0)
        {
            for(QueryPart optQueryPart : optionalParts)
            {
                if(query != "")
                {
                    query += " * ";
                }
                query += optQueryPart.getPart();
            }
            query += ";";
        }
    }

    public CategoryQueryBuilder(List<Set<String>> optionalsList)
        throws ProcessingException
    {
        StringBuilder query = new StringBuilder();
        final Iterator<Set<String>> iterator = optionalsList.iterator();
        while(iterator.hasNext())
        {
            final Set<String> optional = iterator.next();
            final QueryPart queryPart = new QueryPart("+", optional);
            query.append(queryPart.getPart());
            if(iterator.hasNext())
            {
                query.append(" * ");
            }
            else
            {
                query.append(";");
            }
        }
        this.query = query.toString();
    }

    private static String joinParts(List<String> queries, String operator, boolean close)
    {
        StringBuilder query = new StringBuilder();
        final Iterator<String> iterator = queries.iterator();
        while(iterator.hasNext())
        {
            final String queryPart = iterator.next();
            query.append(queryPart);
            if(iterator.hasNext())
            {
                query.append(operator);
            }
            else if(close)
            {
                query.append(";");
            }
        }
        return query.toString();
    }
    
    public CategoryQueryBuilder()
    {
    }

    public String createQueryPart(Set<String> categoriesIds, Operator operator)
        throws ProcessingException
    {
        return new QueryPart(operator.getOperator(), categoriesIds).getPart();
    }

    public static String joinPartsLeaveOpen(List<String> queryParts, Operator operator)
    {
        return joinParts(queryParts, operator.getOperator(), false);
    }

    public static String joinPartsAndClose(List<String> queryParts, Operator operator)
    {
        return joinParts(queryParts, operator.getOperator(), true);
    }

    /**
     * Returns query body.
     * 
     * @return query body.
     */
    public String getQuery()
    {
        return query;
    }

    /**
     * Returns identifiers of the required categories.
     * 
     * @return identifiers of the required categories.
     */
    public String[] getRequiredIdentifiers()
    {
        return requiredPart.getIdentifiers();
    }

    /**
     * Returns identifiers of the optional categories.
     * 
     * @return identifiers of the optional categories.
     */
    public String[] getOptionalIdentifiers()
    {
        return optionalPart.getIdentifiers();
    }

    private class QueryPart
    {
        public String[] identifiers;

        public String queryPart;

        public QueryPart(Set<CategoryResource> categories, String queryOperator,
            final boolean idsAsIdentifiers)
            throws ProcessingException
        {
            StringBuilder queryPartBuffer = new StringBuilder();
            List<String> identifiersList = new ArrayList<>(categories.size());

            final Collection<String> ids = Collections2.transform(categories,
                new Function<CategoryResource, String>()
                    {
                        @Override
                        public String apply(CategoryResource categoryResource)
                        {
                            return (idsAsIdentifiers) ? getCategoryId(categoryResource)
                                : getCategoryPath(categoryResource);
                        }
                    });

            buildQueryPart(queryOperator, ids.iterator(), queryPartBuffer, identifiersList);
        }

        public QueryPart(String queryOperator, Set<String> categoriesIds)
            throws ProcessingException
        {
            StringBuilder queryPartBuffer = new StringBuilder();
            List<String> identifiersList = new ArrayList<>(categoriesIds.size());

            buildQueryPart(queryOperator, categoriesIds.iterator(), queryPartBuffer,
                identifiersList);
        }

        private void buildQueryPart(String queryOperator, Iterator<String> i,
            StringBuilder queryPartBuffer, List<String> identifiersList)
        {
            int j = 0;
            for(; i.hasNext(); j++)
            {
                String categoryIdentifier = i.next();

                if(j == 0)
                {
                    queryPartBuffer.append('(');
                }
                else
                {
                    queryPartBuffer.append(' ');
                    queryPartBuffer.append(queryOperator);
                    queryPartBuffer.append(' ');
                }

                // not: MAP('category.References'){ MAPTRANS('resource.Hierarchy'){ RES(1234) } }
                // but: MAP('category.References'){ RES(1234) }
                // since the parent - child relation is computed by the category resolver
                queryPartBuffer.append("MAP('")
                    .append(CategoryServiceImpl.CATEGORY_RESOURCE_RELATION_NAME).append("'){ RES(")
                    .append(categoryIdentifier).append(") }");
                identifiersList.add(categoryIdentifier);

                if(!i.hasNext())
                {
                    queryPartBuffer.append(')');
                }
            }

            queryPart = queryPartBuffer.toString();

            identifiers = (String[])identifiersList.toArray(new String[identifiersList.size()]);
        }

        /**
         * Query Parts from queries.
         * 
         * @param queries
         * @param queryOperator
         * @throws ProcessingException
         */

        public QueryPart(Set<CategoryQueryResource> queries, String queryOperator)
            throws ProcessingException
        {
            StringBuilder queryPartBuffer = new StringBuilder();
            List identifiersList = new ArrayList(queries.size());

            int j = 0;
            for(Iterator<CategoryQueryResource> i = queries.iterator(); i.hasNext(); j++)
            {
                CategoryQueryResource query = i.next();

                if(j == 0)
                {
                    queryPartBuffer.append('(');
                }
                else
                {
                    queryPartBuffer.append(' ');
                    queryPartBuffer.append(queryOperator);
                    queryPartBuffer.append(' ');
                }
                queryPartBuffer.append(query.getQuery().replace(";", ""));

                if(!i.hasNext())
                {
                    queryPartBuffer.append(')');
                }

                identifiersList.add(query.getOptionalCategoryIdentifiers());
                identifiersList.add(query.getRequiredCategoryIdentifiers());
            }

            queryPart = queryPartBuffer.toString();
            identifiers = (String[])identifiersList.toArray(new String[identifiersList.size()]);
        }

        public String getPart()
        {
            return queryPart;
        }

        public String[] getIdentifiers()
        {
            return identifiers;
        }

        public int length()
        {
            return identifiers.length;
        }

        private String getCategoryPath(CategoryResource cat)
        {
            return "'" + cat.getPath() + "'";
        }

        private String getCategoryId(CategoryResource cat)
        {
            return cat.getIdString();
        }
    }
}
