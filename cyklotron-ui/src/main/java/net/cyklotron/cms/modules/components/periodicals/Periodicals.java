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
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.periodicals.PeriodicalResource;
import net.cyklotron.cms.periodicals.PeriodicalsException;
import net.cyklotron.cms.periodicals.PeriodicalsService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinService;


/**
 * @author fil
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Periodicals 
    extends BasePeriodicalsComponent
{
    public Periodicals(Context context, Logger logger, Templating templating,
        CmsDataFactory cmsDataFactory, SkinService skinService, MVCFinder mvcFinder,
        PeriodicalsService periodicalsService, FilesService cmsFilesService,
        TableStateManager tableStateManager)
    {
        super(context, logger, templating, cmsDataFactory, skinService, mvcFinder,
                        periodicalsService, cmsFilesService, tableStateManager);
        // TODO Auto-generated constructor stub
    }
    protected PeriodicalResource[] getPeriodicals(CoralSession coralSession, SiteResource site)
        throws ProcessingException
    {
        try
        {
            return periodicalsService.getPeriodicals(coralSession, site);
        }
        catch(PeriodicalsException e)
        {
            throw new ProcessingException("failed to retrieve periodicals", e);
        }
    }

    protected String getSessionKey()
    {
        return "cms:periodicals,Periodicals";
    }    

	protected Resource getPeriodicalRoot(CoralSession coralSession, SiteResource site)
	throws PeriodicalsException
	{
		return periodicalsService.getPeriodicalsRoot(coralSession, site);
	}
}
