package net.cyklotron.cms.docimport;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.regex.Pattern;

/**
 * Configuration of remote document import.
 * 
 * @author rafal.krzewski@objectledge.org
 */
public interface ImportSourceConfiguration
{
    /**
     * Location of the remote HTTP resource providing import data.
     * 
     * @throws MalformedURLException
     */
    URL getLocation()
        throws MalformedURLException;

    /** DateFromat object to use for parsing and encoding dates. */
    DateFormat getDateFormat();

    /** Name of the query string parameter for selecting a specific date range. */
    String getDateRangeStartParameter();

    /** Name of the query string parameter for selecting a specific date range. */
    String getDateRangeEndParameter();

    /** XPath for selecting documents in the import XML. */
    String getDocumentXPath();

    /** XPath of the documents original URL, relative to document XPath. */
    String getOriginalURLXPath();

    /** XPath of the document's creation date, relative to document XPath. */
    String getCreationDateXPath();

    /** XPath of the document's modification date, relative to document XPath. */
    String getModificationDateXPath();

    /** XPath of the documents title, relative to document XPath. */
    String getTitleXPath();

    /** Is document title encoded using XML entities? */
    boolean isTitleEntityEncoded();

    /** XPath of the documents abstract, relative to document XPath. */
    String getAbstractXPath();

    /** Is document title abstract using XML entities? */
    boolean isAbstractEntityEncoded();

    /** XPath of the documents content, relative to document XPath. */
    String getContentXPath();

    /** XPath of the documents content, relative to document XPath. */
    boolean isContentEntityEncoded();

    /**
     * XPath of the a document's attachment, relative to document XPath. May yield multiple nodes
     * per item.
     */
    String getAttachentURLXPath();

    /** Is the attachment URL text content a collection of URLs? */
    boolean isAttachmentURLComposite();

    /** Regex pattern for splitting composite attachment URL */
    Pattern getAttachmentURLSeparator();

    /**
     * Transforms an attachment URL. This method is used for testing without network access.
     * 
     * @throws MalformedURLException
     */
    URL transformAttachmentURL(String url)
        throws MalformedURLException;

    /**
     * The name of the HTML cleanup profile configured with HTMLService.
     */
    String getHTMLCleanupProfile();
}
