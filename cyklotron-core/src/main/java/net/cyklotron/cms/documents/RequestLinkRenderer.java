package net.cyklotron.cms.documents;

import net.cyklotron.cms.CmsLinkTool;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;

import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.web.HttpContext;

/**
 */
public class RequestLinkRenderer
    implements LinkRenderer
{
	private Context context;
	private CmsLinkTool link;
	private SiteService siteService;

	public RequestLinkRenderer(SiteService siteService, Context context)
	{
        this.siteService = siteService;
		this.context = context;
        
        //TODO
		//link = (CmsLinkTool)data.getLinkTool();
		//link = (CmsLinkTool)(link.unsetAction().unsetView());
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
		// TODO Auto-generated method stub
		return null;
	}

    public String getNodeURL(CoralSession coralSession, NavigationNodeResource node)
	throws ProcessingException
    {
		// set a virtual for this link
		StringBuffer newUri = new StringBuffer(256);
		String domain;
        try
        {
            domain = siteService.getPrimaryMapping(coralSession, node.getSite());
        }
        catch (SiteException e)
        {
        	throw new ProcessingException(
				"Cannot get primary site mapping for node id="+node.getIdString(), e);
        }
        HttpContext httpContext = HttpContext.getHttpContext(context);
		if(domain == null)
		{
			domain = "";
		}
        else if(httpContext.getRequest().isSecure())
		{
			newUri.append("https://");
		}
		else
		{
			newUri.append("http://");
		}
		newUri.append(domain);
		newUri.append(link.set("x", node.getIdString()).toString());
		
		return newUri.toString();
    }
}
