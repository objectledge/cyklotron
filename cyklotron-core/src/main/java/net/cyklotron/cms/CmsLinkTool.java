package net.cyklotron.cms;

import net.cyklotron.cms.site.SiteResource;

import org.objectledge.context.Context;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.tools.LinkTool;

/**
 * A link tool used for cms applications, supports site skinning.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: CmsLinkTool.java,v 1.4 2005-02-03 09:22:43 pablo Exp $
 */
public class CmsLinkTool extends LinkTool
{
    private CmsDataFactory cmsDataFactory;
    
    /** current site name */
    private String siteName;

    /** current skin name */
    private String skinName;
    
    private Context context;

    /**
     * {@inheritDoc}
     */
    protected LinkTool createInstance(LinkTool source)
    {
        return new CmsLinkTool(((CmsLinkTool)source).httpContext,
            ((CmsLinkTool)source).mvcContext, ((CmsLinkTool)source).requestParameters,
            ((CmsLinkTool)source).config, ((CmsLinkTool)source).cmsDataFactory,
            ((CmsLinkTool)source).context);
    }

    // public interface ///////////////////////////////////////////////////////

    /**
     * @param dashboardContext
     * @param httpContext
     * @param mvcContext
     * @param requestParameters
     * @param config
     */
    public CmsLinkTool(HttpContext httpContext, MVCContext mvcContext, 
        RequestParameters requestParameters, LinkTool.Configuration config,
        CmsDataFactory cmsDataFactory, Context context)
    {
        super(httpContext, mvcContext, requestParameters, config);
        this.cmsDataFactory = cmsDataFactory;
        this.context = context;
    }
    
    /**
     * Overrides the link to point to static content in the site's skin.
     *
     * @path the resource to point
     */
    public CmsLinkTool skinResource(String path)
    {
        if(siteName == null)
        {
            try
            {
                CmsData cmsData = cmsDataFactory.getCmsData(context);
                SiteResource site = cmsData.getSite();
                if(site == null)
                {
                    site = cmsData.getGlobalComponentsDataSite();
                }
                if(site == null)
                {
                    throw new RuntimeException("No site selected");
                }
                siteName = site.getName();
                skinName = cmsData.getSkinName();
            }
            catch(ProcessingException e)
            {
                throw new RuntimeException("cannot access CmsData", e);
            }
        }
        CmsLinkTool next = (CmsLinkTool)(content("sites/"+siteName+"/"+skinName+"/"+path));
        return next;
    }
}
