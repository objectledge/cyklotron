package net.cyklotron.cms.modules.views.files;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.preferences.PreferencesService;

/**
 * Search result screen.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: FileSearchResult.java,v 1.5 2005-03-08 11:02:38 pablo Exp $
 */
public class FileSearchResult
    extends BaseFilesScreen
{
    
    public FileSearchResult(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        FilesService filesService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, filesService);
        
    }
    
    
    /* (non-Javadoc)
     * @see net.cyklotron.cms.modules.views.BaseCMSScreen#process(org.objectledge.parameters.Parameters, org.objectledge.web.mvc.MVCContext, org.objectledge.templating.TemplatingContext, org.objectledge.web.HttpContext, org.objectledge.i18n.I18nContext, org.objectledge.coral.session.CoralSession)
     */
    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession) throws org.objectledge.pipeline.ProcessingException
    {
    }
    
    /**
     * Builds the screen contents.
     *
     * <p>Redirecto to file application or download</p>
     */
    /**
     * TODO
    public String build(RunData data)
        throws ProcessingException
    {
        try
        {
            long rid = parameters.getLong("res_id", -1);
            if(rid == -1)
            {
                throw new ProcessingException("Resource id not found");
            }
            Resource resource = coralSession.getStore().getResource(rid);
            if(!(resource instanceof FileResource) && !(resource instanceof DirectoryResource))
            {
                throw new ProcessingException("Class of the resource '"+resource.getResourceClass().getName()+
                                              "' is does not belong to files application");
            }
            if(resource instanceof FileResource)
            {
                FilesTool filesTool = (FilesTool)templatingContext.get("files");
                data.sendRedirect(filesTool.getLink(resource));
            }
            if(resource instanceof DirectoryResource)
            {
                SiteResource site = CmsTool.getSite(resource);
                if(site != null)
                {
                    LinkTool link = data.getLinkTool();
                    data.sendRedirect(link.unset("view").set("site_id", site.getIdString()).toString());
                }
                else
                {
                    throw new ProcessingException("Directory resource outside the cms branch");
                }
            }
        }
        catch(Exception e)
        {
            throw new ProcessingException("Exception occured during redirecting...",e);
        }
        return null;
    }
    */
    public boolean checkAccessRights(Context context)
    {
        return true;
    }
}

