package net.cyklotron.cms.modules.actions.fixes;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.ResourceClassResource;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: FixToLedge.java,v 1.1 2005-03-10 11:48:22 pablo Exp $
 */
public class FixToLedge
    extends BaseCMSAction
{
    public FixToLedge(Logger logger, StructureService structureService, CmsDataFactory cmsDataFactory)
    {
        super(logger, structureService, cmsDataFactory);
    }

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            QueryResults results = coralSession.getQuery().
                executeQuery("FIND RESOURCE FROM integration.resource_class");
            Resource[] nodes = results.getArray(1);
            for(int i = 0; i < nodes.length; i++)
            {
                fixClass(coralSession, (ResourceClassResource)nodes[i]);
            }
        }
        catch(Exception e)
        {
            logger.error("Structure: CheckNodeState Job Exception",e);            
        }
    }
    
    public void fixClass(CoralSession coralSession, ResourceClassResource rcr)
    {
        String view = rcr.getView();
        if(view != null && view.indexOf(',') > 0)
        {
            view = view.replace(',','.');
            rcr.setView(view);
            rcr.update();
        }
    }
}
