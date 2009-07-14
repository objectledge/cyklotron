package net.cyklotron.cms.modules.actions.structure;

import static net.cyklotron.cms.documents.DocumentMetadataHelper.doc;
import static net.cyklotron.cms.documents.DocumentMetadataHelper.dom4jToText;
import static net.cyklotron.cms.documents.DocumentMetadataHelper.elm;
import static net.cyklotron.cms.documents.DocumentMetadataHelper.selectFirstText;
import static net.cyklotron.cms.documents.DocumentMetadataHelper.textToDom4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.Element;
import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.relation.Relation;
import org.objectledge.coral.relation.RelationModification;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;
import org.objectledge.coral.util.CoralEntitySelectionState;
import org.objectledge.coral.util.ResourceSelectionState;
import org.objectledge.html.HTMLException;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.upload.FileUpload;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.documents.DocumentNodeResourceImpl;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.modules.views.documents.BaseSkinableDocumentScreen;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.structure.internal.ProposedDocumentData;
import net.cyklotron.cms.style.StyleService;

/**
 * Propose new navigation node in document tree.
 * 
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @author <a href="mailo:mover@caltha.pl">Michal Mach</a>
 * @version $Id: ProposeDocument.java,v 1.22 2008-11-05 23:21:37 rafal Exp $
 */

public class SaveProposedChanges
    extends BaseAddEditNodeAction
{
    private CategoryService categoryService;
    
    private RelatedService relatedService;

    public SaveProposedChanges(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService, CategoryService categoryService,
        FileUpload uploadService, FilesService filesService,
        CoralSessionFactory coralSessionFactory, RelatedService relatedService)
    {
        super(logger, structureService, cmsDataFactory, styleService);
        this.categoryService = categoryService;
        this.relatedService = relatedService;
    }

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        long docId = parameters.getLong("doc_id", -1L);

        try
        {
            if(docId != -1)
            {
                DocumentNodeResource node = DocumentNodeResourceImpl.getDocumentNodeResource(
                    coralSession, docId);

                CmsData cmsData = cmsDataFactory.getCmsData(context);
                ProposedDocumentData proposedData = new ProposedDocumentData();
                ProposedDocumentData data = new ProposedDocumentData();
                proposedData.fromProposal(node, coralSession);
                Parameters screenConfig = cmsData.getEmbeddedScreenConfig(proposedData.getOrigin());
                proposedData.setConfiguration(screenConfig);                
                


                if(parameters.getBoolean("title", false))
                {
                    node.setTitle(proposedData.getTitle());
                }
                if(parameters.getBoolean("abstract", false))
                {
                    node.setAbstract(proposedData.getAbstract());
                }
                if(parameters.getBoolean("description", false))
                {
                    node.setDescription(proposedData.getDescription());
                }
                if(parameters.getBoolean("content", false))
                {
                    node.setContent(proposedData.getContent());
                }
                if(parameters.getBoolean("eventPlace", false))
                {
                    node.setEventPlace(proposedData.getEventPlace());
                }
                if(parameters.getBoolean("eventStart", false))
                {
                    node.setEventStart(proposedData.getEventStart());
                }
                if(parameters.getBoolean("eventEnd", false))
                {
                    node.setEventEnd(proposedData.getEventEnd());
                }
                if(parameters.getBoolean("validityStart", false))
                {
                    node.setValidityStart(proposedData.getValidityStart());
                }
                if(parameters.getBoolean("validityEnd", false))
                {
                    node.setValidityEnd(proposedData.getValidityEnd());
                }

               // if(parameters.getBoolean("metaData", false))
               // {

                    Document metaDom = textToDom4j(node.getMeta());

                    String organizedBy = selectFirstText(metaDom, "/meta/organisation/name");
                    String organizedAddress = selectFirstText(metaDom, "/meta/organisation/address");
                    String organizedPhone = selectFirstText(metaDom, "/meta/organisation/tel");
                    String organizedFax = selectFirstText(metaDom, "/meta/organisation/fax");
                    String organizedEmail = selectFirstText(metaDom, "/meta/organisation/e-mail");
                    String organizedWww = selectFirstText(metaDom, "/meta/organisation/url");
                    String sourceName = selectFirstText(metaDom, "/meta/sources/source/name");
                    String sourceUrl = selectFirstText(metaDom, "/meta/sources/source/url");
                    String proposerCredentials = selectFirstText(metaDom,
                        "/meta/authors/author/name");
                    String proposerEmail = selectFirstText(metaDom, "/meta/authors/author/e-mail");

                    
                    if(parameters.getBoolean("organizedBy", false))
                    {
                        organizedBy = proposedData.getOrganizedBy();
                    }
                    if(parameters.getBoolean("organizedAddress", false))
                    {
                        organizedAddress = proposedData.getOrganizedAddress();
                    }
                    if(parameters.getBoolean("organizedPhone", false))
                    {
                        organizedPhone = proposedData.getOrganizedPhone();
                    }
                    if(parameters.getBoolean("organizedFax", false))
                    {
                        organizedFax = proposedData.getOrganizedFax();
                    }
                    if(parameters.getBoolean("organizedEmail", false))
                    {
                        organizedEmail = proposedData.getOrganizedEmail();
                    }
                    if(parameters.getBoolean("organizedWww", false))
                    {
                        organizedWww = proposedData.getOrganizedWww();
                    }
                    if(parameters.getBoolean("sourceName", false))
                    {
                        sourceName = proposedData.getSourceName();
                    }
                    if(parameters.getBoolean("sourceUrl", false))
                    {
                        sourceUrl = proposedData.getSourceUrl();
                    }
                    if(parameters.getBoolean("proposerCredentials", false))
                    {
                        proposerCredentials = proposedData.getProposerCredentials();
                    }
                    if(parameters.getBoolean("proposerEmail", false))
                    {
                        proposerEmail = proposedData.getProposerEmail();
                    }

                    Element metaElm = elm("meta", elm("authors", elm("author", elm("name",
                        proposerCredentials), elm("e-mail", proposerEmail))), elm("sources", elm(
                        "source", elm("name", sourceName), elm("url", sourceUrl))), elm("editor"),
                        elm("organisation", elm("name", organizedBy), elm("address",
                            organizedAddress), elm("tel", organizedPhone),
                            elm("fax", organizedFax), elm("e-mail", organizedEmail), elm("url",
                                organizedWww), elm("id", "0")));

                    Document doc = doc(metaElm);
                    node.setMeta(dom4jToText(doc));
                //}

                if(parameters.getBoolean("docCategories", false))
                {                       
                    Relation relation = categoryService.getResourcesRelation(coralSession);
                    RelationModification modification = new RelationModification();
                    
                    // take document node categories  
                    Set<CategoryResource> publishedDocCategories = new HashSet<CategoryResource>(
                        Arrays.asList(categoryService.getCategories(coralSession, node, false)));
                    // take proposed document categories
                    Set<CategoryResource> proposedDocCategories = proposedData.getSelectedCategories();
                    
                    // take component available root categories id 
                    long root_category_1 = screenConfig.getLong("category_id_1", -1);  
                    long root_category_2 = screenConfig.getLong("category_id_2", -1);
                    List<CategoryResource> allAvailableCategories = new ArrayList<CategoryResource>();
                    allAvailableCategories.addAll(BaseSkinableDocumentScreen.getCategoryList(root_category_1, true, coralSession));
                    allAvailableCategories.addAll(BaseSkinableDocumentScreen.getCategoryList(root_category_2, true, coralSession)); 
                    
                    List<Resource> toRemove = new ArrayList<Resource>(allAvailableCategories);
                    List<Resource> toAdd = new ArrayList<Resource>(proposedDocCategories);

                    // remove proposed categories from available categories
                    toRemove.removeAll(proposedDocCategories);       
                    
                    // remove from proposed categories document node categories
                    toAdd.removeAll(publishedDocCategories);
                    
                    modification.add(toAdd, node);
                    modification.remove(toRemove, node);

                    // update categories
                    coralSession.getRelationManager().updateRelation(relation, modification);
                    
                }
                if(parameters.getBoolean("docAttachments", false))
                {
                    Relation relation = categoryService.getResourcesRelation(coralSession);
                    RelationModification modification = new RelationModification();
                    
                    List<Resource> publishedDocAttachments = new ArrayList<Resource>(Arrays
                        .asList(relatedService.getRelatedTo(coralSession, node, 
                            node.getRelatedResourcesSequence(), null)));
                    
                    List<Resource> proposedDocAttachments = proposedData.getAttachments();                   
                   
                    List<Resource> toRemove = new ArrayList<Resource>(publishedDocAttachments);
                    List<Resource> toAdd = new ArrayList<Resource>(proposedDocAttachments);
                    
                    toRemove.removeAll(proposedDocAttachments);
                    toAdd.removeAll(publishedDocAttachments);
                    
                    modification.add(node, toAdd);
                    modification.remove(node, toRemove);

                    coralSession.getRelationManager().updateRelation(relation, modification);
                }
                if(!parameters.getBoolean("save_doc_proposal",false)){
                    node.setProposedContent(null);
                }
                node.update();
            }

        }
        catch(EntityDoesNotExistException e)
        {
            logger.error("excception", e);
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
        }
        catch(ValueRequiredException e)
        {
            logger.error("excception", e);
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
        }
        catch(HTMLException e)
        {
            logger.error("excception", e);
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        try
        {
            Permission permission = coralSession.getSecurity().getUniquePermission(
                "cms.structure.submit");
            CmsData cmsData = cmsDataFactory.getCmsData(context);
            Parameters screenConfig = cmsData.getEmbeddedScreenConfig();
            long parentId = screenConfig.getLong("parent_id", -1L);
            Resource parent = parentId != -1L ? NavigationNodeResourceImpl
                .getNavigationNodeResource(coralSession, parentId) : cmsData.getNode();
            return coralSession.getUserSubject().hasPermission(parent, permission);
        }
        catch(Exception e)
        {
            throw new ProcessingException("Exception occured during access rights checking ", e);
        }
    }

    /**
     * @{inheritDoc
     */
    public boolean requiresAuthenticatedUser(Context context)
        throws Exception
    {
        return false;
    }

    @Override
    protected String getViewName()
    {
        return null;
    }
}
