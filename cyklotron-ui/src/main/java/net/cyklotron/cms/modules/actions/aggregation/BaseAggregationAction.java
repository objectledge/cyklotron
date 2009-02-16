package net.cyklotron.cms.modules.actions.aggregation;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.aggregation.AggregationService;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: BaseAggregationAction.java,v 1.3 2005-05-20 05:32:01 pablo Exp $
 */
public abstract class BaseAggregationAction
    extends BaseCMSAction
{
    /** structure service */
    protected SiteService siteService;
    
    /** aggregation service */
    protected AggregationService aggregationService;

    public BaseAggregationAction(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SiteService siteService,
        AggregationService aggregationService)
    {
        super(logger, structureService, cmsDataFactory);
        this.siteService = siteService;
        this.aggregationService = aggregationService;
    }

    public boolean checkAccessRights(Context context)
        throws Exception
    {
        return checkAdministrator(context);
    }
}
