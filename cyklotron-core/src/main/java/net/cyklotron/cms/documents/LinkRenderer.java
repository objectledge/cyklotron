/*
 * Created on Oct 30, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.documents;

import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;

import org.objectledge.pipeline.ProcessingException;

/**
 * @author fil
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface LinkRenderer
{
    /**
     * Returns a fully qualified URL to the specified file.
     * 
     * @param file the file.
     * @return a fully qualified URL.
     */    
    public String getFileURL(FileResource file)
	   throws ProcessingException;

    /**
     * Returns a fully qualified link to a common resource, as defined by
     * labeo link tool.
     * 
     * @param path resource path.
     * @return a fully qualified URL.
     */
	public String getCommonResourceURL(SiteResource site, String path);

    /**
     * Returns a fully qualified link to a resource on the server.
     * 
     * <p>Note that the path must include context path. This is a serious
     * disadvantage.</p>
     * 
     * @param path the path of the resource on the server.
     * @return a fully qualified URL.
     */
	public String getAbsoluteURL(SiteResource site, String path);

    /**
     * Returns a fully qualified URL to the specified navigation node.
     * 
     * @param node
     * @return 
     */
    public String getNodeURL(NavigationNodeResource node)
        throws ProcessingException;
}
