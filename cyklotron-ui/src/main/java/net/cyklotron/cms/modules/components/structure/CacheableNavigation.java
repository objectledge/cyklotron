package net.cyklotron.cms.modules.components.structure;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.Template;
import org.objectledge.templating.Templating;
import org.objectledge.web.mvc.builders.BuildException;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.StructureService;

/**
 * Base class for cacheable navigation components.
 * Takes care of caching rendered versions of the navigations.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: CacheableNavigation.java,v 1.2 2005-01-26 03:52:35 pablo Exp $
 */

public abstract class CacheableNavigation extends BaseNavigation
{
    
    
    public CacheableNavigation(Context context, Logger logger, Templating templating,
        CmsDataFactory cmsDataFactory, SkinService skinService, MVCFinder mvcFinder,
        TableStateManager tableStateManager, StructureService structureService)
    {
        super(context, logger, templating, cmsDataFactory, skinService, mvcFinder,
                        tableStateManager, structureService);
    }

    // TODO: make a prepare implementation which avoids
    //       row set creation if data is cached.
    
    public String build(Template template) throws BuildException
    {
        // 1. get result from cache
        //    The key used must be proper for the component type, as I wrote before,
        //     Navigations will have a key consisting of:
        //         navigation instance name + selected navigation node id
        
        // 2. if result is null, generate the result using super.build(data)
        //    call
        
        // 3. store the result in cache
        
        String retValue = super.build(template);
        return retValue;
    }
}
