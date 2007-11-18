package net.cyklotron.cms.category.query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.util.ResourceSelectionState;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.internal.CategoryServiceImpl;

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
    
    /**
     * Builds a category query from a ResourceSelectionState.
     * @param coralSession the coral session.
     * @param state categories selection state.
     * @param idsAsIdentifiers if true category resource ids are used instead of paths to build
     *  a query
     * 
     * @throws ProcessingException
     */
    public CategoryQueryBuilder(CoralSession coralSession, ResourceSelectionState state, 
        boolean idsAsIdentifiers)
        throws ProcessingException
    {
        this(
            (Set<CategoryResource>)(state.getEntities(coralSession, "required").keySet()),
            (Set<CategoryResource>)(state.getEntities(coralSession, "optional").keySet()),
            idsAsIdentifiers);
    }

    /**
     * Builds a category query from a CategoryResource sets.
     * @param required the set of required categories.
     * @param optional the set of optional categories.
     * @param idsAsIdentifiers if true category resource ids are used instead of paths to build
     *  a query
     * 
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

        public QueryPart(Set<CategoryResource> categories, String queryOperator, boolean idsAsIdentifiers)
            throws ProcessingException
        {
            StringBuilder queryPartBuffer = new StringBuilder();
            List identifiersList = new ArrayList(categories.size());

            int j = 0;
            for (Iterator<CategoryResource> i = categories.iterator(); i.hasNext(); j++)
            {
                CategoryResource cat = i.next();
                String id = cat.getIdString();

                if (j == 0)
                {
                    queryPartBuffer.append('(');
                }
                else
                {
                    queryPartBuffer.append(' ');
                    queryPartBuffer.append(queryOperator);
                    queryPartBuffer.append(' ');
                }

                String categoryIdentifier = 
                    (idsAsIdentifiers) ? getCategoryId(cat) : getCategoryPath(cat);

                // not: MAP('category.References'){ MAPTRANS('resource.Hierarchy'){ RES(1234) } }
                // but: MAP('category.References'){ RES(1234) }
                // since the parent - child relation is computed by the category resolver
                queryPartBuffer.append("MAP('")
                    .append(CategoryServiceImpl.CATEGORY_RESOURCE_RELATION_NAME)
                    .append("'){ RES(")
                    .append(categoryIdentifier)
                    .append(") }");
                identifiersList.add(categoryIdentifier);
                
                if (!i.hasNext())
                {
                    queryPartBuffer.append(')');
                }
            }

            queryPart = queryPartBuffer.toString();
            
            identifiers = (String[]) identifiersList.toArray(new String[identifiersList.size()]);
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
            return "'"+cat.getPath()+"'";
        }

        private String getCategoryId(CategoryResource cat)
        {
            return cat.getIdString();
        }
    }
}
