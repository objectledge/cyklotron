package net.cyklotron.cms.documents;

import net.cyklotron.cms.CmsLinkTool;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;

import org.objectledge.pipeline.ProcessingException;

/**
 */
public class RequestLinkRenderer
implements LinkRenderer
{
	private RunData data;
	private CmsLinkTool link;
	private SiteService siteService;

	public RequestLinkRenderer(RunData data)
	{
		this.data = data;
		
		link = (CmsLinkTool)data.getLinkTool();
		link = (CmsLinkTool)(link.unsetAction().app("cms").unsetView());

		siteService = (SiteService)data.getBroker().getService(SiteService.SERVICE_NAME);
	}
	
    public String getFileURL(FileResource file)
    {
    	return null;
    }

	public String getCommonResourceURL(SiteResource site, String path)
	{
		return link.commonResource(path).toString();
	}

	/* (non-Javadoc)
	 * @see net.cyklotron.cms.documents.LinkRenderer#getAbsoluteURL(java.lang.String)
	 */
	public String getAbsoluteURL(SiteResource site, String path)
	{
		// TODO Auto-generated method stub
		return null;
	}

    public String getNodeURL(NavigationNodeResource node)
	throws ProcessingException
    {
		// set a virtual for this link
		StringBuffer newUri = new StringBuffer(256);
		String domain;
        try
        {
            domain = siteService.getPrimaryMapping(node.getSite());
        }
        catch (SiteException e)
        {
        	throw new ProcessingException(
				"Cannot get primary site mapping for node id="+node.getIdString(), e);
        }
		if(domain == null)
		{
			domain = "";
		}
		else if(data.getRequest().isSecure())
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
