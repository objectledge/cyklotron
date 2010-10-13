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
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;
import org.objectledge.html.HTMLException;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
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
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.modules.views.documents.BaseSkinableDocumentScreen;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.structure.internal.OrganizationData;
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
    @Override
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
                ProposedDocumentData proposedData = new ProposedDocumentData(logger);
                ProposedDocumentData publishedData = new ProposedDocumentData(logger);
                proposedData.fromProposal(node, coralSession);
                Parameters screenConfig = cmsData.getEmbeddedScreenConfig(proposedData.getOrigin());
                proposedData.setConfiguration(screenConfig);

                if(parameters.get("title", "").equals("accept"))
                {
                    node.setTitle(proposedData.getTitle());
                }
                else if(parameters.get("title", "").equals("reject"))
                {
                    proposedData.setTitle(node.getTitle());
                }
                if(parameters.get("abstract", "").equals("accept"))
                {
                    node.setAbstract(proposedData.getAbstract());
                }
                else if(parameters.get("abstract", "").equals("reject"))
                {
                    proposedData.setDocAbstract(node.getAbstract());
                }
                if(parameters.get("description", "").equals("accept"))
                {
                    node.setDescription(proposedData.getDescription());
                }
                else if(parameters.get("description", "").equals("reject"))
                {
                    proposedData.setDescription(node.getDescription());
                }
                if(parameters.get("content", "").equals("accept"))
                {
                    node.setContent(proposedData.getContent());
                }
                else if(parameters.get("content", "").equals("reject"))
                {
                    proposedData.setContent(node.getContent());
                }
                if(parameters.get("eventPlace", "").equals("accept"))
                {
                    node.setEventPlace(proposedData.getEventPlace());
                }
                else if(parameters.get("eventPlace", "").equals("reject"))
                {
                    proposedData.setEventPlace(node.getEventPlace());
                }
                if(parameters.get("eventStart", "").equals("accept"))
                {
                    node.setEventStart(proposedData.getEventStart());
                }
                else if(parameters.get("eventStart", "").equals("reject"))
                {
                    proposedData.setEventStart(node.getEventStart());
                }
                if(parameters.get("eventEnd", "").equals("accept"))
                {
                    node.setEventEnd(proposedData.getEventEnd());
                }
                else if(parameters.get("eventEnd", "").equals("reject"))
                {
                    proposedData.setEventEnd(node.getEventEnd());
                }
                if(parameters.get("validityStart", "").equals("accept"))
                {
                    node.setValidityStart(proposedData.getValidityStart());
                }
                else if(parameters.get("validityStart", "").equals("reject"))
                {
                    proposedData.setValidityStart(node.getValidityStart());
                }
                if(parameters.get("validityEnd", "").equals("accept"))
                {
                    node.setValidityEnd(proposedData.getValidityEnd());
                }
                else if(parameters.get("validityEnd", "").equals("reject"))
                {
                    proposedData.setValidityEnd(node.getValidityEnd());
                }

                Document metaDom = textToDom4j(node.getMeta());

                String eventProvince = selectFirstText(metaDom, "/meta/event/address/province");
                String eventPostCode = selectFirstText(metaDom, "/meta/event/address/postcode");
                String eventCity = selectFirstText(metaDom, "/meta/event/address/city");
                String eventStreet = selectFirstText(metaDom, "/meta/event/address/street");
                List<OrganizationData> organizations = OrganizationData.fromMeta(metaDom,
                    "/meta/organizations");
                String sourceName = selectFirstText(metaDom, "/meta/sources/source/name");
                String sourceUrl = selectFirstText(metaDom, "/meta/sources/source/url");
                String proposerCredentials = selectFirstText(metaDom, "/meta/authors/author/name");
                String proposerEmail = selectFirstText(metaDom, "/meta/authors/author/e-mail");

                if(parameters.get("eventProvince", "").equals("accept"))
                {
                    eventProvince = proposedData.getEventProvince();
                }
                else if(parameters.get("eventProvince", "").equals("reject"))
                {
                    proposedData.setEventProvince(eventProvince);
                }
                if(parameters.get("eventPostCode", "").equals("accept"))
                {
                    eventPostCode = proposedData.getEventPostCode();
                }
                else if(parameters.get("eventPostCode", "").equals("reject"))
                {
                    proposedData.setEventPostCode(eventPostCode);
                }
                if(parameters.get("eventCity", "").equals("accept"))
                {
                    eventCity = proposedData.getEventCity();
                }
                else if(parameters.get("eventCity", "").equals("reject"))
                {
                    proposedData.setEventCity(eventCity);
                }
                if(parameters.get("eventStreet", "").equals("accept"))
                {
                    eventStreet = proposedData.getEventStreet();
                }
                else if(parameters.get("eventStreet", "").equals("reject"))
                {
                    proposedData.setEventStreet(eventStreet);
                }
                if(parameters.get("sourceName", "").equals("accept"))
                {
                    sourceName = proposedData.getSourceName();
                }
                else if(parameters.get("sourceName", "").equals("reject"))
                {
                    proposedData.setSourceName(sourceName);
                }
                if(parameters.get("sourceUrl", "").equals("accept"))
                {
                    sourceUrl = proposedData.getSourceUrl();
                }
                else if(parameters.get("sourceUrl", "").equals("reject"))
                {
                    proposedData.setSourceUrl(sourceUrl);
                }
                if(parameters.get("proposerCredentials", "").equals("accept"))
                {
                    proposerCredentials = proposedData.getProposerCredentials();
                }
                else if(parameters.get("proposerCredentials", "").equals("reject"))
                {
                    proposedData.setProposerCredentials(proposerCredentials);
                }
                if(parameters.get("proposerEmail", "").equals("accept"))
                {
                    proposerEmail = proposedData.getProposerEmail();
                }
                else if(parameters.get("proposerEmail", "").equals("reject"))
                {
                    proposedData.setProposerEmail(proposerEmail);
                }

                boolean anyOrgsUpdated = updateOrganisatios(parameters, organizations, proposedData
                    .getOrganizations());

                if(anyOrgsUpdated)
                {
                    node.setOrganizationIds(OrganizationData.getOrganizationIds(organizations));
                }
                
                Element metaElm = elm("meta", elm("authors", elm("author", elm("name",
                    proposerCredentials), elm("e-mail", proposerEmail))), elm("sources", elm(
                    "source", elm("name", sourceName), elm("url", sourceUrl))), elm("editor"), elm(
                    "event", elm("address", elm("street", eventStreet), elm("postcode",
                        eventPostCode), elm("city", eventCity), elm("province", eventProvince))),
                        OrganizationData.toMeta(organizations));

                Document doc = doc(metaElm);
                node.setMeta(dom4jToText(doc));

                if(parameters.get("docCategories", "").equals("accept"))
                {
                    Relation relation = categoryService.getResourcesRelation(coralSession);
                    RelationModification modification = new RelationModification();

                    // take document node categories
                    Set<CategoryResource> publishedDocCategories = new HashSet<CategoryResource>(
                        Arrays.asList(categoryService.getCategories(coralSession, node, false)));
                    // take proposed document categories
                    Set<CategoryResource> proposedDocCategories = proposedData
                        .getSelectedCategories();

                    // take component available root categories id
                    long root_category_1 = screenConfig.getLong("category_id_1", -1);
                    long root_category_2 = screenConfig.getLong("category_id_2", -1);
                    int categoryDepth = screenConfig.getInt("category_depth", 1);
                    List<CategoryResource> allAvailableCategories = new ArrayList<CategoryResource>();
                    BaseSkinableDocumentScreen.getCategoryList(root_category_1, categoryDepth,
                        true, coralSession, allAvailableCategories);
                    BaseSkinableDocumentScreen.getCategoryList(root_category_2, categoryDepth,
                        true, coralSession, allAvailableCategories);

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
                else if(parameters.get("docCategories", "").equals("reject"))
                {
                    proposedData.setSelectedCategories(new HashSet<CategoryResource>(Arrays
                        .asList(categoryService.getCategories(coralSession, node, false))));
                }

                publishedData.setConfiguration(screenConfig);
                publishedData.fromNode(node, categoryService, relatedService, coralSession);

                if(parameters.get("docAttachments", "").equals("accept"))
                {
                    Relation relation = relatedService.getRelation(coralSession);
                    RelationModification modification = new RelationModification();

                    List<Resource> publishedDocAttachments = publishedData.getAttachments();
                    List<Resource> proposedDocAttachments = proposedData.getAttachments();

                    List<Resource> toRemove = new ArrayList<Resource>(publishedDocAttachments);
                    List<Resource> toAdd = new ArrayList<Resource>(proposedDocAttachments);

                    for(Resource res : proposedDocAttachments)
                    {
                        ((FileResource)res).setDescription(proposedData
                            .getAttachmentDescription(res));
                    }

                    toRemove.removeAll(proposedDocAttachments);
                    toAdd.removeAll(publishedDocAttachments);

                    Resource publishedTumbnail = node.getThumbnail();
                    if(publishedTumbnail != null && toRemove.contains(publishedTumbnail))
                    {
                        toRemove.remove(publishedTumbnail);
                        node.setThumbnail(null);
                    }

                    modification.add(node, toAdd);
                    modification.remove(node, toRemove);

                    coralSession.getRelationManager().updateRelation(relation, modification);
                }
                else if(parameters.get("docAttachments", "").equals("reject"))
                {
                    proposedData.setAttachments(publishedData.getAttachments());
                }
                if(!parameters.get("redactors_note", "").equals(node.getRedactorsNote()))
                {
                    node.setRedactorsNote(parameters.get("redactors_note", ""));
                }
                if(parameters.getBoolean("save_doc_proposal", false))
                {
                    proposedData.toProposal(node);
                }
                else
                {
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

    private boolean updateOrganisatios(Parameters parameters, List<OrganizationData> publishedOrganizations,
        List<OrganizationData> proposedOrganizations)
    {
        List<OrganizationData> toRemove = new ArrayList<OrganizationData>();
        int maxOrgsCount = Math.max(publishedOrganizations.size(), proposedOrganizations.size());

        boolean anyOrgsUpdated = false;
        for(int i = 0; i < maxOrgsCount; i++)
        {            
            OrganizationData publishedOrg = OrganizationData.get(publishedOrganizations, i);
            OrganizationData proposedOrg = OrganizationData.get(proposedOrganizations, i);            
            String prefix = "organization_" + (i+1) + "_";
            boolean anyAccepted = false;
            boolean anyRejected = false;            
            if(parameters.get(prefix + "name", "").equals("accept"))
            {
                publishedOrg.setName(proposedOrg.getName());
                publishedOrg.setId(proposedOrg.getId());
                anyAccepted = true;
                anyOrgsUpdated = true;
            }
            else if(parameters.get(prefix + "name", "").equals("reject"))
            {
                proposedOrg.setName(publishedOrg.getName());
                proposedOrg.setId(publishedOrg.getId());
                anyRejected = true;
            }
            if(parameters.get(prefix + "province", "").equals("accept"))
            {
                publishedOrg.setProvince(proposedOrg.getProvince());
                anyAccepted = true;
            }
            else if(parameters.get(prefix + "province", "").equals("reject"))
            {
                proposedOrg.setProvince(publishedOrg.getProvince());
                anyRejected = true;
            }
            if(parameters.get(prefix + "postCode", "").equals("accept"))
            {
                publishedOrg.setPostCode(proposedOrg.getPostCode());
                anyAccepted = true;
            }
            else if(parameters.get(prefix + "postCode", "").equals("reject"))
            {
                proposedOrg.setPostCode(publishedOrg.getPostCode());
                anyRejected = true;
            }
            if(parameters.get(prefix + "city", "").equals("accept"))
            {
                publishedOrg.setCity(proposedOrg.getCity());
                anyAccepted = true;
            }
            else if(parameters.get(prefix + "city", "").equals("reject"))
            {
                proposedOrg.setCity(publishedOrg.getCity());
                anyRejected = true;
            }
            if(parameters.get(prefix + "street", "").equals("accept"))
            {
                publishedOrg.setStreet(proposedOrg.getStreet());
                anyAccepted = true;
            }
            else if(parameters.get(prefix + "street", "").equals("reject"))
            {
                proposedOrg.setStreet(publishedOrg.getStreet());
                anyRejected = true;
            }
            if(parameters.get(prefix + "phone", "").equals("accept"))
            {
                publishedOrg.setPhone(proposedOrg.getPhone());
                anyAccepted = true;
            }
            else if(parameters.get(prefix + "phone", "").equals("reject"))
            {
                proposedOrg.setPhone(publishedOrg.getPhone());
                anyRejected = true;
            }
            if(parameters.get(prefix + "fax", "").equals("accept"))
            {
                publishedOrg.setFax(proposedOrg.getFax());
                anyAccepted = true;
            }
            else if(parameters.get(prefix + "fax", "").equals("reject"))
            {
                proposedOrg.setFax(publishedOrg.getFax());
                anyRejected = true;
            }
            if(parameters.get(prefix + "email", "").equals("accept"))
            {
                publishedOrg.setEmail(proposedOrg.getEmail());
                anyAccepted = true;
            }
            else if(parameters.get(prefix + "email", "").equals("reject"))
            {
                proposedOrg.setEmail(publishedOrg.getEmail());
                anyRejected = true;
            }
            if(parameters.get(prefix + "www", "").equals("accept"))
            {
                publishedOrg.setWww(proposedOrg.getWww());
                anyAccepted = true;
            }
            else if(parameters.get(prefix + "www", "").equals("reject"))
            {
                proposedOrg.setWww(publishedOrg.getWww());
                anyRejected = true;
            }
            
            if(i >= publishedOrganizations.size() && !publishedOrg.isBlank())
            {
                publishedOrganizations.add(publishedOrg);                
            }
            if(i >= publishedOrganizations.size() && publishedOrg.isBlank() && !anyAccepted && anyRejected)
            {
                toRemove.add(proposedOrg);
            }
        }
        proposedOrganizations.removeAll(toRemove);
        return anyOrgsUpdated;
    }

    @Override
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        try
        {
            Parameters requestParameters = context.getAttribute(RequestParameters.class);
            CoralSession coralSession = context.getAttribute(CoralSession.class);
            Subject userSubject = coralSession.getUserSubject();

            long id = requestParameters.getLong("doc_id", -1);
            Resource node = NavigationNodeResourceImpl.getNavigationNodeResource(coralSession, id);
            Permission modifyPermission = coralSession.getSecurity().getUniquePermission(
                "cms.structure.modify");
            Permission modifyOwnPermission = coralSession.getSecurity().getUniquePermission(
                "cms.structure.modify_own");
            if(userSubject.hasPermission(node, modifyPermission))
            {
                return true;
            }
            if(node.getOwner().equals(userSubject)
                && userSubject.hasPermission(node, modifyOwnPermission))
            {
                return true;
            }
            return false;
        }
        catch(Exception e)
        {
            throw new ProcessingException("Exception occured during access rights checking ", e);
        }
    }

    /**
     * @{inheritDoc
     */
    @Override
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
