package net.cyklotron.cms.modules.views.structure;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NaviConstants;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 * The default void screen assember for forum application.
 */
public abstract class BaseStructureScreen 
extends BaseCMSScreen implements NaviConstants
{
    /** structure service */
    protected StructureService structureService;

    /** site service */
    protected SiteService siteService;

    /** style service */
    protected StyleService styleService;
    
    /** related service */
    protected RelatedService relatedService;

    
    
    public BaseStructureScreen(Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, StructureService structureService,
        StyleService styleService, SiteService siteService, RelatedService relatedService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.structureService = structureService;
        this.styleService = styleService;
        this.siteService = siteService;
        this.relatedService = relatedService;
    }

    public boolean checkModifyPermission()
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return getCmsData().getNode().canModify(context, coralSession.getUserSubject());
    }
}
