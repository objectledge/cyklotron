package net.cyklotron.cms.modules.actions.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.table.SequenceComparator;
import net.labeo.services.resource.Resource;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 *
`*
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: ResetSequence.java,v 1.2 2005-01-24 10:26:59 pablo Exp $
 */
public class ResetSequence
    extends BaseStructureAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        NavigationNodeResource node = getNode(context);

        Resource[] children = coralSession.getStore().getResource(node);
        List list = Arrays.asList(children);
        Comparator comparator = new SequenceComparator();
        Collections.sort(list,comparator);
        List ids = new ArrayList();
        for(int i = 0; i < list.size(); i++)
        {
            ids.add(((Resource)list.get(i)).getIdObject());
        }
        httpContext.setSessionAttribute(CURRENT_SEQUENCE,ids);
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return checkPermission(context, coralSession, "cms.structure.move");
    }
}
