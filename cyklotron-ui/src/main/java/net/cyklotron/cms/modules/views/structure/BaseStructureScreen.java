package net.cyklotron.cms.modules.views.structure;

import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NaviConstants;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

import org.objectledge.pipeline.ProcessingException;

/**
 * The default void screen assember for forum application.
 */
public abstract class BaseStructureScreen extends BaseCMSScreen implements NaviConstants, Secure
{
    /** logging facility */
    protected Logger log;

    /** structure service */
    protected StructureService structureService;

    /** site service */
    protected SiteService siteService;

    /** style service */
    protected StyleService styleService;
    
    /** related service */
    protected RelatedService relatedService;

    public BaseStructureScreen()
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility("navi");
        structureService = (StructureService)broker.getService(StructureService.SERVICE_NAME);
        styleService = (StyleService)broker.getService(StyleService.SERVICE_NAME);
        siteService = (SiteService)broker.getService(SiteService.SERVICE_NAME);
        relatedService = (RelatedService)broker.getService(RelatedService.SERVICE_NAME);
    }

    public boolean checkModifyPermission(RunData data)
        throws ProcessingException
    {
        return getCmsData().getNode().canModify(coralSession.getUserSubject());
    }
}
