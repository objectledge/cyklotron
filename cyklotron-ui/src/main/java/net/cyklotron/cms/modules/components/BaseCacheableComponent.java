package net.cyklotron.cms.modules.components;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.templating.Template;
import org.objectledge.templating.Templating;
import org.objectledge.web.mvc.builders.BuildException;

import net.cyklotron.cms.CmsDataFactory;

/**
 */
public abstract class BaseCacheableComponent
    extends BaseCMSComponent
{
    public BaseCacheableComponent(Context context, Logger logger, Templating templating,
        CmsDataFactory cmsDataFactory)
    {
        super(context, logger, templating, cmsDataFactory);
        // TODO Auto-generated constructor stub
    }

    public String build(Template template) throws BuildException
    {
        // 1. get result from cache
        //    The key used must be porper for the component type, as I wrote
        //    before Navigations will have a key consisting of:
        //         navigation instance name + selected navigation node id

        // 2. if result is null, generate the result using super.build(data)
        //    call

        // 3. store the result in cache

        String retValue = super.build(template);
        return retValue;
    }

}
