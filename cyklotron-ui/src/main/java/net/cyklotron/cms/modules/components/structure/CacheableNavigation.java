package net.cyklotron.cms.modules.components.structure;

import org.objectledge.pipeline.ProcessingException;

/**
 * Base class for cacheable navigation components.
 * Takes care of caching rendered versions of the navigations.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: CacheableNavigation.java,v 1.1 2005-01-24 04:35:20 pablo Exp $
 */

public abstract class CacheableNavigation extends BaseNavigation
{
    // TODO: make a prepare implementation which avoids
    //       row set creation if data is cached.
    
    public String build(RunData data) throws ProcessingException
    {
        // 1. get result from cache
        //    The key used must be proper for the component type, as I wrote before,
        //     Navigations will have a key consisting of:
        //         navigation instance name + selected navigation node id
        
        // 2. if result is null, generate the result using super.build(data)
        //    call
        
        // 3. store the result in cache
        
        String retValue = super.build(data);
        return retValue;
    }
}
