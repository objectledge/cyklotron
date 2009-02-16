package net.cyklotron.cms.modules.views.banner;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.banner.BannerService;
import net.cyklotron.cms.files.FilesException;
import net.cyklotron.cms.files.FilesMapResource;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;


/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: BannerSource.java,v 1.3 2005-01-26 05:23:34 pablo Exp $
 */
public class BannerSource
    extends BaseBannerScreen
{
    /** structure service */
    FilesService filesService;

    public BannerSource(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, BannerService bannerService,
        FilesService filesService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, bannerService);
        this.filesService = filesService;
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        SiteResource site = getSite();
        if(site != null)
        {
            try
            {
                FilesMapResource media = filesService.getFilesRoot(coralSession, site);
                parameters.set("dirs",media.getIdString());
            }
            catch(FilesException e)
            {
                throw new ProcessingException("Cannot create TableTool", e);
            }
        }
    }
}
