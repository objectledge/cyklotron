package net.cyklotron.cms.modules.actions.fixes;

import org.jcontainer.dna.Logger;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.files.DirectoryResource;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

/**
 * 
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawe� Potempski</a>
 * @version $Id: FixListenerFailure.java,v 1.1 2005-01-24 04:34:29 pablo Exp $
 */
public class FixListenerFailure
    extends BaseCMSAction
{
    /** files service */
    private FilesService filesService;

    /** site service */
    private SiteService siteService;
    
    /** resource service */
    private SecurityService cmsSecurityService;

    protected Logger log;

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();
        try
        {
            filesService = (FilesService)broker.getService(FilesService.SERVICE_NAME);
            siteService = (SiteService)broker.getService(SiteService.SERVICE_NAME);
            cmsSecurityService = (SecurityService)broker.getService(SecurityService.SERVICE_NAME);
            log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility("fixes");
            SiteResource[] sites = siteService.getSites();
            for(int i = 0; i < sites.length; i++)
            {
                fixSite(sites[i], subject, data);
            }
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", StringUtils.stackTrace(e));
        }
    }

    public void fixSite(SiteResource site, Subject subject, RunData data)
        throws Exception
    {
        //FilesMapResource filesRoot = filesService.getFilesRoot(site);
        try
        {
            DirectoryResource publicDirectory = filesService.createRootDirectory(site, "public", true, null, subject);
            publicDirectory.setDescription("Public directory");
            publicDirectory.update(subject);
            DirectoryResource protectedDirectory = filesService.createRootDirectory(site, "protected", false, null, subject);
            protectedDirectory.setDescription("Protected directory");
            protectedDirectory.update(subject);
            log.debug("FIX ACTION COMPLETED FOR SITE "+site.getName());
        }
        catch(Exception e)
        {
            log.error("FIX ACTION ERROR ",e);
        }
    }
}
