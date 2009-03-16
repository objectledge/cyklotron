package net.cyklotron.cms.structure.internal;

import static net.cyklotron.cms.documents.HTMLUtil.getFirstText;
import static net.cyklotron.cms.documents.HTMLUtil.parseXmlAttribute;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dom4j.Document;
import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.encodings.HTMLEntityEncoder;
import org.objectledge.parameters.Parameters;
import org.objectledge.templating.TemplatingContext;

import net.cyklotron.cms.CmsNodeResource;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryResourceImpl;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.documents.DocumentException;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.related.RelatedService;

/**
 * Data object used by ProposeDocument view and action.
 * <p>
 * Feels kind like breaking open door, but I'm not willing to learn formtool just to make one silly
 * screen.
 * </p>
 * 
 * <p>
 * Not threadsafe, but there should be no need to share this object among thread.
 * </p>
 * 
 * @author rafal
 */
public class ProposedDocumentData
{
    // component configuration
    private boolean calendarTree;
    private boolean inheritCategories;
    private boolean attachmentsEnabled;
    private int attachmentsMaxCount;
    private int attachmentsMaxSize;
    private String attachmentsAllowedFormats;
    
    // form data    
    private String name;
    private String title;
    private String docAbstract;
    private String content;
    private String eventPlace;
    private String organizedBy;
    private String organizedAddress;
    private String organizedPhone;
    private String organizedFax;
    private String organizedEmail;
    private String organizedWww;
    private String sourceName;
    private String sourceUrl;
    private String proposerCredentials;
    private String proposerEmail;
    private String description;
    private Date validityStart;
    private Date validityEnd;
    private Date eventStart;
    private Date eventEnd;
    private Set<CategoryResource> availableCategories;
    private Set<CategoryResource> selectedCategories;
    private List<Resource> attachments;
    private List<String> attachmentDescriptions;

    // validation
    private String validationFailure;

    // helper objects
    private static final HTMLEntityEncoder ENCODER = new HTMLEntityEncoder();
    private final DateFormat format = DateFormat.getDateTimeInstance();

    public ProposedDocumentData(Parameters configuration)
    {
        calendarTree = configuration.getBoolean("calendar_tree", true);
        inheritCategories = configuration.getBoolean("inherit_categories", true);
        
        attachmentsEnabled = configuration.getBoolean("attachments_enabled", false);
        attachmentsMaxCount = configuration.getInt("attachments_max_count", 0);
        attachmentsMaxSize = configuration.getInt("attachments_max_size", 0);
        attachmentsAllowedFormats = configuration.get(
            "attachments_allowed_formats", "jpg gif doc rtf pdf xls");
    }
    
    public void fromParameters(Parameters parameters, CoralSession coralSession)
        throws EntityDoesNotExistException
    {
        name = parameters.get("name", "");
        title = parameters.get("title", "");
        docAbstract = parameters.get("abstract", "");
        content = parameters.get("content", "");
        eventPlace = parameters.get("event_place", "");
        organizedBy = parameters.get("organized_by", "");
        organizedAddress = parameters.get("organized_address", "");
        organizedPhone = parameters.get("organized_phone", "");
        organizedFax = parameters.get("organized_fax", "");
        organizedEmail = parameters.get("organized_email", "");
        organizedWww = parameters.get("organized_www", "");
        sourceName = parameters.get("source_name", "");
        sourceUrl = parameters.get("source_url", "");
        proposerCredentials = parameters.get("proposer_credentials", "");
        proposerEmail = parameters.get("proposer_email", "");
        description = parameters.get("description", "");
        
        validityStart = getDate(parameters, "validity_start");
        validityEnd = getDate(parameters, "validity_end");
        eventStart = getDate(parameters, "event_start");
        eventEnd = getDate(parameters, "event_end");
        
        selectedCategories = new HashSet<CategoryResource>();
        for(long categoryId : parameters.getLongs("selected_categories"))
        {   
            if(categoryId != -1)
            {
                selectedCategories.add(CategoryResourceImpl.getCategoryResource(coralSession, categoryId));
            }
        }

        availableCategories = new HashSet<CategoryResource>();
        for(long categoryId : parameters.getLongs("available_categories"))
        {
            availableCategories.add(CategoryResourceImpl.getCategoryResource(coralSession, categoryId));
        }        
        
        if(attachmentsEnabled)
        {
            attachmentDescriptions = new ArrayList<String>(attachmentsMaxCount);
            for(int i = 1; i <= attachmentsMaxCount; i++)
            {
                attachmentDescriptions.add(parameters.get("attachment_description_"+i));
            }
        }
    }

    /**
     * Transfers the data into the templating context. 
     * 
     * <p>
     * This is needed to keep the exiting templates working
     * </p>
     * 
     * @param templatingContext
     */
    public void toTemplatingContext(TemplatingContext templatingContext)
    {
        templatingContext.put("name", name);
        templatingContext.put("title", title);
        templatingContext.put("abstract", docAbstract);
        templatingContext.put("content", content);
        templatingContext.put("event_place", eventPlace);
        templatingContext.put("organized_by", organizedBy);
        templatingContext.put("organized_address", organizedAddress);
        templatingContext.put("organized_phone", organizedPhone);
        templatingContext.put("organized_fax", organizedFax);
        templatingContext.put("organized_email", organizedEmail);
        templatingContext.put("organized_www", organizedWww);
        templatingContext.put("source_name", sourceName);
        templatingContext.put("source_url", sourceUrl);
        templatingContext.put("proposer_credentials", proposerCredentials);
        templatingContext.put("proposer_email", proposerEmail);
        templatingContext.put("description", description);
        setDate(templatingContext, "validity_start", validityStart);
        setDate(templatingContext, "validity_end", validityEnd);
        setDate(templatingContext, "event_start", eventStart);
        setDate(templatingContext, "event_end", eventEnd);
        templatingContext.put("selected_categories", selectedCategories);    
        if(attachmentsEnabled)
        {
            templatingContext.put("attachments_enabled", attachmentsEnabled);
            templatingContext.put("attachments_max_count", attachmentsMaxCount);
            int remaining = attachmentsMaxCount - attachments.size();
            remaining = remaining >= 0 ? remaining : 0;
            templatingContext.put("attachments_remaining_count", remaining);
            templatingContext.put("attachments_max_size", attachmentsMaxSize);
            templatingContext.put("attachments_allowed_formats", attachmentsAllowedFormats);
            templatingContext.put("current_attachments", attachments);
            templatingContext.put("attachment_descriptions", attachmentDescriptions);
        }
    }
    
    public void fromNode(DocumentNodeResource node, CategoryService categoryService,
        RelatedService relatedService, CoralSession coralSession)
    {
        // calendarTree
        // inheritCategories
        name = node.getName();
        title = node.getTitle();
        docAbstract = node.getAbstract();
        content = node.getContent();
        description = node.getDescription();
        validityStart = node.getValidityStart();
        validityEnd = node.getValidityEnd();        
        eventPlace = node.getEventPlace();
        eventStart = node.getEventStart();
        eventEnd = node.getEventEnd();
        try
        {
            Document metaDom = parseXmlAttribute(node.getMeta(), "meta");
            organizedBy = getFirstText(metaDom, "/meta/organisation/name");
            organizedAddress = getFirstText(metaDom, "/meta/organisation/address");
            organizedPhone = getFirstText(metaDom, "/meta/organisation/tel");
            organizedFax = getFirstText(metaDom, "/meta/organisation/fax");
            organizedEmail = getFirstText(metaDom, "/meta/organisation/e-mail");
            organizedWww = getFirstText(metaDom, "/meta/organisation/url");
            sourceName = getFirstText(metaDom, "/meta/sources/source/name");
            sourceUrl = getFirstText(metaDom, "/meta/sources/source/url");
            proposerCredentials = getFirstText(metaDom, "/meta/authors/author/name");
            proposerEmail = getFirstText(metaDom, "/meta/authors/author/e-mail");
        }
        catch(DocumentException e)
        {
            throw new RuntimeException("malformed metadada in resource "+node.getIdString(), e);
        }
        selectedCategories = new HashSet<CategoryResource>(Arrays.asList(categoryService
            .getCategories(coralSession, node, false)));
        if(attachmentsEnabled)
        {
            attachments = new ArrayList<Resource>(Arrays.asList(relatedService.getRelatedTo(coralSession,
                node, node.getRelatedResourcesSequence(), null)));
            attachmentDescriptions = new ArrayList<String>(attachmentsMaxCount);
            for(Resource attachment : attachments)
            {
                if(attachment instanceof CmsNodeResource)
                {
                    attachmentDescriptions.add(((CmsNodeResource)attachment).getDescription());
                }
                else
                {
                    attachmentDescriptions.add("");
                }
            }
            // fill up with empty strings to make template logic more simple
            while(attachmentDescriptions.size() < attachmentsMaxCount)
            {
                attachmentDescriptions.add("");
            }
        }
    }
    
    public void toNode(DocumentNodeResource node)
    {
        // set attributes to new node
        node.setDescription(enc(description));
        content = setContent(node, content);
        node.setAbstract(enc(docAbstract));
        node.setValidityStart(validityStart);
        node.setValidityEnd(validityEnd);
        node.setEventStart(eventStart);
        node.setEventEnd(eventEnd);
        node.setEventPlace(enc(eventPlace));
        StringBuilder buf = new StringBuilder();
        buf.append("<meta><authors><author><name>");
        buf.append(enc(proposerCredentials));
        buf.append("</name><e-mail>");
        buf.append(enc(proposerEmail));
        buf.append("</e-mail></author></authors>");
        buf.append("<sources><source><name>");
        buf.append(enc(sourceName));
        buf.append("</name><url>");
        buf.append(enc(sourceUrl));
        buf.append("</url></source></sources>");
        buf.append("<editor></editor><organisation><name>");
        buf.append(enc(organizedBy));
        buf.append("</name><address>");
        buf.append(enc(organizedAddress));
        buf.append("</address><tel>");
        buf.append(enc(organizedPhone));
        buf.append("</tel><fax>");
        buf.append(enc(organizedFax));
        buf.append("</fax><e-mail>");
        buf.append(enc(organizedEmail));
        buf.append("</e-mail><url>");
        buf.append(enc(organizedWww));
        buf.append("</url><id>0</id></organisation></meta>");
        node.setMeta(buf.toString());
    }

    // validation
    
    public boolean isValid()
    {
        if(name.equals(""))
        {
            setValidationFailure("navi_name_empty");
            return false;
        }
        if(title.equals(""))
        {
            setValidationFailure("navi_title_empty");
            return false;
        }
        if(proposerCredentials.equals(""))
        {
            setValidationFailure("proposer_credentials_empty");
            return false;
        } 
        return true;
    }
    
    // getters
       
    public String getName()
    {
        return enc(name);
    }
    
    public String getTitle()
    {
        return enc(title);
    }    

    public Date getValidityStart()
    {
        return validityStart;
    }
    
    public boolean isCalendarTree()
    {
        return calendarTree;
    }

    public boolean isInheritCategories()
    {
        return inheritCategories;
    }

    public Set<CategoryResource> getSelectedCategories()
    {
        return selectedCategories;
    }
    
    public Set<CategoryResource> getAvailableCategories()
    {
        return availableCategories;
    }
    
    // utitily

    public void setValidationFailure(String validationFailure)
    {
        this.validationFailure = validationFailure;
    }

    public String getValidationFailure()
    {
        return validationFailure;
    }

    private Date getDate(Parameters parameters, String key)
    {
        if(parameters.isDefined(key) && parameters.get(key).trim().length() > 0)
        {
            return parameters.getDate(key);
        }
        else
        {
            return null;
        }
    }
    
    private void setDate(TemplatingContext templatingContext, String key, Date value)
    {
        if(value != null)
        {
            templatingContext.put(key, value.getTime());
        }
    }
    
    private String formatDate(Date date)
    {
        if(date != null)
        {
            return format.format(date);
        }
        else
        {
            return "Undefined";
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
       
    private String enc(String s)
    {
        s = s.replaceAll("<[^>]*?>", " "); // strip html tags
        return ENCODER.encodeAttribute(s, "UTF-16");
    }    
    
    public void logProposal(Logger logger, DocumentNodeResource node)
    {
        // build proposals log
        StringBuilder proposalsDump = new StringBuilder();
        proposalsDump.append("----------------------------------\n");
        proposalsDump.append("Document id: ").append(node.getIdString()).append("\n");
        proposalsDump.append("Document path: ").append(node.getPath()).append("\n");
        proposalsDump.append("Created: ").append(node.getCreationTime()).append("\n");
        proposalsDump.append("Created by: ").append(node.getCreatedBy().getName()).append("\n");
        proposalsDump.append("Document title: ").append(title).append("\n");
        proposalsDump.append("Event start: ").append(formatDate(eventStart)).append("\n");
        proposalsDump.append("Event end: ").append(formatDate(eventEnd)).append("\n");
        proposalsDump.append("Document validity start: ").append(formatDate(validityStart)).append(
            "\n");
        proposalsDump.append("Document validity end: ").append(formatDate(validityEnd))
            .append("\n");
        proposalsDump.append("Organized by: ").append(organizedBy).append("\n");
        proposalsDump.append("Organizer address: ").append(organizedAddress).append("\n");
        proposalsDump.append("Organizer phone: ").append(organizedPhone).append("\n");
        proposalsDump.append("Organizer fax: ").append(organizedFax).append("\n");
        proposalsDump.append("Organizer email: ").append(organizedEmail).append("\n");
        proposalsDump.append("Organizer URL: ").append(organizedWww).append("\n");
        proposalsDump.append("Source name: ").append(sourceName).append("\n");
        proposalsDump.append("Source URL: ").append(sourceUrl).append("\n");
        proposalsDump.append("Proposer credentials: ").append(proposerCredentials).append("\n");
        proposalsDump.append("Proposer email: ").append(proposerEmail).append("\n");
        proposalsDump.append("Administrative description: ").append(proposerEmail).append("\n");
        proposalsDump.append("Content: \n").append(content).append("\n");
        logger.debug(proposalsDump.toString());
    }
}
