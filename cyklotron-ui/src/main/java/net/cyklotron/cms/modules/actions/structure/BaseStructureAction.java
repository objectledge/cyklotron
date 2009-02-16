package net.cyklotron.cms.modules.actions.structure;

import org.jcontainer.dna.Logger;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.structure.NaviConstants;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 *
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: BaseStructureAction.java,v 1.1 2005-01-24 04:33:55 pablo Exp $
 */
public abstract class BaseStructureAction
    extends BaseCMSAction
    implements NaviConstants
{
    /** style service */
    protected StyleService styleService;
    
    public BaseStructureAction(Logger logger, StructureService structureService, 
        CmsDataFactory cmsDataFactory, StyleService styleService)
    {
        super(logger, structureService, cmsDataFactory);
        this.styleService = styleService;
    }
}

