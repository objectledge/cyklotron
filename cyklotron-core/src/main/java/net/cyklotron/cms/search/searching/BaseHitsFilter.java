package net.cyklotron.cms.search.searching;

import java.util.HashMap;
import net.cyklotron.cms.search.searching.cms.LuceneSearchHit;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.CoralSession;

/**
 * This is a base filter for filtering search results upon their visibility based on branches permission
 * assignments.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseHitsFilter.java,v 1.2 2005-01-18 17:38:19 pablo Exp $
 */
public abstract class BaseHitsFilter implements net.labeo.services.table.TableFilter
{
    private CoralSession resourceService;
    private HashMap branchAccessCache = new HashMap();

    public BaseHitsFilter(CoralSession resourceService)
    {
        this.resourceService = resourceService;
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
                    Resource branch = resourceService.getStore().getResource(Long.parseLong(branchId));
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
