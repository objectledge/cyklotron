package net.cyklotron.cms;

import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.tools.LinkTool;

import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureException;
import net.cyklotron.cms.structure.StructureService;

/**
 * A link tool used for cms applications, supports site skinning.
 * 
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: CmsLinkTool.java,v 1.11 2008-10-02 15:39:12 rafal Exp $
 */
public class CmsLinkTool
    extends LinkTool
{
    private final CmsDataFactory cmsDataFactory;

    /** current site name */
    private String siteName;

    /** current skin name */
    private String skinName;

    private Context context;

    private final SiteService siteService;

    private final CoralSessionFactory coralSessionFactory;

    private StructureService structureService;

    /**
     * {@inheritDoc}
     */
    protected LinkTool createInstance(LinkTool linkTool)
    {
        final CmsLinkTool cmsLinkTool = (CmsLinkTool)linkTool;
        return new CmsLinkTool(cmsLinkTool.httpContext, cmsLinkTool.context,
            cmsLinkTool.mvcContext, cmsLinkTool.requestParameters, cmsLinkTool.config,
            cmsLinkTool.cmsDataFactory, cmsLinkTool.siteService, cmsLinkTool.structureService,
            cmsLinkTool.coralSessionFactory);
    }

    // public interface ///////////////////////////////////////////////////////

    /**
     * @param httpContext
     * @param mvcContext
     * @param requestParameters
     * @param config
     * @param urlRewriteRegistry TODO
     * @param siteService TODO
     * @param coralSessionFactory TODO
     */
    public CmsLinkTool(HttpContext httpContext, Context context, MVCContext mvcContext,
        RequestParameters requestParameters, LinkTool.Configuration config,
        CmsDataFactory cmsDataFactory, SiteService siteService, StructureService structureService,
        CoralSessionFactory coralSessionFactory)
    {
        super(httpContext, mvcContext, requestParameters, config);
        this.context = context;
        this.cmsDataFactory = cmsDataFactory;
        this.siteService = siteService;
        this.structureService = structureService;
        this.coralSessionFactory = coralSessionFactory;
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
        CmsLinkTool next = (CmsLinkTool)(content("sites/" + siteName + "/" + skinName + "/" + path));
        return next;
    }

    /**
     * Set a parameter that contains an UI element name (view, component or action). When called in
     * CykloKlon this method simply calls set(name,value). When called in Cyklotron this method
     * replaces commas with dots.
     * 
     * @param name name of the parameter.
     * @param value value of the parameter.
     * @return modified link tool instance.
     */
    public LinkTool setUiElementName(String name, String value)
    {
        return set(name, value.replace(",", "."));
    }

    /**
     * Overrides link to point to a specific navigation node.
     * 
     * @param node the navigation node.
     * @return modified link tool instance.
     */
    public LinkTool setNode(NavigationNodeResource node)
    {
        if(node != null)
        {
            final LinkTool hostLink = getHostLink(node);
            final LinkTool link = node.getSite().getRequiresSecureChannel() ? hostLink.https()
                : hostLink;
            if(node.getQuickPath() != null)
            {
                return link.rootContent(node.getQuickPath());
            }
            else
            {
                try
                {
                    final CoralSession coralSession = coralSessionFactory.getCurrentSession();
                    if(structureService.getRootNode(coralSession, node.getSite()).equals(node))
                    {
                        return link.unsetView().unset("x");
                    }
                    else
                    {
                        return link.unsetView().set("x", node.getIdString());
                    }
                }
                catch(IllegalStateException | StructureException e)
                {
                    throw new RuntimeException("cannot determine home page for site"
                        + node.getSite().getName(), e);
                }
            }
        }
        else
        {
            return unsetView().unset("x");
        }
    }

    private LinkTool getHostLink(NavigationNodeResource node)
    {
        try
        {
            if(getHost() == null)
            {
                final CmsData cmsData = cmsDataFactory.getCmsData(context);
                SiteResource curSite = cmsData.getSite();
                if(curSite == null)
                {
                    curSite = cmsData.getGlobalComponentsDataSite();
                }
                if(curSite == null)
                {
                    throw new RuntimeException("No site selected");
                }
                if(curSite.equals(node.getSite()) && !isAbsolute())
                {
                    return this;
                }
                else
                {
                    try
                    {
                        final CoralSession coralSession = coralSessionFactory.getCurrentSession();
                        final String primaryHostName = siteService.getPrimaryMapping(coralSession,
                            node.getSite());
                        return this.host(primaryHostName);
                    }
                    catch(IllegalStateException e)
                    {
                        throw new RuntimeException("cannot access CoralSession", e);
                    }
                    catch(SiteException e)
                    {
                        throw new RuntimeException("cannot determine primary domain name for site"
                            + node.getSite().getName(), e);
                    }
                }
            }
            else
            {
                return this;
            }
        }
        catch(ProcessingException e)
        {
            throw new RuntimeException("cannot access CmsData", e);
        }
    }

    /**
     * Returns a link pointing to RenderComponent view for the specified component instance on the
     * current page.
     * 
     * @param instanceName component instance name.
     * @return
     * @throws ProcessingException
     */
    public LinkTool renderComponent(String instanceName)
        throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        LinkTool link = view("RenderComponent").set("node_id", cmsData.getNode().getId()).set(
            "component_instance", instanceName);
        if(cmsData.isSkinNameOverriden())
        {
            link = link.set(CmsConstants.SKIN_OVERRIDE, cmsData.getSkinName());
        }
        return link;
    }
}
