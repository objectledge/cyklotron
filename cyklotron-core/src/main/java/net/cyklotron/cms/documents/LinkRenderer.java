/*
 * Created on Oct 30, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.documents;

import org.objectledge.coral.session.CoralSession;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;

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
    public String getFileURL(CoralSession coralSession, FileResource file)
	   throws ProcessingException;

    /**
     * Returns a fully qualified link to a common resource, as defined by link tool.
     * 
     * @param path resource path.
     * @return a fully qualified URL.
     */
	public String getCommonResourceURL(CoralSession coralSession, SiteResource site, String path);

    /**
     * Returns a fully qualified link to a resource on the server.
     * 
     * <p>Note that the path must include context path. This is a serious
     * disadvantage.</p>
     * 
     * @param path the path of the resource on the server.
     * @return a fully qualified URL.
     */
	public String getAbsoluteURL(CoralSession coralSession, SiteResource site, String path);

    /**
     * Returns a fully qualified URL to the specified navigation node.
     * 
     * @param node
     * @return a fully qualified URL to the specified navigation node.
     */
    public String getNodeURL(CoralSession coralSession, NavigationNodeResource node)
        throws ProcessingException;
}
