package net.cyklotron.cms.modules.actions.category;

import org.objectledge.pipeline.ProcessingException;

/**
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: BaseCopyPasteAction.java,v 1.1 2005-01-24 04:33:58 pablo Exp $
 */
public abstract class BaseCopyPasteAction extends BaseCategoryAction
{
    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        return checkPermission(context, coralSession, "cms.category.move");
    }
}
