package net.cyklotron.cms.category.query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.cyklotron.cms.category.CategoryResource;

import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.util.ResourceSelectionState;
import org.objectledge.pipeline.ProcessingException;

/**
 * A helper class that builds a category query from a ResourceSelectionState.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryBuilder.java,v 1.3 2005-01-18 17:38:20 pablo Exp $ 
 */
public class CategoryQueryBuilder
{
    private QueryPart requiredPart;
    
    private QueryPart optionalPart;
    
    private String query = "";
    
    /**
     * Builds a category query from a ResourceSelectionState.
     * @param resourceService the resource service.
     * @param state categories selection state.
     * @param idsAsIdentifiers if true category resource ids are used instead of paths to build a query
     * 
     * @throws ProcessingException
     */
    public CategoryQueryBuilder(CoralSession resourceService, ResourceSelectionState state, 
        boolean idsAsIdentifiers)
        throws ProcessingException
    {
        requiredPart = new QueryPart(resourceService, state, "required", "AND", idsAsIdentifiers);
        optionalPart = new QueryPart(resourceService, state, "optional", "OR", idsAsIdentifiers);
        if(requiredPart.length() > 0 && optionalPart.length() > 0)
        {
            query = requiredPart.getPart() + " AND " + optionalPart.getPart() + ";";
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
     * @return
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

        public QueryPart(
        	CoralSession resourceService,
        	ResourceSelectionState state,
        	String selectionState,
        	String queryOperator, boolean idsAsIdentifiers)
            throws ProcessingException
        {
            Map temp = state.getResources(resourceService, selectionState);
            Set categories = temp.keySet();

            StringBuffer queryPartBuffer = new StringBuffer();
            List identifiersList = new ArrayList(categories.size());

            int j = 0;
            for (Iterator i = categories.iterator(); i.hasNext(); j++)
            {
                CategoryResource cat = (CategoryResource) (i.next());
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
                queryPartBuffer.append(categoryIdentifier);
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
            // TODO: Add slash unescaping to allow slashes in category names (???) 
            return "'"+cat.getPath()+"'";
        }

        private String getCategoryId(CategoryResource cat)
        {
            return cat.getIdString();
        }
    }
}
