package net.cyklotron.cms.modules.actions.structure;

import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.webcore.ProcessingException;

/**
 * Base copy/cut/paste action
 * 
 * @author <a href="mailto:pablo@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseCopyPasteAction.java,v 1.1 2005-01-24 04:33:55 pablo Exp $
 */
public abstract class BaseCopyPasteAction extends BaseStructureAction
{
    public boolean canMove(Subject subject, Resource resource)
        throws ProcessingException
    {
    	Permission permission = coralSession.getSecurity().getUniquePermission("cms.structure.move");
    	return subject.hasPermission(resource, permission);
    }
}
