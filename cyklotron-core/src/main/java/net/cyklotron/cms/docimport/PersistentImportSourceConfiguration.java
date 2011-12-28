package net.cyklotron.cms.docimport;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

/**
 * ImportSourceConfiguration implementation based on {@link ImportResource}.
 * 
 * @author rafal.krzewski@objectledge.org
 */
public class PersistentImportSourceConfiguration
    implements ImportSourceConfiguration
{
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm";

    private static final String DEFAULT_DATE_RANGE_START_PARAMETER = "created_after";

    private static final String DEFAULT_DATE_RANGE_END_PARAMETER = "created_before";

    private static final String DEFAULT_DOCUMENT_XPATH = "/xml/node";

    private static final String DEFAULT_ORIGINAL_URL_XPATH = "URL";

    private static final String DEFAULT_CREATION_DATE_XPATH = "Post_date";

    private static final String DEFAULT_MODIFICATION_DATE_XPATH = "Updated_date";

    private static final String DEFAULT_TITLE_XPATH = "Tytuł";

    private static final boolean DEFAULT_TITLE_ENTITY_ENCODED = true;

    private static final String DEFAULT_ABSTRACT_XPATH = "Teaser";

    private static final boolean DEFAULT_ABSTRACT_ENTITY_ENCODED = true;

    private static final String DEFAULT_CONTENT_XPATH = "Treść";

    private static final boolean DEFAULT_CONTENT_ENTITY_ENCODED = true;

    private static final String DEFAULT_ATTACHMENT_URL_XPATH = "Załączniki";

    private static final boolean DEFAULT_ATTACHMENT_URL_COMPOSITE = true;

    private static final String DEFAULT_ATTACHMENT_URL_SEPARATOR = "\\s+";

    private static final String DEFAULT_HTML_CLEANUP_PROFILE = null;

    private final ImportResource res;

    public PersistentImportSourceConfiguration(ImportResource res)
    {
        this.res = res;
    }

    @Override
    public URL getLocation()
        throws MalformedURLException
    {
        return new URL(res.getLocation());
    }

    @Override
    public String getSourceName()
    {
        return res.getSourceName();
    }

    @Override
    public DateFormat getDateFormat()
    {
        final String dateFormat = res.isDateFormatDefined() ? res.getDateFormat()
            : DEFAULT_DATE_FORMAT;
        return new SimpleDateFormat(dateFormat);
    }

    @Override
    public String getDateRangeStartParameter()
    {
        return res.isDateRangeStartParameterDefined() ? res.getDateRangeStartParameter()
            : DEFAULT_DATE_RANGE_START_PARAMETER;
    }

    @Override
    public String getDateRangeEndParameter()
    {
        return res.isDateRangeEndParameterDefined() ? res.getDateRangeEndParameter()
            : DEFAULT_DATE_RANGE_END_PARAMETER;
    }

    @Override
    public String getDocumentXPath()
    {
        return res.isDocumentXPathDefined() ? res.getDocumentXPath() : DEFAULT_DOCUMENT_XPATH;
    }

    @Override
    public String getOriginalURLXPath()
    {
        return res.isOriginalURLXPathDefined() ? res.getOriginalURLXPath()
            : DEFAULT_ORIGINAL_URL_XPATH;
    }

    @Override
    public String getCreationDateXPath()
    {
        return res.isCreationDateXPathDefined() ? res.getCreationDateXPath()
            : DEFAULT_CREATION_DATE_XPATH;
    }

    @Override
    public String getModificationDateXPath()
    {
        return res.isModificationDateXPathDefined() ? res.getModificationDateXPath()
            : DEFAULT_MODIFICATION_DATE_XPATH;
    }

    @Override
    public String getTitleXPath()
    {
        return res.isTitleXPathDefined() ? res.getTitleXPath() : DEFAULT_TITLE_XPATH;
    }

    @Override
    public boolean isTitleEntityEncoded()
    {
        return res.getTitleEntityEncoded(DEFAULT_TITLE_ENTITY_ENCODED);
    }

    @Override
    public String getAbstractXPath()
    {
        return res.isAbstractXPathDefined() ? res.getAbstractXPath() : DEFAULT_ABSTRACT_XPATH;
    }

    @Override
    public boolean isAbstractEntityEncoded()
    {
        return res.getAbstractEntityEncoded(DEFAULT_ABSTRACT_ENTITY_ENCODED);
    }

    @Override
    public String getContentXPath()
    {
        return res.isContentXPathDefined() ? res.getContentXPath() : DEFAULT_CONTENT_XPATH;
    }

    @Override
    public boolean isContentEntityEncoded()
    {
        return res.getContentEntitytEncoded(DEFAULT_CONTENT_ENTITY_ENCODED);
    }

    @Override
    public String getAttachentURLXPath()
    {
        return res.isAttachentURLXPathDefined() ? res.getAttachentURLXPath()
            : DEFAULT_ATTACHMENT_URL_XPATH;
    }

    @Override
    public boolean isAttachmentURLComposite()
    {
        return res.getAttachmentURLComposite(DEFAULT_ATTACHMENT_URL_COMPOSITE);
    }

    @Override
    public Pattern getAttachmentURLSeparator()
    {
        final String pattern = res.isAttachmentURLSeparatorDefined() ? res
            .getAttachmentURLSeparator() : DEFAULT_ATTACHMENT_URL_SEPARATOR;
        return Pattern.compile(pattern);
    }

    @Override
    public String getHTMLCleanupProfile()
    {
        return res.isHtmlCleanupProfileDefined() ? res.getHtmlCleanupProfile()
            : DEFAULT_HTML_CLEANUP_PROFILE;
    }

    @Override
    public URL transformAttachmentURL(String url)
        throws MalformedURLException
    {
        return new URL(url);
    }
}
