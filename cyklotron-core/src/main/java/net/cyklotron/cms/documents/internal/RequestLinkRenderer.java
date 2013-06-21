package net.cyklotron.cms.documents.internal;

import org.objectledge.coral.session.CoralSession;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.tools.LinkToolFactory;

import net.cyklotron.cms.CmsLinkTool;
import net.cyklotron.cms.documents.LinkRenderer;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 */
public class RequestLinkRenderer
    implements LinkRenderer
{
    private SiteService siteService;
    private HttpContext httpContext;

    private CmsLinkTool link;
    
	public RequestLinkRenderer(SiteService siteService, HttpContext httpContext, LinkToolFactory linkToolFactory)
        throws ProcessingException
	{
        this.siteService = siteService;
        this.httpContext = httpContext;
        
		link = (CmsLinkTool)linkToolFactory.getTool();
		link = (CmsLinkTool)(link.unsetAction().unsetView());
	}
	
    public String getFileURL(CoralSession coralSession, FileResource file)
    {
    	return null;
    }

	public String getCommonResourceURL(CoralSession coralSession, SiteResource site, String path)
	{
		return link.content(path).toString();
	}

	/* (non-Javadoc)
	 * @see net.cyklotron.cms.documents.LinkRenderer#getAbsoluteURL(java.lang.String)
	 */
	public String getAbsoluteURL(CoralSession coralSession, SiteResource site, String path)
	{
		return null;
	}

    public String getNodeURL(CoralSession coralSession, NavigationNodeResource node)
	throws ProcessingException
    {
        return link.setNode(node).toString();
    }
}
