package net.cyklotron.cms.modules.views.link;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.link.LinkException;
import net.cyklotron.cms.link.LinkRootResource;
import net.cyklotron.cms.link.LinkService;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.StructureService;

/**
 * link application base screen
 * @version $Id: BaseLinkScreen.java,v 1.4 2005-02-10 17:50:15 rafal Exp $
 */
public abstract class BaseLinkScreen extends BaseCMSScreen
{
    /** link service */
    protected LinkService linkService;

    /** structure service */
    protected StructureService structureService;

    public BaseLinkScreen(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        LinkService linkService, StructureService structureService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.linkService = linkService;
        this.structureService = structureService;
    }

    public LinkRootResource getLinkRoot(CoralSession coralSession)
        throws ProcessingException
    {
        CmsData cmsData = getCmsData();
        SiteResource site = cmsData.getSite();
        if(site == null)
        {
            site = cmsData.getGlobalComponentsDataSite();
        }
        if(site == null)
        {
            throw new ProcessingException("No site selected");
        }
        try
        {
            return linkService.getLinkRoot(coralSession, site);
        }
        catch(LinkException e)
        {
            throw new ProcessingException("failed to lookup links root");
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        try
        {
            return coralSession.getUserSubject().hasRole(getLinkRoot(coralSession).getAdministrator());
        }
        catch(ProcessingException e)
        {
            logger.error("Subject has no rights to view this screen");
            return false;
        }
    }
}
