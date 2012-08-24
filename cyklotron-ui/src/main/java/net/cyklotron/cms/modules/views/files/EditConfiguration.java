package net.cyklotron.cms.modules.views.files;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.files.FilesException;
import net.cyklotron.cms.files.FilesMapResource;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;

public class EditConfiguration
    extends BaseCMSScreen
{
    private final FilesService filesService;

    public EditConfiguration(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager, FilesService filesService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.filesService = filesService;
    }

    @Override
    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            CmsData cmsData = cmsDataFactory.getCmsData(context);
            FilesMapResource filesMap = filesService.getFilesRoot(coralSession, cmsData.getSite());
            templatingContext.put("expanded_directory", filesMap.getExpandedDirectory());
        }
        catch(FilesException e)
        {
            throw new ProcessingException(e);
        }
    }

    @Override
    public boolean checkAccessRights(Context context)
        throws Exception
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        CoralSession coralSession = context.getAttribute(CoralSession.class);
        return coralSession.getUserSubject().hasRole(cmsData.getSite().getAdministrator());
    }
}
