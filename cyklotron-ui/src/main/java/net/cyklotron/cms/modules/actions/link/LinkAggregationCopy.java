package net.cyklotron.cms.modules.actions.link;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.aggregation.AggregationException;
import net.cyklotron.cms.aggregation.AggregationService;
import net.cyklotron.cms.link.BaseLinkResource;
import net.cyklotron.cms.link.BaseLinkResourceImpl;
import net.cyklotron.cms.link.LinkException;
import net.cyklotron.cms.link.LinkService;
import net.cyklotron.cms.link.PoolResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 * Link aggregation copy action.
 * 
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: LinkAggregationCopy.java,v 1.4 2005-03-09 09:59:01 pablo Exp $
 */
public class LinkAggregationCopy
    extends BaseLinkAction
{
    AggregationService aggregationService;
 
    
    
    public LinkAggregationCopy(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, LinkService linkService, WorkflowService workflowService,
        AggregationService aggregationService)
    {
        super(logger, structureService, cmsDataFactory, linkService, workflowService);
        this.aggregationService = aggregationService;
    }
    
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Subject subject = coralSession.getUserSubject();

        long resourceId = parameters.getLong("res_id", -1);
        long parentId = parameters.getLong("parent_id", -1);
        
        try
        {
            Resource parent = coralSession.getStore().getResource(parentId);
            if(!(parent instanceof PoolResource))
            {
                route(mvcContext, templatingContext, "aggregation.ImportTarget", "invalid_pool");
                return;
            }
            BaseLinkResource source = BaseLinkResourceImpl.getBaseLinkResource(coralSession, resourceId);
            if(!aggregationService.canImport(coralSession, source, parent, subject))
            {
                route(mvcContext, templatingContext, "aggregation.ImportTarget", "no_rights_to_import");
                return;
            }
            String targetName = parameters.get("target_name",source.getName());
            BaseLinkResource link;
            link = linkService.copyLink(coralSession, source, targetName, (PoolResource)parent, subject);
            aggregationService.createImport(coralSession, source, link, subject);
        }
        catch(EntityDoesNotExistException e)
        {
            logger.error("ARLException: ",e);
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            return;
        }
        catch(LinkException e)
        {
            logger.error("LinkException: ",e);
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            return;
        }
        catch(AggregationException e)
        {
            logger.error("AggregationException: ",e);
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            return;
        }
        templatingContext.put("result","copied_successfully");
    }
}

