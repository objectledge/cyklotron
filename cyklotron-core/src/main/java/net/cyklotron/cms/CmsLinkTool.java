package net.cyklotron.cms;

import net.labeo.LabeoRuntimeException;
import net.labeo.webcore.LinkTool;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.site.SiteResource;

/**
 * A link tool used for cms applications, supports site skinning.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: CmsLinkTool.java,v 1.1 2005-01-12 20:44:52 pablo Exp $
 */
public class CmsLinkTool extends LinkTool
{
    /** current site name */
    private String siteName;

    /** current skin name */
    private String skinName;

    // public interface ///////////////////////////////////////////////////////


    public void reset()
    {
        super.reset();
        siteName = null;
        skinName = null;
    }

    public void prepare(RunData data)
    {
        super.prepare(data);
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
                CmsData cmsData = CmsData.getCmsData(data);
                SiteResource site = cmsData.getSite();
                if(site == null)
                {
                    site = cmsData.getGlobalComponentsDataSite();
                }
                if(site == null)
                {
                    throw new LabeoRuntimeException("No site selected");
                }
                siteName = site.getName();
                skinName = cmsData.getSkinName();
            }
            catch(ProcessingException e)
            {
                throw new LabeoRuntimeException("cannot access CmsData", e);
            }
        }

        CmsLinkTool next = (CmsLinkTool)(resource("sites/"+siteName+"/"+skinName+"/"+path));
        next.app = "cms";
        return next;
    }
}
