package net.cyklotron.cms.modules.actions.structure;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 * Base copy/cut/paste action
 * 
 * @author <a href="mailto:pablo@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseCopyPasteAction.java,v 1.3 2005-03-08 10:54:17 pablo Exp $
 */
public abstract class BaseCopyPasteAction extends BaseStructureAction
{
    public BaseCopyPasteAction(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService)
    {
        super(logger, structureService, cmsDataFactory, styleService);
        
    }
    
    public boolean canMove(Context context, Subject subject, Resource resource)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
    	Permission permission = coralSession.getSecurity().getUniquePermission("cms.structure.move");
    	return subject.hasPermission(resource, permission);
    }
}
