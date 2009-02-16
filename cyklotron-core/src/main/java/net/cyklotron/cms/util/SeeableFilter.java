package net.cyklotron.cms.util;

import org.objectledge.coral.store.Resource;
import org.objectledge.table.TableFilter;

import net.cyklotron.cms.SeeableResource;

/**
 * A filter that accepts any resource unless it is descended from 'seeable' class an it's 'hidden' attribute is set to true.
 * 
 * @author rafal
 */
public class SeeableFilter implements TableFilter<Resource>
{
    public boolean accept(Resource r)
    {
        if(r instanceof SeeableResource)
        {
            SeeableResource sr = (SeeableResource)r;
            return !sr.getHidden(false);
        }
        // resources not descended from seeable are accepted by this filter by default
        return true;
    }
}
