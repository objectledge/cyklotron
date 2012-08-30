package net.cyklotron.cms.modules.actions.related;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.datatypes.ResourceList;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.relation.Relation;
import org.objectledge.coral.relation.RelationModification;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.Subject;
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
import net.cyklotron.cms.category.CategoryConstants;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryResourceImpl;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.FilesException;
import net.cyklotron.cms.files.FilesMapResource;
import net.cyklotron.cms.files.FilesService;
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
    
    protected CategoryService categoryService;
    
    protected FilesService filesService;
    
    public UpdateRelations(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, RelatedService relatedService,
        CategoryService categoryService, FilesService filesService)
    {
        super(logger, structureService, cmsDataFactory, relatedService);
        this.categoryService = categoryService;
        this.filesService = filesService;
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
            ResourceSelectionState relatedState = getSelectionState(context, resource);
            relatedState.update(parameters);
           
            List<Resource> oldResources = new ArrayList<Resource>(Arrays.asList(relation.get(resource)));
            List<Resource> newResources = new ArrayList<Resource>(
                            relatedState.getEntities(coralSession,"selected").keySet());

            List<Resource> toRemove = new ArrayList<Resource>(oldResources);
            toRemove.removeAll(newResources);
            List<Resource> toAdd = new ArrayList<Resource>(newResources);
            toAdd.removeAll(oldResources);
            
            HashSet <Resource> categorizableResources = new HashSet();
            if(resource instanceof DocumentNodeResource
                && ((DocumentNodeResource)resource).isThumbnailDefined())
            {
                categorizableResources.add(((DocumentNodeResource)resource).getThumbnail());
            }
            categorizableResources.addAll(oldResources);
            updateFileResourceCategories(context, parameters, categorizableResources, coralSession);

            modification.add(resource, toAdd);
            modification.remove(resource, toRemove);
            
            if(symetricRelation)
            {
                modification.add(toAdd, resource);
                modification.remove(toRemove, resource);
                for(Resource res : toAdd)
                {
                    removeSelectionState(context, res);
                }
                for(Resource res : toRemove)
                {
                    removeSelectionState(context, res);
                }
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
        catch(FilesException e)
        {
            logger.error("Fiels Exception: ",e);
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            return;
        }        
        templatingContext.put("sym_rel",symetricRelation);
        templatingContext.put("result","updated_successfully");
    }

    
    /**
     * Update FileResource front categories
     * @param context
     * @param parameters
     * @param fileResource
     * @param filesRoot
     * @param coralSession
     * @throws EntityDoesNotExistException
     * @throws FilesException 
     * @throws ProcessingException 
     */
    private void updateFileResourceCategories(Context context, Parameters parameters,
        HashSet <Resource> resources, CoralSession coralSession)
        throws EntityDoesNotExistException, FilesException, ProcessingException
    {

        CmsData cmsData = cmsDataFactory.getCmsData(context);
        FilesMapResource filesRoot = filesService.getFilesRoot(coralSession, cmsData.getSite());
        
        if(filesRoot.isFrontCategoriesDefined() && !filesRoot.getFrontCategories().isEmpty())
        {
            Subject subject = coralSession.getUserSubject();
            ResourceList<CategoryResource> frontCategories = filesRoot.getFrontCategories();
            Permission classifyPermission = coralSession.getSecurity().getUniquePermission(
                "cms.category.classify");

            for(Resource resource : resources)
            {
                if(resource instanceof FileResource)
                {
                    long[] newCategoriesIds = parameters.getLongs("res-"+resource.getIdString()+"-front-categories");
                    HashSet<Resource> newCategories = new HashSet<Resource>();

                    for(long catId : newCategoriesIds)
                    {
                        newCategories.add(CategoryResourceImpl.getCategoryResource(coralSession,
                            catId));
                    }

                    List<CategoryResource> resourceCategories = Arrays.asList(categoryService
                        .getCategories(coralSession, resource, false));
                                        
                    List<Resource> oldCategories = new ArrayList<Resource>(frontCategories);
                    oldCategories.retainAll(resourceCategories);
                    
                    List<Resource> toRemove = new ArrayList<Resource>(oldCategories);
                    toRemove.removeAll(newCategories);
                    List<Resource> toAdd = new ArrayList<Resource>(newCategories);
                    toAdd.removeAll(oldCategories);

                    if(!(toAdd.isEmpty() && toRemove.isEmpty()))
                    {

                        RelationModification diff = new RelationModification();
                        for(Iterator i = toAdd.iterator(); i.hasNext();)
                        {
                            CategoryResource category = (CategoryResource)(i.next());
                            if(subject.hasPermission(category, classifyPermission))
                            {
                                diff.add(category, resource);
                            }
                        }
                        for(Iterator i = toRemove.iterator(); i.hasNext();)
                        {
                            CategoryResource category = (CategoryResource)(i.next());
                            if(subject.hasPermission(category, classifyPermission))
                            {
                                diff.remove(category, resource);
                            }
                        }

                        // update relation
                        Relation refs = categoryService.getResourcesRelation(coralSession);
                        coralSession.getRelationManager().updateRelation(refs, diff);

                        // remove state
                        ResourceSelectionState categorizationState = ResourceSelectionState
                            .getState(context, CategoryConstants.CATEGORY_SELECTION_STATE + ":"
                                + resource.getIdString());
                        CoralEntitySelectionState.removeState(context, categorizationState);
                    }
                    
                }
            }
        }
    }
    
    private void removeSelectionState(Context context, Resource res)
    {
        CoralEntitySelectionState.removeState(context, getSelectionState(context, res));
    }
    
    private ResourceSelectionState getSelectionState(Context context, Resource resource)
    {
        return ResourceSelectionState.getState(context, RELATED_SELECTION_STATE + ":"
            + resource.getIdString());
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

