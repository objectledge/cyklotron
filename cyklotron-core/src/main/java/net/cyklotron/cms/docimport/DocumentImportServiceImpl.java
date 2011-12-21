package net.cyklotron.cms.docimport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.objectledge.encodings.HTMLEntityDecoder;

/**
 * An implementation of DocumentImportService.
 * 
 * @author rafal.krzewski@objectledge.org
 */
public class DocumentImportServiceImpl
    implements DocumentImportService
{
    @Override
    public Collection<DocumentData> importDocuments(ImportSourceConfiguration config, Date start,
        Date end)
        throws IOException
    {
        // SimpleDateFormat is not thread safe
        DateFormat dateFormat = (DateFormat)config.getDateFormat().clone();

        URL requestUrl = buildRequestURL(start, end, config, dateFormat);

        byte[] xmlData = download(requestUrl);

        SAXReader saxReader = new SAXReader(false);
        try
        {
            Document xmlDocument = saxReader.read(new ByteArrayInputStream(xmlData));
            return loadDocuments(xmlDocument, config, dateFormat);
        }
        catch(DocumentException e)
        {
            throw new IOException("malformed xml data", e);
        }
    }

    private Collection<DocumentData> loadDocuments(Document xmlDocument,
        ImportSourceConfiguration config, DateFormat dateFormat)
        throws DocumentException, IOException
    {
        HTMLEntityDecoder dec = new HTMLEntityDecoder();
        Collection<DocumentData> documents = new ArrayList<DocumentData>();
        @SuppressWarnings("unchecked")
        List<Node> documentNodes = xmlDocument.selectNodes(config.getDocumentXPath());
        for(Node documentNode : documentNodes)
        {
            String title = documentNode.selectSingleNode(config.getTitleXPath()).getText();
            if(config.isTitleEntityEncoded())
            {
                title = dec.decode(title);
            }
            String _abstract = documentNode.selectSingleNode(config.getAbstractXPath()).getText();
            if(config.isAbstractEntityEncoded())
            {
                _abstract = dec.decode(_abstract);
            }
            String content = documentNode.selectSingleNode(config.getContentXPath()).getText();
            if(config.isContentEntityEncoded())
            {
                content = dec.decode(content);
            }

            URI originalURI = parseURI(documentNode.selectSingleNode(config.getOriginalURLXPath()));

            Date creationDate = parseDate(
                documentNode.selectSingleNode(config.getCreationDateXPath()), dateFormat);
            Date modificationDate = parseDate(
                documentNode.selectSingleNode(config.getModificationDateXPath()), dateFormat);

            List<AttachmentData> attachments = new ArrayList<AttachmentData>();
            @SuppressWarnings("unchecked")
            List<Node> attachmentNodes = documentNode.selectNodes(config.getAttachentURLXPath());
            for(Node attachmentNode : attachmentNodes)
            {
                final String attachementNodeText = attachmentNode.getText().trim();
                if(attachementNodeText.length() > 0)
                {
                    if(config.isAttachmentURLComposite())
                    {
                        String[] attachmentURLs = config.getAttachmentURLSeparator().split(
                            attachementNodeText);
                        for(String attachmentURL : attachmentURLs)
                        {
                            attachments.add(loadAttachment(attachmentURL, attachmentNode, config));
                        }
                    }
                    else
                    {
                        attachments
                            .add(loadAttachment(attachementNodeText, attachmentNode, config));
                    }
                }
            }

            documents.add(new DocumentData(title, _abstract, content, originalURI, creationDate,
                modificationDate, attachments));
        }

        return documents;
    }

    private AttachmentData loadAttachment(String attachmentURL, Node attachmentNode,
        ImportSourceConfiguration config)
        throws DocumentException, IOException
    {
        try
        {
            URL url = new URL(attachmentURL);
            byte[] contents = download(url);
            return new AttachmentData(url.toURI(), contents);
        }
        catch(MalformedURLException e)
        {
            throw new DocumentException("malformed URL " + attachmentURL + " at "
                + attachmentNode.getUniquePath(), e);
        }
        catch(URISyntaxException e)
        {
            throw new DocumentException("malformed URL " + attachmentURL + " at "
                + attachmentNode.getUniquePath(), e);
        }
    }

    private URI parseURI(Node node)
        throws DocumentException
    {
        try
        {
            return new URI(node.getText());
        }
        catch(URISyntaxException e)
        {
            throw new DocumentException("malformed URI at " + node.getUniquePath(), e);
        }
    }

    private Date parseDate(Node node, DateFormat dateFormat)
        throws DocumentException
    {
        try
        {
            return dateFormat.parse(node.getText());
        }
        catch(ParseException e)
        {
            throw new DocumentException("malformed date at " + node.getUniquePath(), e);
        }
    }

    private URL buildRequestURL(Date start, Date end, ImportSourceConfiguration config,
        DateFormat dateFormat)
        throws MalformedURLException
    {
        URL requestUrl = config.getLocation();
        if(start != null || end != null)
        {
            StringBuilder request = new StringBuilder();
            request.append(requestUrl.toExternalForm());
            request.append("?");
            if(start != null)
            {
                request.append(config.getDateRangeStartParameter());
                request.append("=");
                request.append(dateFormat.format(start));
            }
            if(end != null)
            {
                if(start != null)
                {
                    request.append("&");
                }
                request.append(config.getDateRangeEndParameter());
                request.append("=");
                request.append(dateFormat.format(end));
            }
            requestUrl = new URL(request.toString());
        }
        return requestUrl;
    }

    private byte[] download(URL url)
        throws IOException
    {
        InputStream is = url.openStream();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try
        {
            byte[] buff = new byte[4096];
            int i = 0;
            while(i >= 0)
            {
                i = is.read(buff);
                if(i > 0)
                {
                    os.write(buff, 0, i);
                }
            }
        }
        finally
        {
            is.close();
            os.close();
        }
        return os.toByteArray();
    }
}
