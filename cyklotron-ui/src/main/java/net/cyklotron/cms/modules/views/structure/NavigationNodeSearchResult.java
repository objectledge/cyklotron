package net.cyklotron.cms.modules.views.structure;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;
/**
 * Navigation node information screen.
 */
public class NavigationNodeSearchResult
    extends BaseStructureScreen
{
    
    /**
     * Builds the screen contents.
     *
     * <p>Redirecto to section</p>
     */
    //TODO
    /**
    public String build(RunData data)
        throws ProcessingException
    {
        LinkTool link = data.getLinkTool();
        long rid = parameters.getLong("res_id", -1);
        if(rid == -1)
        {
            throw new ProcessingException("Resource id not found");
        }
        link = link.set("x",rid).unset("view");
        try
        {
            data.sendRedirect(link.toString());
        }
        catch(Exception e)
        {
            throw new ProcessingException("Exception occured during redirecting...",e);
        }
        return null;
    }
    */
    public NavigationNodeSearchResult(Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, StructureService structureService,
        StyleService styleService, SiteService siteService, RelatedService relatedService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        structureService, styleService, siteService, relatedService);
        // TODO Auto-generated constructor stub
    }
    
    
    /* (non-Javadoc)
     * @see net.cyklotron.cms.modules.views.BaseCMSScreen#process(org.objectledge.parameters.Parameters, org.objectledge.web.mvc.MVCContext, org.objectledge.templating.TemplatingContext, org.objectledge.web.HttpContext, org.objectledge.i18n.I18nContext, org.objectledge.coral.session.CoralSession)
     */
    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession) throws ProcessingException
    {
        // TODO Auto-generated method stub

    }
}
