package net.cyklotron.cms.modules.actions.structure;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;
import org.objectledge.encodings.HTMLEntityEncoder;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.upload.FileUpload;
import org.objectledge.upload.UploadContainer;
import org.objectledge.upload.UploadLimitExceededException;
import org.objectledge.utils.StackTrace;
import org.objectledge.utils.StringUtils;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryResourceImpl;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.files.DirectoryResource;
import net.cyklotron.cms.files.DirectoryResourceImpl;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.structure.NavigationNodeAlreadyExistException;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;
import net.cyklotron.cms.structure.StructureException;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 * Propose new navigation node in document tree.
 * 
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @author <a href="mailo:mover@caltha.pl">Michal Mach</a>
 * @version $Id: ProposeDocument.java,v 1.21 2008-11-05 23:01:27 rafal Exp $
 */

public class ProposeDocument
    extends BaseAddEditNodeAction
{
    private static final HTMLEntityEncoder ENCODER = new HTMLEntityEncoder();

    private CategoryService categoryService;

    private final FileUpload uploadService;

    private final FilesService filesService;

    private final CoralSessionFactory coralSessionFactory;

    private final RelatedService relatedService;

    public ProposeDocument(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService, CategoryService categoryService,
        FileUpload uploadService, FilesService filesService,
        CoralSessionFactory coralSessionFactory, RelatedService relatedService)
    {
        super(logger, structureService, cmsDataFactory, styleService);
        this.categoryService = categoryService;
        this.uploadService = uploadService;
        this.filesService = filesService;
        this.coralSessionFactory = coralSessionFactory;
        this.relatedService = relatedService;
    }

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        // basic setup

        Subject subject = coralSession.getUserSubject();
        Parameters screenConfig = cmsDataFactory.getCmsData(context).getEmbeddedScreenConfig();
        DocumentNodeResource node = null;
        boolean valid = true;

        try
        {
            // get parameters
            boolean calendarTree = parameters.getBoolean("calendar_tree", false);
            String name = parameters.get("name", "");
            String title = parameters.get("title", "");
            String doc_abstract = parameters.get("abstract", "");
            String content = parameters.get("content", "");
            String event_place = parameters.get("event_place", "");
            String organized_by = parameters.get("organized_by", "");
            String organized_address = parameters.get("organized_address", "");
            String organized_phone = parameters.get("organized_phone", "");
            String organized_fax = parameters.get("organized_fax", "");
            String organized_email = parameters.get("organized_email", "");
            String organized_www = parameters.get("organized_www", "");
            String source = parameters.get("source", "");
            String proposer_credentials = parameters.get("proposer_credentials", "");
            String proposer_email = parameters.get("proposer_email", "");
            String description = parameters.get("description", "");

            // check required parameters
            if(name.equals(""))
            {
                templatingContext.put("result", "navi_name_empty");
                valid = false;
            }
            if(valid && title.equals(""))
            {
                templatingContext.put("result", "navi_title_empty");
                valid = false;
            }
            if(valid && proposer_credentials.equals(""))
            {
                templatingContext.put("result", "proposer_credentials_empty");
                valid = false;
            }
            // file upload - checking
            if(valid)
            {
                valid = checkUploadedFiles(context, templatingContext, screenConfig, valid);
            }

            // find parent node
            long[] parentsId = parameters.getLongs("parent");
            if(valid && parentsId.length == 0)
            {
                templatingContext.put("result", "parent_not_found");
                return;
            }

            long parentId = -1L;
            NavigationNodeResource parent = null;
            if(valid)
            {
                parentId = parentsId[0];

                parent = NavigationNodeResourceImpl.getNavigationNodeResource(coralSession,
                    parentId);

                if(calendarTree && parameters.get("validity_start").length() > 0)
                {
                    parent = structureService.getParent(coralSession, parent, new Date(parameters
                        .getLong("validity_start")),
                        StructureService.DAILY_CALENDAR_TREE_STRUCTURE, subject);
                }
                try
                {
                    // add navigation node
                    node = structureService.addDocumentNode(coralSession, enc(name), enc(title),
                        parent, subject);
                }
                catch(NavigationNodeAlreadyExistException e)
                {
                    templatingContext.put("result", "navi_name_repeated");
                    valid = false;
                }
            }

            if(valid)
            {
                // set attributes to new node
                node.setDescription(enc(description));
                int sequence = getMaxSequence(coralSession, parent);
                node.setSequence(sequence);
                content = setContent(node, content);
                node.setAbstract(enc(doc_abstract));
                setValidity(parameters, node);
                setEventDates(parameters, node);
                node.setEventPlace(enc(event_place));
                String meta = buildMeta(organized_by, organized_address, organized_phone,
                    organized_fax, organized_email, organized_www, source, proposer_credentials,
                    proposer_email);
                node.setMeta(meta);
                setState(coralSession, subject, node);
                // update the node
                structureService.updateNode(coralSession, node, enc(name), true, subject);
                assignCategories(parameters, coralSession, subject, node, parentId);
                uploadAndAttachFiles(node, parameters, screenConfig, coralSession);

                logProposal(parameters, node, title, content, organized_by, organized_address,
                    organized_phone, organized_fax, organized_email, organized_www, source,
                    proposer_credentials, proposer_email);
            }
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            logger.error("StructureException: ", e);
            templatingContext.put("trace", new StackTrace(e));
            valid = false;
        }
        // make the newly created node a current node
        if(valid)
        {
            parameters.set("state", "Result");
            templatingContext.put("result", "added_successfully");
        }
        else
        {
            parameters.set("state", "AddDocument");
        }
    }

    private boolean checkUploadedFiles(Context context, TemplatingContext templatingContext,
        Parameters screenConfig, boolean valid)
        throws ProcessingException
    {
        if(screenConfig.getBoolean("attachments_enabled", false))
        {
            // TODO check if attachment_dir_id is valid, and user has write rights
            int attachmentsMaxCount = screenConfig.getInt("attachments_max_count", 0);
            long attachmentsMaxSize = screenConfig.getLong("attachments_max_size", 0);
            String allowedFormats = screenConfig.get("attachments_allowed_formats", "")
                .toLowerCase();
            List<String> allowedFormatList = Arrays.asList(allowedFormats.split("\\s+"));

            fileCheck: for (int i = 0; i < attachmentsMaxCount; i++)
            {
                try
                {
                    UploadContainer uploadedFile = uploadService.getContainer("attachment_"
                        + (i + 1));
                    if(uploadedFile != null)
                    {
                        if(uploadedFile.getSize() > attachmentsMaxSize * 1024)
                        {
                            templatingContext.put("result", "attachment_size_exceeded"); // i18n
                            valid = false;
                            break fileCheck;
                        }
                        String fileName = uploadedFile.getFileName();
                        String fileExt = fileName.substring(fileName.lastIndexOf('.') + 1).trim()
                            .toLowerCase();
                        if(!allowedFormatList.contains(fileExt))
                        {
                            templatingContext.put("result", "attachment_type_not_allowed"); // i18n
                            valid = false;
                            break fileCheck;
                        }
                    }
                }
                catch(UploadLimitExceededException e)
                {
                    templatingContext.put("result", "upload_size_exceeded"); // i18n
                    valid = false;
                    break fileCheck;
                }
            }
        }
        return valid;
    }

    private void uploadAndAttachFiles(DocumentNodeResource node, Parameters parameters, Parameters screenConfig,
        CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            if(screenConfig.getBoolean("attachments_enabled", false))
            {
                int attachmentsMaxCount = screenConfig.getInt("attachments_max_count", 0);
                ResourceList<FileResource> attachments = new ResourceList<FileResource>(coralSessionFactory);
                for (int i = 0; i < attachmentsMaxCount; i++)
                {
                    UploadContainer file = uploadService.getContainer("attachment_" + (i + 1));
                    if(file != null)
                    {
                        String description = parameters.get("attachment_description_" + (i + 1));
                        long dirId = screenConfig.getLong("attachments_dir_id", -1);
                        DirectoryResource dir = DirectoryResourceImpl.getDirectoryResource(
                            coralSession, dirId);
                        FileResource f = filesService.createFile(coralSession,
                            getAttachmentName(file.getFileName()), file.getInputStream(), file
                                .getMimeType(), null, dir);
                        f.setDescription(description);
                        f.update();
                        attachments.add(f);
                    }
                }
                relatedService.setRelatedTo(coralSession, node, attachments.toArray(new Resource[attachments.size()])); 
                node.setRelatedResourcesSequence(attachments);
                node.update();
            }
        }
        catch(Exception e)
        {
            throw new ProcessingException("problem while processing attachments", e);
        }
    }

    private String getAttachmentName(String fileName)
    {
        StringBuilder buff = new StringBuilder();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        buff.append(df.format(new Date())); // timestamp
        buff.append("_"); // separator
        fileName = StringUtils.iso1toUtf8(fileName);
        fileName = StringUtils.unaccentLatinChars(fileName); // unaccent accented latin characters
        fileName = fileName.replaceAll("[^A-Za-z0-9-_.]+", "_"); // squash everything except alphanumerics and allowed punctuation
        fileName = fileName.replaceAll("_{2,}", "_"); // contract sequences of multiple _
        buff.append(fileName);
        return buff.toString();
    }

    private void setEventDates(Parameters parameters, DocumentNodeResource node)
    {
        // handle dates

        if(parameters.get("event_start").length() > 0)
        {
            Date event_start = null;
            event_start = new Date(parameters.getLong("event_start"));
            node.setEventStart(event_start);
        }
        else
        {
            node.setEventStart(null);
        }

        if(parameters.get("event_end").length() > 0)
        {
            Date event_end = null;
            event_end = new Date(parameters.getLong("event_end"));
            node.setEventEnd(event_end);
        }
        else
        {
            node.setEventEnd(null);
        }
    }

    private void setState(CoralSession coralSession, Subject subject, DocumentNodeResource node)
        throws StructureException
    {
        // set the state to taken if user is redactor (logged in)
        Permission permission = coralSession.getSecurity().getUniquePermission(
            "cms.structure.modify_own");
        if(subject.hasPermission(node, permission))
        {
            structureService.enterState(coralSession, node, "taken", subject);
        }
        else
        {
            structureService.enterState(coralSession, node, "new", subject);
        }
    }

    private void logProposal(Parameters parameters, DocumentNodeResource node, String title,
        String content, String organized_by, String organized_address, String organized_phone,
        String organized_fax, String organized_email, String organized_www, String source,
        String proposer_credentials, String proposer_email)
    {
        // build proposals log
        StringBuilder proposalsDump = new StringBuilder();
        proposalsDump.append("----------------------------------\n");
        proposalsDump.append("-----------------------------------\n");
        proposalsDump.append("Document id: " + node.getIdString() + "\n");
        proposalsDump.append("Document path: " + node.getPath() + "\n");
        proposalsDump.append("Created: " + node.getCreationTime() + "\n");
        proposalsDump.append("Created by: " + node.getCreatedBy().getName() + "\n");
        proposalsDump.append("Document title: " + title + "\n");
        if(parameters.get("event_start").length() > 0)
        {
            proposalsDump.append("Event start: "
                + new Date(parameters.getLong("event_start")).toString() + "\n");
        }
        else
        {
            proposalsDump.append("Event start: Undefined \n");
        }
        if(parameters.get("event_end").length() > 0)
        {
            proposalsDump.append("Event end: "
                + new Date(parameters.getLong("event_end")).toString() + "\n");
        }
        else
        {
            proposalsDump.append("Event end: Undefined \n");
        }
        if(parameters.get("validity_start").length() > 0)
        {
            proposalsDump.append("Document validity start: "
                + new Date(parameters.getLong("validity_start")).toString() + "\n");
        }
        else
        {
            proposalsDump.append("Document validity start: Undefined \n");
        }
        if(parameters.get("validity_end").length() > 0)
        {
            proposalsDump.append("Document validity end: "
                + new Date(parameters.getLong("validity_end")).toString() + "\n");
        }
        else
        {
            proposalsDump.append("Document validity end: Undefined \n");
        }
        proposalsDump.append("Organized by: " + organized_by + "\n");
        proposalsDump.append("Organizer address: " + organized_address + "\n");
        proposalsDump.append("Organizer phone: " + organized_phone + "\n");
        proposalsDump.append("Organizer fax: " + organized_fax + "\n");
        proposalsDump.append("Organizer email: " + organized_email + "\n");
        proposalsDump.append("Organizer URL: " + organized_www + "\n");
        proposalsDump.append("Source: " + source + "\n");
        proposalsDump.append("Proposer credentials: " + proposer_credentials + "\n");
        proposalsDump.append("Proposer email: " + proposer_email + "\n");
        proposalsDump.append("Administrative description: " + proposer_email + "\n");
        proposalsDump.append("Content: \n" + content + "\n");
        logger.debug(proposalsDump.toString());
    }

    private void assignCategories(Parameters parameters, CoralSession coralSession,
        Subject subject, DocumentNodeResource node, long parentId)
        throws EntityDoesNotExistException
    {
        long[] catIds = parameters.getLongs("category_id");
        List<Long> catIdsList = new ArrayList<Long>();
        for (long id : catIds)
        {
            if(id != -1)
            {
                catIdsList.add(id);
            }
        }
        boolean inheritCategories = parameters.getBoolean("inherit_categories", false);

        NavigationNodeResource parent;
        if(inheritCategories || catIdsList.size() > 0)
        {
            Relation refs = categoryService.getResourcesRelation(coralSession);
            RelationModification diff = new RelationModification();
            Permission classifyPermission = coralSession.getSecurity().getUniquePermission(
                "cms.category.classify");
            if(inheritCategories)
            {
                parent = NavigationNodeResourceImpl.getNavigationNodeResource(coralSession,
                    parentId);
                CategoryResource[] categories = categoryService.getCategories(coralSession, parent,
                    false);
                for (int i = 0; i < categories.length; i++)
                {
                    if(subject.hasPermission(categories[i], classifyPermission))
                    {
                        diff.add(categories[i], node);
                    }
                }
            }
            for (long id : catIdsList)
            {
                CategoryResource categoryResource = CategoryResourceImpl.getCategoryResource(
                    coralSession, id);
                if(subject.hasPermission(categoryResource, classifyPermission))
                {
                    diff.add(categoryResource, node);
                }
            }
            coralSession.getRelationManager().updateRelation(refs, diff);
        }
    }

    private String setContent(DocumentNodeResource node, String content)
    {
        content = content.replaceAll("\r\n", "\n");
        content = content.replaceAll("\n", "</p>\n<p>");
        content = "<p>" + content + "</p>";
        content = content.replaceAll("<p>\\s*</p>", "");
        node.setContent(content);
        return content;
    }

    private int getMaxSequence(CoralSession coralSession, NavigationNodeResource parent)
    {
        // get greatest sequence number to put new node on top of
        // sequence-sorted list
        int sequence = 0;
        Resource[] children = coralSession.getStore().getResource(parent);
        for (int i = 0; i < children.length; i++)
        {
            Resource child = children[i];
            if(child instanceof NavigationNodeResource)
            {
                int childSeq = ((NavigationNodeResource)child).getSequence(0);
                sequence = sequence < childSeq ? childSeq : sequence;
            }
        }
        return sequence;
    }

    private String buildMeta(String organized_by, String organized_address, String organized_phone,
        String organized_fax, String organized_email, String organized_www, String source,
        String proposer_credentials, String proposer_email)
    {
        // assemble meta attribute from captured parameters
        StringBuilder buf = new StringBuilder();
        buf.append("<meta><authors><author><name>");
        buf.append(enc(proposer_credentials));
        buf.append("</name><e-mail>");
        buf.append(enc(proposer_email));
        buf.append("</e-mail></author></authors>");
        buf.append("<sources><source><name>");
        buf.append(enc(source));
        buf.append("</name><url>http://</url></source></sources>");
        buf.append("<editor></editor><organisation><name>");
        buf.append(enc(organized_by));
        buf.append("</name><address>");
        buf.append(enc(organized_address));
        buf.append("</address><tel>");
        buf.append(enc(organized_phone));
        buf.append("</tel><fax>");
        buf.append(enc(organized_fax));
        buf.append("</fax><e-mail>");
        buf.append(enc(organized_email));
        buf.append("</e-mail><url>");
        buf.append(enc(organized_www));
        buf.append("</url><id>0</id></organisation></meta>");
        return buf.toString();
    }

    protected String getViewName()
    {
        return "";
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);
        try
        {
            Permission permission = coralSession.getSecurity().getUniquePermission(
                "cms.structure.submit");
            Resource node = coralSession.getStore().getResource(
                parameters.getLong("parent_node_id", -1));
            return coralSession.getUserSubject().hasPermission(node, permission);
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

    private String enc(String s)
    {
        s = s.replaceAll("<[^>]*?>", " "); // strip html tags
        return ENCODER.encodeAttribute(s, "UTF-16");
    }
}
