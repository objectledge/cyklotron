package net.cyklotron.cms.library;

import java.text.SimpleDateFormat;
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
        TITLE, AUTHORS, PUB_YEAR, KEYWORDS
    }

    // internal state (immutable)

    private final String title;

    private final String authors;

    private final String pubYear;

    private final String keywords;

    private final DocumentNodeResource descriptionDoc;

    private final List<FileResource> downloads;

    // constructor

    /**
     * Creates a new IndexCard instance. This method expects it's arguments to be problem free,
     * according to
     * {@link LibraryService#validateIndexCardCandidate(org.objectledge.coral.store.Resource)}.
     * 
     * @param descriptionDoc description documents
     * @param downloads downloadable files
     */
    public IndexCard(DocumentNodeResource descriptionDoc, List<FileResource> downloads)
    {
        this.title = descriptionDoc.getTitle();
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
        @SuppressWarnings("unchecked")
        List<Element> authorNames = metaDOM.selectNodes("/meta/authors/author/name");
        StringBuilder buff = new StringBuilder();
        for(int i = 0; i < authorNames.size(); i++)
        {
            buff.append(authorNames.get(i));
            if(i < authorNames.size() - 1)
            {
                buff.append(", ");
            }
        }
        this.authors = buff.toString();
        SimpleDateFormat df = new SimpleDateFormat("yyyy");
        this.pubYear = df.format(descriptionDoc.getValidityStart());
        this.keywords = descriptionDoc.getKeywords();
        this.descriptionDoc = descriptionDoc;
        this.downloads = downloads;
    }

    // getters

    public String getTitle()
    {
        return title;
    }

    public String getAuthors()
    {
        return authors;
    }

    public String getPubYear()
    {
        return pubYear;
    }

    public String getKeywords()
    {
        return keywords;
    }

    public DocumentNodeResource getDescriptionDoc()
    {
        return descriptionDoc;
    }

    public List<FileResource> getDownloads()
    {
        return downloads;
    }

    public String getProperty(Property property)
    {
        switch(property)
        {
        case TITLE:
            return title;
        case AUTHORS:
            return authors;
        case PUB_YEAR:
            return pubYear;
        case KEYWORDS:
            return keywords;
        default:
            throw new IllegalArgumentException();
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
        return "IndexCard for "+descriptionDoc.toString();
    }
}
