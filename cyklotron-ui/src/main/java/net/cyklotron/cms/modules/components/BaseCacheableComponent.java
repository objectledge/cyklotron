package net.cyklotron.cms.modules.components;

import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.preferences.PreferencesService;

/**
 */
public abstract class BaseCacheableComponent
    extends BaseCMSComponent
{
    protected PreferencesService prefService;

    public BaseCacheableComponent()
    {
        super();

        // 1. set up data model listeners which will flush the cache
        //    For instance navigations will listen to:
        //      - changes in navigation configuration (parameter containers)
        //      - changes in site structure
        //          * reordering nodes
        //          * changes in nodes content (especially title :))
        //          * deleting nodes
        //          * adding nodes
        //          * moving nodes
        //    Making it easy to implement:
        //      Anything changes in site structure - flush navigation cache
        //      (which may not be good for intranet sites)

        // IMPORTANT: Component caches should be grouped by sites or even
        //      parts of sites to decrease the quantity of information
        //      flushed from cache on site changes
    }

    public String build(RunData data) throws ProcessingException
    {
        // 1. get result from cache
        //    The key used must be porper for the component type, as I wrote
        //    before Navigations will have a key consisting of:
        //         navigation instance name + selected navigation node id

        // 2. if result is null, generate the result using super.build(data)
        //    call

        // 3. store the result in cache

        String retValue = super.build(data);
        return retValue;
    }

}
