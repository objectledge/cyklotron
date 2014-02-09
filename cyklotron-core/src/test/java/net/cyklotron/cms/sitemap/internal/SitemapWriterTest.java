package net.cyklotron.cms.sitemap.internal;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import junit.framework.TestCase;

import org.objectledge.filesystem.FileSystem;

import net.cyklotron.cms.sitemap.ChangeFrequency;
import net.cyklotron.cms.sitemap.SitemapImage;
import net.cyklotron.cms.sitemap.SitemapItem;

public class SitemapWriterTest
    extends TestCase
{
    public void testWriter()
        throws Exception
    {
        new File("target/sitemap").mkdir();
        FileSystem fs = FileSystem.getStandardFileSystem("target/sitemap");
        SitemapWriter w = new SitemapWriter(fs, "", "domain.tld", false);

        SitemapImage img1 = new SitemapImage(1, new URI("http://domain.tld/images/1.jpg"), null);
        SitemapImage img2 = new SitemapImage(2, new URI("http://domain.tld/images/2.jpg"),
            "img 2 caption");
        SitemapImage img3 = new SitemapImage(3, new URI("http://domain.tld/images/3.jpg"),
            "img 3 caption");

        Collection<SitemapItem> items = new ArrayList<>();
        items.add(new SitemapItem(10, new URI("http://domain.tld/doc/10.html"), null, null, img()));
        items.add(new SitemapItem(20, new URI("http://domain.tld/doc/20.html"), new Date(), null,
            img(img1)));
        items.add(new SitemapItem(30, new URI("http://domain.tld/doc/30.html"), null,
            ChangeFrequency.DAILY, img(img2, img3)));

        w.write(items.iterator());
    }

    private static Collection<SitemapImage> img(SitemapImage... imgs)
    {
        return Arrays.asList(imgs);
    }
}
