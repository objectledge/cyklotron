package net.cyklotron.cms.modules.actions.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.structure.table.SequenceComparator;
import net.cyklotron.cms.style.StyleService;

/**
 *
`*
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: ResetSequence.java,v 1.3 2005-01-25 08:24:46 pablo Exp $
 */
public class ResetSequence
    extends BaseStructureAction
{
    public ResetSequence(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService)
    {
        super(logger, structureService, cmsDataFactory, styleService);
        // TODO Auto-generated constructor stub
    }
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
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return checkPermission(context, coralSession, "cms.structure.move");
    }
}
