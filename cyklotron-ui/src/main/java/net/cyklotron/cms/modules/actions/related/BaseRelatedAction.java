package net.cyklotron.cms.modules.actions.related;

import org.jcontainer.dna.Logger;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.related.RelatedConstants;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: BaseRelatedAction.java,v 1.2 2005-01-24 10:27:38 pablo Exp $
 */
public abstract class BaseRelatedAction
    extends BaseCMSAction
    implements RelatedConstants
{
    protected RelatedService relatedService;
    
    public BaseRelatedAction(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, RelatedService relatedService)
    {
        super(logger, structureService, cmsDataFactory);
        this.relatedService = relatedService;
    }
}


