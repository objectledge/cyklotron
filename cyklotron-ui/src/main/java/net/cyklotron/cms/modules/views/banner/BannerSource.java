package net.cyklotron.cms.modules.views.banner;

import net.labeo.services.table.TableService;
import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.files.FilesException;
import net.cyklotron.cms.files.FilesMapResource;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.site.SiteResource;


/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: BannerSource.java,v 1.2 2005-01-25 11:24:01 pablo Exp $
 */
public class BannerSource
    extends BaseBannerScreen
{
    /** table service */
    TableService ts;

    /** structure service */
    FilesService filesService;

    public BannerSource()
    {
        ts = (TableService)broker.getService(TableService.SERVICE_NAME);
        filesService = (FilesService)broker.getService(FilesService.SERVICE_NAME);
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        SiteResource site = getSite();
        if(site != null)
        {
            try
            {
                FilesMapResource media = filesService.getFilesRoot(site);
                parameters.set("dirs",media.getIdString());
            }
            catch(FilesException e)
            {
                throw new ProcessingException("Cannot create TableTool", e);
            }
        }
    }
}
