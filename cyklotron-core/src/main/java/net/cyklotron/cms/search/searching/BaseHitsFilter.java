package net.cyklotron.cms.search.searching;

import java.util.HashMap;

import net.cyklotron.cms.search.searching.cms.LuceneSearchHit;

import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.table.TableFilter;

/**
 * This is a base filter for filtering search results upon their visibility based on branches permission
 * assignments.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseHitsFilter.java,v 1.4 2005-01-20 06:52:40 pablo Exp $
 */
public abstract class BaseHitsFilter 
    implements TableFilter
{
    private HashMap branchAccessCache = new HashMap();

    protected Context context;
    
    public BaseHitsFilter(Context context)
    {
        this.context = context;
    }

    public boolean accept(Object object)
    {
        SearchHit hit = (SearchHit)object;
        if(hit instanceof LuceneSearchHit)
        {
            String branchId = hit.get("branch_id");
            if(!branchAccessCache.containsKey(branchId))
            {
                try
                {
                    CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
                    Resource branch = coralSession.getStore().getResource(Long.parseLong(branchId));
                    branchAccessCache.put(branchId, new Boolean( checkAccess(branch) ));
                }
                catch(EntityDoesNotExistException e)
                {
                    // WARN: Maybe we should log it!!
                    branchAccessCache.put(branchId, new Boolean(false));
                }
            }
            return ((Boolean)(branchAccessCache.get(branchId))).booleanValue();
        }
        else
        {
            return true;
        }
    }
    
    public abstract boolean checkAccess(Resource branch);
}
