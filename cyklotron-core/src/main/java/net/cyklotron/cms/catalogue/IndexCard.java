package net.cyklotron.cms.catalogue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.files.FileResource;

public class IndexCard
{
    public enum Property
    {
        TITLE,
        SUBTITLE,
        KEYWORDS,
        ABSTRACT,
        EVENT_TITLE,
        EVENT_PLACE,
        EVENT_START,
        EVENT_END,
        VALIDITY_START,
        VALIDITY_END,
        PUB_YEAR,
        AUTHORS,
        SOURCES,
        ORGANIZATIONS,
    }

    // internal state (immutable)

    private final String title;
    
    private final String subtitle;
    
    private final String keywords;

    private final String _abstract;
    
    private final String eventTitle;
    
    private final String eventPlace;
    
    private final String eventStart;

    private final String eventEnd;
    
    private final String validityStart;
    
    private final String validityEnd;

    private final String pubYear;

    private final String authors;
    
    private final String sources;
    
    private final String organizations;
    
    private final DocumentNodeResource descriptionDoc;

    private final List<FileResource> downloads;

    // constructor

    /**
     * Creates a new IndexCard instance. This method expects it's arguments to be problem free,
     * according to
     * {@link CatalogueService#validateIndexCardCandidate(org.objectledge.coral.store.Resource)}.
     * 
     * @param descriptionDoc description documents
     * @param downloads downloadable files
     */
    public IndexCard(DocumentNodeResource descriptionDoc, List<FileResource> downloads)
    {
        this.title = stringValue(descriptionDoc.getTitle());
        this.subtitle = stringValue(descriptionDoc.getSubTitle());
        this.keywords = stringValue(descriptionDoc.getKeywords());
        this._abstract = stringValue(descriptionDoc.getAbstract());
        this.eventTitle = stringValue(descriptionDoc.getTitleCalendar());
        this.eventPlace = stringValue(descriptionDoc.getEventPlace());
        
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        
        this.eventStart = dateValue(descriptionDoc.getEventStart(), df);
        this.eventEnd = dateValue(descriptionDoc.getEventEnd(), df);
        this.validityStart = dateValue(descriptionDoc.getValidityStart(), df);
        this.validityEnd = dateValue(descriptionDoc.getValidityEnd(), df);
        this.pubYear = this.validityStart.length() == 10 ? this.validityStart.substring(0, 4) : "";        
        
        Document metaDOM;
        try
        {
            metaDOM = DocumentHelper.parseText(descriptionDoc.getMeta());
        }
        catch(org.dom4j.DocumentException e)
        {
            throw new RuntimeException("metadata for document #" + descriptionDoc.getIdString()
                + "contains invalid XML", e);
        }

        this.authors = listValue(metaDOM, "/meta/authors/author/name");
        this.sources = listValue(metaDOM, "/meta/sources/source/name");
        this.organizations = listValue(metaDOM, "/meta/organizations/organization/name");
        
        this.descriptionDoc = descriptionDoc;
        this.downloads = downloads;
    }
    
    // constructor's helper methods

    private static String stringValue(String s)
    {
        return s != null ? s : "";
    }
    
    private static String dateValue(Date d, DateFormat df)
    {
        return d != null ? df.format(d) : "";
    }

    private static String listValue(Document metaDOM, String query)
    {
        @SuppressWarnings("unchecked")
        List<Element> elements = metaDOM.selectNodes(query);
        StringBuilder buff = new StringBuilder();
        for(int i = 0; i < elements.size(); i++)
        {
            buff.append(elements.get(i).getTextTrim());
            if(i < elements.size() - 1)
            {
                buff.append(", ");
            }
        }
        return buff.toString();
    }

    // getters

    public String getTitle()
    {
        return title;
    }

    public String getSubtitle()
    {
        return subtitle;
    }

    public String getKeywords()
    {
        return keywords;
    }

    public String getAbstract()
    {
        return _abstract;
    }

    public String getEventTitle()
    {
        return eventTitle;
    }

    public String getEventPlace()
    {
        return eventPlace;
    }

    public String getEventStart()
    {
        return eventStart;
    }

    public String getEventEnd()
    {
        return eventEnd;
    }

    public String getValidityStart()
    {
        return validityStart;
    }

    public String getValidityEnd()
    {
        return validityEnd;
    }

    public String getPubYear()
    {
        return pubYear;
    }

    public String getAuthors()
    {
        return authors;
    }

    public String getSources()
    {
        return sources;
    }

    public String getOrganizations()
    {
        return organizations;
    }

    public DocumentNodeResource getDescriptionDoc()
    {
        return descriptionDoc;
    }

    public List<FileResource> getDownloads()
    {
        return downloads;
    }
    
    // reflective access

    public String getProperty(Property property)
    {
        switch(property)
        {
        case TITLE:
            return title;
        case SUBTITLE:
            return subtitle;
        case KEYWORDS:
            return keywords;
        case ABSTRACT:
            return _abstract;
        case EVENT_TITLE:
            return eventTitle;
        case EVENT_PLACE:
            return eventPlace;
        case EVENT_START:
            return eventStart;
        case EVENT_END:
            return eventEnd;
        case VALIDITY_START:
            return validityStart;
        case VALIDITY_END:
            return validityEnd;
        case PUB_YEAR:
            return pubYear;
        case AUTHORS:
            return authors;
        case SOURCES:
            return sources;
        case ORGANIZATIONS:
            return organizations;
        default:
            throw new IllegalArgumentException(property.name());
        }
    }

    // java.lang.Object method overrides

    /**
     * IndexCard objects are considered equal when their descriptionDocs are equal, hashCode
     * implementation behaves accordingly.
     */
    public int hashCode()
    {
        return descriptionDoc.hashCode();
    }

    /**
     * IndexCard objects are considered equal when their descriptionDocs are equal.
     */
    public boolean equals(Object o)
    {
        if(o instanceof IndexCard)
        {
            return descriptionDoc.equals(((IndexCard)o).descriptionDoc);
        }
        else
        {
            return false;
        }
    }

    public String toString()
    {
        return "IndexCard for " + descriptionDoc.toString();
    }
}
