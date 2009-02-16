package net.cyklotron.cms.modules.views.files;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.tools.LinkTool;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.CmsTool;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;

/**
 * Search result screen.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: GotoFiles.java,v 1.2 2005-09-04 13:26:55 rafal Exp $
 */
public class GotoFiles
    extends BaseFilesScreen
{
    
    public GotoFiles(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        FilesService filesService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, filesService);
        
    }
    
    /**
     * 
     */
    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession) throws org.objectledge.pipeline.ProcessingException
    {
        try
        {
            long rid = parameters.getLong("res_id", -1);
            if(rid == -1)
            {
                throw new ProcessingException("Resource id not found");
            }
            try
            {
                Resource resource = coralSession.getStore().getResource(rid);
                SiteResource site = CmsTool.getSite(resource);
                if(resource instanceof FileResource)
                {
                    resource = resource.getParent();    
                }
                LinkTool link = (LinkTool)templatingContext.get("link");
                link = link.unset("x").view("files.ListDirectory")
                    .set("dir_id",resource.getId()).set("site_id",site.getId());    
                httpContext.sendRedirect(link.toString());
            }
            catch(Exception e)
            {
                throw new ProcessingException("Exception occured during redirecting...",e);
            }
        }
        catch(Exception e)
        {
            throw new ProcessingException("Exception occured during redirecting...",e);
        }
    }

    public boolean checkAccessRights(Context context)
    {
        return true;
    }
	
    /**
     * @{inheritDoc}
     */
    public boolean requiresAuthenticatedUser(Context context)
        throws Exception
    {
        return false;
    } 
}

