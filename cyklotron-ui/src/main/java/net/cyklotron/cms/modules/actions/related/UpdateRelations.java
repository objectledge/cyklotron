package net.cyklotron.cms.modules.actions.related;

import java.util.Map;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.util.ResourceSelectionState;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.ProtectedResource;

/**
 * Update resource relationships.
 * 
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: UpdateRelations.java,v 1.1 2005-01-24 04:34:41 pablo Exp $
 */
public class UpdateRelations
    extends BaseRelatedAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        long resId = parameters.getLong("res_id", -1L);
        long resClassResId = parameters.getLong("res_class_id", -1L);
        try
        {
            Resource resource = coralSession.getStore().getResource(resId);
            ResourceSelectionState relatedState =
                ResourceSelectionState.getState(data, RELATED_SELECTION_STATE);
            relatedState.update(data);
            Map selected = relatedState.getResources(coralSession,"selected");
            Resource[] resources = new Resource[selected.size()];
            selected.keySet().toArray(resources);
            relatedService.setRelatedTo(resource, resources, coralSession.getUserSubject());
            ResourceSelectionState.removeState(data, relatedState);
        }
        catch(EntityDoesNotExistException e)
        {
            log.error("ARLException: ",e);
            templatingContext.put("result","exception");
            templatingContext.put("trace",StringUtils.stackTrace(e));
            return;
        }
        templatingContext.put("result","updated_successfully");
    }

    public boolean checkAccess(RunData data)
    {
        long resId = parameters.getLong("res_id", -1L);
        try
        {
            Resource resource = coralSession.getStore().getResource(resId);
            if(resource instanceof ProtectedResource)
            {
                return ((ProtectedResource)resource).canModify(coralSession.getUserSubject());
            }
        }
        catch(EntityDoesNotExistException e)
        {
        }
        return true;
    }

}

