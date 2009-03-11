package net.cyklotron.cms.structure.internal;

import java.text.DateFormat;
import java.util.Date;

import org.jcontainer.dna.Logger;
import org.objectledge.encodings.HTMLEntityEncoder;
import org.objectledge.parameters.Parameters;
import org.objectledge.templating.TemplatingContext;

import net.cyklotron.cms.documents.DocumentNodeResource;

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
    private static final HTMLEntityEncoder ENCODER = new HTMLEntityEncoder();
    DateFormat format = DateFormat.getDateTimeInstance();
    
    String name;
    String title;
    String docAbstract;
    String content;
    String eventPlace;
    String organizedBy;
    String organizedAddress;
    String organizedPhone;
    String organizedFax;
    String organizedEmail;
    String organizedWww;
    String sourceName;
    String sourceUrl;
    String proposerCredentials;
    String proposerEmail;
    String description;
    Date validityStart;
    Date validityEnd;
    Date eventStart;
    Date eventEnd;
    boolean calendarTree;
    boolean inheritCategories;
    long categoryIds[];
    
    private String validationFailure;

    public void fromParameters(Parameters parameters)
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
        
        calendarTree = parameters.getBoolean("calendar_tree", false);
        inheritCategories = parameters.getBoolean("inherit_categories", false);
        
        categoryIds = parameters.getLongs("category_ids");
    }
    
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
        templatingContext.put("calendar_tree", calendarTree);
        templatingContext.put("inherit_categories", inheritCategories);
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
        templatingContext.put("category_ids", categoryIds);        
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
        node.setMeta(getdMeta());
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
    
    public long[] getCategoryIds()
    {
        return categoryIds;
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
    

    private String getdMeta()
    {
        // assemble meta attribute from captured parameters
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
        return buf.toString();
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
