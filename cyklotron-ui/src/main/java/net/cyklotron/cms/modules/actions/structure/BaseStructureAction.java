package net.cyklotron.cms.modules.actions.structure;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.CmsData;
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

    /**
     * Checks if the current subject can modify the current node.
     * 
     * @param context Context
     * @return true if the current subject can modify the current node.
     * @throws ProcessingException if checking fails.
     */
    protected boolean checkModifyPermission(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = context.getAttribute(CoralSession.class);
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        return cmsData.getNode().canModify(coralSession, coralSession.getUserSubject());
    }
}

