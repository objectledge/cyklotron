package net.cyklotron.cms.modules.actions.link;

import net.cyklotron.cms.aggregation.AggregationException;
import net.cyklotron.cms.aggregation.AggregationService;
import net.cyklotron.cms.link.BaseLinkResource;
import net.cyklotron.cms.link.BaseLinkResourceImpl;
import net.cyklotron.cms.link.LinkException;
import net.cyklotron.cms.link.PoolResource;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Link aggregation copy action.
 * 
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: LinkAggregationCopy.java,v 1.2 2005-01-24 10:27:01 pablo Exp $
 */
public class LinkAggregationCopy
    extends BaseLinkAction
{
    AggregationService aggregationService;
    
    public LinkAggregationCopy()
    {
        aggregationService = (AggregationService)broker.getService(AggregationService.SERVICE_NAME);
    }
    
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();

        long resourceId = parameters.getLong("res_id", -1);
        long parentId = parameters.getLong("parent_id", -1);
        
        try
        {
            Resource parent = coralSession.getStore().getResource(parentId);
            if(!(parent instanceof PoolResource))
            {
                route(data, "aggregation,ImportTarget", "invalid_pool");
                return;
            }
            BaseLinkResource source = BaseLinkResourceImpl.getBaseLinkResource(coralSession, resourceId);
            if(!aggregationService.canImport(source, parent, subject))
            {
                route(data, "aggregation,ImportTarget", "no_rights_to_import");
                return;
            }
            String targetName = parameters.get("target_name",source.getName());
            BaseLinkResource link;
            link = linkService.copyLink(source, targetName, (PoolResource)parent, subject);
            aggregationService.createImport(source, link, subject);
        }
        catch(EntityDoesNotExistException e)
        {
            log.error("ARLException: ",e);
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            return;
        }
        catch(LinkException e)
        {
            log.error("LinkException: ",e);
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            return;
        }
        catch(AggregationException e)
        {
            log.error("AggregationException: ",e);
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            return;
        }
        templatingContext.put("result","copied_successfully");
    }
}

