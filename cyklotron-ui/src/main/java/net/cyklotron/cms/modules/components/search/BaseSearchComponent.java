package net.cyklotron.cms.modules.components.search;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.templating.Templating;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.components.BaseCMSComponent;
import net.cyklotron.cms.search.SearchService;

/**
 * The base component class for search app
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseSearchComponent.java,v 1.2 2005-01-26 03:52:19 pablo Exp $
 */
public abstract class BaseSearchComponent
    extends BaseCMSComponent
{
    /** search service */
    protected SearchService searchService;
    
    public BaseSearchComponent(Context context, Logger logger, Templating templating,
        CmsDataFactory cmsDataFactory, SearchService searchService)
    {
        super(context, logger, templating, cmsDataFactory);
        this.searchService = searchService;
    }
}
