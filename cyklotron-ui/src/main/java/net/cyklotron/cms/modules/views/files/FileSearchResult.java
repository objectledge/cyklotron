package net.cyklotron.cms.modules.views.files;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.AuthenticationContext;
import org.objectledge.context.Context;
import org.objectledge.coral.modules.views.BaseCoralView;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.Template;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.builders.AbstractBuilder;
import org.objectledge.web.mvc.builders.BuildException;
import org.objectledge.web.mvc.tools.LinkTool;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.CmsTool;
import net.cyklotron.cms.files.DirectoryResource;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.files.FilesTool;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;

/**
 * Search result screen.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: FileSearchResult.java,v 1.8 2006-02-09 13:59:00 pablo Exp $
 */
public class FileSearchResult
    extends AbstractBuilder
{
    FilesService filesService;
    
    public FileSearchResult(Context context, Logger logger, FilesService filesService)
    {
        super(context);
        this.filesService = filesService;
    }

    /**
     * {@inheritDoc}
     */
    public String build(Template template, String embeddedBuildResults) 
        throws BuildException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
        MVCContext mvcContext = MVCContext.getMVCContext(context);
        I18nContext i18nContext = I18nContext.getI18nContext(context);
        AuthenticationContext authenticationContext = 
            AuthenticationContext.getAuthenticationContext(context);
        HttpContext httpContext = HttpContext.getHttpContext(context);
        templatingContext.put("coralSession", coralSession);
        templatingContext.put("mvcContext", mvcContext);
        templatingContext.put("parameters", parameters);
        templatingContext.put("httpContext", httpContext);
        templatingContext.put("authenticationContext", authenticationContext);
        templatingContext.put("i18nContext", i18nContext);
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
                httpContext.sendRedirect(filesTool.getLink(resource));
            }
            if(resource instanceof DirectoryResource)
            {
                SiteResource site = CmsTool.getSite(resource);
                if(site != null)
                {
                    LinkTool link = (LinkTool)templatingContext.get("link");
                    httpContext.sendRedirect(link.unsetView().set("site_id", site.getIdString()).toString());
                }
                else
                {
                    throw new ProcessingException("Directory resource outside the cms branch");
                }
            }
        }
        catch(Exception e)
        {
            throw new BuildException("Exception occured during redirecting...",e);
        }
        return "";
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

