package net.cyklotron.cms.docimport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import junit.framework.TestCase;

public class DocumentImportServiceTest
    extends TestCase
{
    private static final boolean NETWORKING_TESTS = true;

    private DocumentImportServiceImpl service = new DocumentImportServiceImpl();

    private TestImportSourceConfiguration config = new TestImportSourceConfiguration(true, true);

    public void testParsing()
        throws IOException
    {
        Collection<DocumentData> documents = service.loadBatch(config, null, null,
            config.getDateFormat());
        assertEquals(10, documents.size());
        final Iterator<DocumentData> docIterator = documents.iterator();
        assertEquals(4, docIterator.next().getAttachments().size());
        assertEquals(3, docIterator.next().getAttachments().size());
        assertEquals(3, docIterator.next().getAttachments().size());
        assertEquals(0, docIterator.next().getAttachments().size());
    }

    public void testBatching()
        throws IOException
    {
        if(!NETWORKING_TESTS)
        {
            return;
        }

        TestImportSourceConfiguration config = new TestImportSourceConfiguration(false, false);
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(0);
        cal.set(Calendar.YEAR, 2011);
        cal.set(Calendar.MONTH, Calendar.NOVEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date start = cal.getTime();
        cal.set(Calendar.DAY_OF_MONTH, 15);
        Date end = cal.getTime();
        Collection<DocumentData> documents = service.importDocuments(config, start, end);
        assertEquals(21, documents.size());
    }

    /**
     * Download attachments into the source tree for test isolation.
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args)
        throws IOException
    {
        DocumentImportService service = new DocumentImportServiceImpl();
        TestImportSourceConfiguration config = new TestImportSourceConfiguration(true, false);
        Collection<DocumentData> documents = service.importDocuments(config, null, null);
        for(DocumentData doc : documents)
        {
            for(AttachmentData att : doc.getAttachments())
            {
                final String[] split = att.getOriginalURI().getPath().split("/");
                File out = new File("src/test/resources/ngo/um/" + split[split.length - 1]);
                FileOutputStream fos = new FileOutputStream(out);
                fos.write(att.getContents());
                fos.close();
            }
        }
    }
}
