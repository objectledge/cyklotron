package net.cyklotron.cms.modules.actions.related;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.relation.Relation;
import org.objectledge.coral.relation.RelationModification;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.util.CoralEntitySelectionState;
import org.objectledge.coral.util.ResourceSelectionState;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.ProtectedResource;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.structure.StructureService;

/**
 * Update resource relationships.
 * 
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: UpdateRelations.java,v 1.9 2007-12-20 16:57:05 rafal Exp $
 */
public class UpdateRelations
    extends BaseRelatedAction
{
    
    public UpdateRelations(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, RelatedService relatedService)
    {
        super(logger, structureService, cmsDataFactory, relatedService);
        
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        long resId = parameters.getLong("res_id", -1L);
        long resClassResId = parameters.getLong("res_class_id", -1L);
        boolean symetricRelation = parameters.getBoolean("sym_rel", false);
        try
        {
            Relation relation = relatedService.getRelation(coralSession);
            RelationModification modification = new RelationModification();
            
            Resource resource = coralSession.getStore().getResource(resId);
            ResourceSelectionState relatedState = ResourceSelectionState.getState(context,
                RELATED_SELECTION_STATE + ":" + resource.getIdString());
            relatedState.update(parameters);
           
            ListIterator<Resource> iterator;
            List<Resource> oldResorces = new ArrayList<Resource>(Arrays.asList(relation.get(resource)));
         
            List<Resource> toRemove = new ArrayList<Resource>(oldResorces);
            List<Resource> toAdd = new ArrayList<Resource>(
                            relatedState.getEntities(coralSession,"selected").keySet());
            
            if(symetricRelation){
                
                toRemove.removeAll(toAdd);
                toAdd.removeAll(oldResorces);
                
                modification.add(toAdd, resource);                
                modification.add(resource, toAdd);
                iterator = toRemove.listIterator();
                while(iterator.hasNext()){
                    Resource removeResource = (Resource)iterator.next();
                    modification.remove(removeResource, resource);
                    modification.remove(resource, removeResource);
                }
            }
            else{
                modification.clear();
                modification.add(resource, toAdd);
            }
            
            coralSession.getRelationManager().updateRelation(relation, modification);
            CoralEntitySelectionState.removeState(context, relatedState);
        }
        catch(EntityDoesNotExistException e)
        {
            logger.error("ARLException: ",e);
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            return;
        }        
        templatingContext.put("sym_rel",symetricRelation);
        templatingContext.put("result","updated_successfully");
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        if(!cmsData.isApplicationEnabled("related"))
        {
            logger.debug("Application 'related' not enabled in site");
            return false;
        }
        Parameters parameters = RequestParameters.getRequestParameters(context);
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        long resId = parameters.getLong("res_id", -1L);
        try
        {
            Resource resource = coralSession.getStore().getResource(resId);
            if(resource instanceof ProtectedResource)
            {
                return ((ProtectedResource)resource).canModify(coralSession, coralSession.getUserSubject());
            }
        }
        catch(EntityDoesNotExistException e)
        {
            // resource undefined, or got deleted - ignore
        }
        return true;
    }

}

