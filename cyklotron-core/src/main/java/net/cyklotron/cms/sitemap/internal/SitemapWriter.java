package net.cyklotron.cms.sitemap.internal;

import static java.lang.String.format;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.zip.GZIPOutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.io.output.CountingOutputStream;
import org.objectledge.filesystem.FileSystem;

import net.cyklotron.cms.sitemap.SitemapImage;
import net.cyklotron.cms.sitemap.SitemapItem;

import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;

public class SitemapWriter
{
    private static final int BATCH_ITEMS = 50000;

    private static final int BATCH_BYTES = 50 * 1024 * 1024 - 100 * 1024;

    private static final String SITEMAP_NS = "http://www.sitemaps.org/schemas/sitemap/0.9";

    private static final String SITEMAP_IMAGE_NS = "http://www.google.com/schemas/sitemap-image/1.1";

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'hh:mmXXX";

    private final FileSystem fileSystem;

    private final String basePath;

    private final String domain;

    private final URI baseURI;

    private final XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newFactory();

    private final boolean compress;

    private final DateFormat dateFormat;

    private CountingOutputStream counter;

    public SitemapWriter(FileSystem fileSystem, String basePath, String domain, boolean compress)
    {
        this.fileSystem = fileSystem;
        this.basePath = basePath;
        this.domain = domain;
        this.compress = compress;
        this.dateFormat = new SimpleDateFormat(DATE_FORMAT);
        try
        {
            this.baseURI = new URI(format("http://%s/", domain));
        }
        catch(URISyntaxException e)
        {
            throw new IllegalArgumentException("invalid domain name " + domain, e);
        }
    }

    public void write(Iterator<SitemapItem> items)
        throws IOException, XMLStreamException
    {
        final String suffix = format("%tY%<tm%<td_%<tH%<tM", new Date());
        final String workDir = format("%s/%s_%s", basePath, domain, suffix);
        fileSystem.mkdirs(workDir);

        int seq = 1;
        int cnt = 0;
        LongSet seen = new LongOpenHashSet();
        XMLStreamWriter index = startIndex(workDir);
        XMLStreamWriter batch = startBatch(index, workDir, seq, compress);
        while(items.hasNext())
        {
            final SitemapItem item = items.next();
            final long id = item.getResourceId();
            if(!seen.contains(id))
            {
                seen.add(id);
                if(cnt++ >= BATCH_ITEMS || counter.getCount() >= BATCH_BYTES)
                {
                    end(batch);
                    batch = startBatch(index, workDir, ++seq, compress);
                    cnt = 0;
                }
                writeItem(batch, item);
            }
        }
        end(batch);
        end(index);

        final String targetDir = format("%s/%s", basePath, domain);
        if(fileSystem.exists(targetDir))
        {
            final String oldDir = format("%s/%s_old", basePath, domain);
            fileSystem.rename(targetDir, oldDir);
            fileSystem.rename(workDir, targetDir);
            fileSystem.deleteRecursive(oldDir);
        }
        else
        {
            fileSystem.rename(workDir, targetDir);
        }
    }

    private XMLStreamWriter startIndex(String path)
        throws XMLStreamException
    {
        OutputStream os = fileSystem.getOutputStream(path + "/sitemap_index.xml");
        XMLStreamWriter xsw = xmlOutputFactory.createXMLStreamWriter(os, "UTF-8");
        xsw.writeStartDocument("UTF-8", "1.0");
        xsw.setDefaultNamespace(SITEMAP_NS);
        xsw.writeStartElement("sitemapindex");
        xsw.writeDefaultNamespace(SITEMAP_NS);
        return xsw;
    }

    private XMLStreamWriter startBatch(XMLStreamWriter index, String path, int seq, boolean compress)
        throws IOException, XMLStreamException
    {
        final String filename = "/sitemap_vol" + seq + ".xml" + (compress ? ".gz" : "");
        OutputStream os;
        if(compress)
        {
            os = new GZIPOutputStream(fileSystem.getOutputStream(path + filename), true);
        }
        else
        {
            os = fileSystem.getOutputStream(path + filename);
        }
        counter = new CountingOutputStream(os);
        XMLStreamWriter xsw = xmlOutputFactory.createXMLStreamWriter(counter, "UTF-8");
        xsw.writeStartDocument("UTF-8", "1.0");
        xsw.setDefaultNamespace(SITEMAP_NS);
        xsw.setPrefix("image", SITEMAP_IMAGE_NS);
        xsw.writeStartElement("urlset");
        xsw.writeDefaultNamespace(SITEMAP_NS);
        xsw.writeNamespace("image", SITEMAP_IMAGE_NS);

        index.writeStartElement("sitemap");

        index.writeStartElement("loc");
        index.writeCharacters(baseURI.resolve(filename).toASCIIString());
        index.writeEndElement();

        index.writeEndElement();

        return xsw;
    }

    private void writeItem(XMLStreamWriter xsw, SitemapItem item)
        throws XMLStreamException
    {
        xsw.writeStartElement("url");

        xsw.writeStartElement("loc");
        xsw.writeCharacters(item.getUri().toASCIIString());
        xsw.writeEndElement();

        if(item.getLastModified() != null)
        {
            xsw.writeStartElement("lastmod");
            xsw.writeCharacters(dateFormat.format(item.getLastModified()));
            xsw.writeEndElement();
        }

        if(item.getChangeFrequency() != null)
        {
            xsw.writeStartElement("changefreq");
            xsw.writeCharacters(item.getChangeFrequency().name().toLowerCase());
            xsw.writeEndElement();
        }

        if(item.getImages() != null && !item.getImages().isEmpty())
        {
            for(SitemapImage image : item.getImages())
            {
                xsw.writeStartElement(SITEMAP_IMAGE_NS, "image");

                xsw.writeStartElement(SITEMAP_IMAGE_NS, "loc");
                xsw.writeCharacters(image.getUri().toASCIIString());
                xsw.writeEndElement();

                if(image.getCaption() != null && image.getCaption().trim().length() > 0)
                {
                    xsw.writeStartElement(SITEMAP_IMAGE_NS, "caption");
                    xsw.writeCharacters(image.getCaption());
                    xsw.writeEndElement();
                }

                xsw.writeEndElement();
            }
        }

        xsw.writeEndElement();
    }

    private void end(XMLStreamWriter xsw)
        throws XMLStreamException
    {
        xsw.writeEndDocument();
        xsw.close();
    }
}
