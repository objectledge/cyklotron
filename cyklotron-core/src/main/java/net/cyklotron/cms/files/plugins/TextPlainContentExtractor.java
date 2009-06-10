package net.cyklotron.cms.files.plugins;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: TextPlainContentExtractor.java,v 1.3 2005-02-09 19:22:50 rafal Exp $
 */
public class TextPlainContentExtractor
    implements ContentExtractorPlugin
{
    /**
     * return the file content.
     *
     * @param is the file inputstream.
     * @param encoding the encoding.
     * @return the readable content of the file.
     * @throws IOException if the operation fails.
     */
    public String getContent(InputStream is, String encoding)
        throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[is.available() > 0 ? is.available() : 32];
        int count = 0;
        while(count >= 0)
        {
            count = is.read(buffer,0,buffer.length);
            if(count > 0)
            {
                baos.write(buffer, 0, count);
            }
        }
        return baos.toString(encoding);
    }
    
    public String[] getMimetypes()
    {
        return new String[]{"text/plain"};
    }
}
