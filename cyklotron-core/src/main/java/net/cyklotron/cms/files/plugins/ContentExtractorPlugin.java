package net.cyklotron.cms.files.plugins;

import java.io.IOException;
import java.io.InputStream;


/**
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: ContentExtractorPlugin.java,v 1.3 2005-02-09 19:22:50 rafal Exp $
 */
public interface ContentExtractorPlugin
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
        throws IOException;
    
    /**
     * get the mimetypes handled by this plugin.
     * 
     * @return the array of mimetypes.
     */
    public String[] getMimetypes();
}
