package net.cyklotron.cms.files.plugins;

import java.io.IOException;
import java.io.InputStream;


/**
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: ContentExtractorPlugin.java,v 1.2 2005-01-18 10:01:16 pablo Exp $
 */
public interface ContentExtractorPlugin
{
    /**
     * return the file content.
     *
     * @param is the file inputstream.
     * @param encoding the encoding.
     * @return the readable content of the file.
     * @throws FilesException.
     */
    public String getContent(InputStream is, String encoding)
        throws IOException;
    
    /**
     * get the mimetypes handled by this plugin.
     * 
     * @return the array of mimetypes.
     */
    public String[] getMimetypes();
}
