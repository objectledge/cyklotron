package net.cyklotron.cms;

import org.objectledge.context.Context;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.tools.LinkTool;

import net.cyklotron.cms.site.SiteResource;

/**
 * A link tool used for cms applications, supports site skinning.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: CmsLinkTool.java,v 1.8 2005-05-17 07:40:44 zwierzem Exp $
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
     * Set object...
     * 
     * @param name the name.
     * @param value the value.
     * @return link tool.
     */
    public LinkTool set(String name, Object value)
    {
        if(value == null)
        {
            return set(name, "");
        }
        else
        {
            return set(name, value.toString());
        }
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
