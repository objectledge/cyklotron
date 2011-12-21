package net.cyklotron.cms.docimport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;

public class DocumentImportServiceTest
    extends TestCase
{
    private DocumentImportService service = new DocumentImportServiceImpl();

    private TestImportSourceConfiguration config = new TestImportSourceConfiguration(true);

    public void testParsing()
        throws IOException
    {
        Collection<DocumentData> documents = service.importDocuments(config, null, null);
        assertEquals(10, documents.size());
        final Iterator<DocumentData> docIterator = documents.iterator();
        assertEquals(4, docIterator.next().getAttachments().size());
        assertEquals(3, docIterator.next().getAttachments().size());
        assertEquals(3, docIterator.next().getAttachments().size());
        assertEquals(0, docIterator.next().getAttachments().size());
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
        TestImportSourceConfiguration config = new TestImportSourceConfiguration(false);
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
