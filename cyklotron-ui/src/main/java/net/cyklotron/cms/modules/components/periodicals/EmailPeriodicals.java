/*
 * Created on Nov 6, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.modules.components.periodicals;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.periodicals.EmailPeriodicalsRootResource;
import net.cyklotron.cms.periodicals.PeriodicalResource;
import net.cyklotron.cms.periodicals.PeriodicalsException;
import net.cyklotron.cms.periodicals.PeriodicalsService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.StructureService;

/**
 * @author fil
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EmailPeriodicals 
    extends BasePeriodicalsComponent
{
    protected StructureService structureService;
    
    public EmailPeriodicals(org.objectledge.context.Context context, Logger logger,
        Templating templating, CmsDataFactory cmsDataFactory, SkinService skinService,
        MVCFinder mvcFinder, PeriodicalsService periodicalsService, FilesService cmsFilesService,
        TableStateManager tableStateManager, StructureService structureService)
    {
        super(context, logger, templating, cmsDataFactory, skinService, mvcFinder,
                        periodicalsService, cmsFilesService, tableStateManager);
        this.structureService = structureService;
    }
    
    protected PeriodicalResource[] getPeriodicals(CoralSession coralSession, SiteResource site)
        throws ProcessingException
    {
        try
        {
            return periodicalsService.getEmailPeriodicals(coralSession, site);
        }
        catch(PeriodicalsException e)
        {
            throw new ProcessingException("failed to retrieve periodicals", e);
        }
    }
    
    protected String getSessionKey()
    {
        return "cms:periodicals,EmailPeriodicals";
    }
    
    public void prepareDefault(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
        super.prepareDefault(context);
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        try
        {
			EmailPeriodicalsRootResource root =
				periodicalsService.getEmailPeriodicalsRoot(coralSession, cmsData.getSite());
			templatingContext.put("subscriptionNode", root.getSubscriptionNode());
        }
        catch (PeriodicalsException e)
        {
        	throw new ProcessingException("cannot get email periodicals root", e);
        }
    }

    protected Resource getPeriodicalRoot(CoralSession coralSession, SiteResource site)
	throws PeriodicalsException
    {
        return periodicalsService.getEmailPeriodicalsRoot(coralSession, site);
    }
}
