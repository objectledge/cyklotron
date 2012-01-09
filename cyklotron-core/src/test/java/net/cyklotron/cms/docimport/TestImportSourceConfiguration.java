package net.cyklotron.cms.docimport;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

/**
 * An implementation of ImpostSourceConifguration used for tests.
 * 
 * @author rafal.krzewski@objectledge.org
 */
public class TestImportSourceConfiguration
    implements ImportSourceConfiguration
{
    private final boolean localXML;

    private final boolean localAttachments;

    /**
     * Creates test ImportSourceConfiguration instance.
     * 
     * @param localXML should XML data be loaded from disk?
     * @param localAttachments should attachments be loaded from disk?
     */
    public TestImportSourceConfiguration(boolean localXML, boolean localAttachments)
    {
        this.localXML = localXML;
        this.localAttachments = localAttachments;
    }

    @Override
    public URL getLocation()
        throws MalformedURLException
    {
        if(localXML)
        {
            return new File("src/test/resources/ngo/um/aktualnosci.xml").toURI().toURL();
        }
        else
        {
            return new URL("http://ngo.um.warszawa.pl/xml/aktualnosci");
        }
    }

    public String getSourceName()
    {
        return "Strona Urzędu Miasta Stołecznego Warszawy";
    }

    @Override
    public DateFormat getDateFormat()
    {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm");
    }

    @Override
    public String getDateRangeStartParameter()
    {
        return "created_after";
    }

    @Override
    public String getDateRangeEndParameter()
    {
        return "created_before";
    }

    @Override
    public String getDocumentXPath()
    {
        return "/xml/node";
    }

    @Override
    public String getOriginalURLXPath()
    {
        return "URL";
    }

    @Override
    public String getCreationDateXPath()
    {
        return "Post_date";
    }

    @Override
    public String getModificationDateXPath()
    {
        return "Updated_date";
    }

    @Override
    public String getTitleXPath()
    {
        return "Tytuł";
    }

    @Override
    public boolean isTitleEntityEncoded()
    {
        return true;
    }

    @Override
    public String getAbstractXPath()
    {
        return "Teaser";
    }

    @Override
    public boolean isAbstractEntityEncoded()
    {
        return true;
    }

    @Override
    public String getContentXPath()
    {
        return "Treść";
    }

    @Override
    public boolean isContentEntityEncoded()
    {
        return true;
    }

    @Override
    public String getAttachentURLXPath()
    {
        return "Załączniki";
    }

    @Override
    public boolean isAttachmentURLComposite()
    {
        return true;
    }

    @Override
    public Pattern getAttachmentURLSeparator()
    {
        return Pattern.compile("\\s+");
    }

    @Override
    public URL transformAttachmentURL(String url)
        throws MalformedURLException
    {
        if(localAttachments)
        {
            String[] split = url.split("/");
            return new File("src/test/resources/ngo/um/" + split[split.length - 1]).toURI().toURL();
        }
        else
        {
            return new URL(url);
        }
    }

    @Override
    public String getTitleCleanupProfile()
    {
        return "passthrough";
    }
    
    @Override
    public String getAbstractCleanupProfile()
    {
        return "passthrough";
    }
    
    @Override
    public String getContentCleanupProfile()
    {
        return "passthrough";
    }
}
