package net.cyklotron.cms.modules.views.documents;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.html.HTMLService;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.files.DirectoryResource;
import net.cyklotron.cms.files.DirectoryResourceImpl;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;

/**
 * A screen for configuring calendar screen.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: ProposeDocumentConf.java,v 1.3 2008-10-30 17:46:03 rafal Exp $
 */
public class ProposeDocumentConf
    extends BaseCMSScreen
{
    private final HTMLService htmlService;

    public ProposeDocumentConf(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, HTMLService htmlService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.htmlService = htmlService;
    }

    @Override
    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            Parameters screenConfig = getScreenConfig();
            long root1 = screenConfig.getLong("category_id_1", -1);
            long root2 = screenConfig.getLong("category_id_2", -1);
            if(root1 != -1)
            {
                templatingContext.put("category_1", coralSession.getStore().getResource(root1));
            }
            if(root2 != -1)
            {
                templatingContext.put("category_2", coralSession.getStore().getResource(root2));
            }
            int categoryDepth = screenConfig.getInt("category_depth", 1);
            templatingContext.put("category_depth", categoryDepth);
            templatingContext.put("attachments_enabled", screenConfig.getBoolean(
                "attachments_enabled", false));
            long dirId = screenConfig.getLong("attachments_dir_id", -1);
            templatingContext.put("attachments_dir_id", dirId);
            if(dirId != -1)
            {
                CmsData cmsData = cmsDataFactory.getCmsData(context);
                DirectoryResource dir = DirectoryResourceImpl.getDirectoryResource(coralSession,
                    dirId);
                String dirLocalPath = dir.getPath().substring(cmsData.getSite().getPath().length())
                    .substring(6);
                templatingContext.put("attachments_dir", screenConfig.get("attachments_dir",
                    dirLocalPath));
            }
            int fileCount = screenConfig.getInt("attachments_max_count", -1);
            if(fileCount != -1)
            {
                templatingContext.put("attachments_max_count", fileCount);
            }
            int fileSize = screenConfig.getInt("attachments_max_size", -1);
            int imageSize = screenConfig.getInt("attachemnt_images_max_size", -1);
            if(fileSize != -1)
            {
                templatingContext.put("attachments_max_size", fileSize);
            }
            if(imageSize != -1)
            {
                templatingContext.put("attachemnt_images_max_size", imageSize);
            }
            String formats = screenConfig.get(
                "attachments_allowed_formats", "jpg gif doc rtf pdf xls");
            templatingContext.put("attachments_allowed_formats", formats);
            templatingContext.put("attachments_multi_upload",
                screenConfig.getBoolean("attachments_multi_upload", false));
            templatingContext.put("inherit_categories", screenConfig.getBoolean("inherit_categories", false));
            templatingContext.put("calendar_tree", screenConfig.getBoolean("calendar_tree", false));
            long parentId = screenConfig.getLong("parent_id", -1L);
            if(parentId != -1)
            {
                try
                {
                    String parentPath = NavigationNodeResourceImpl.getNavigationNodeResource(
                        coralSession, parentId).getSitePath();
                    templatingContext.put("parent_id", parentId);
                    templatingContext.put("parent_path", parentPath);
                }
                catch(EntityDoesNotExistException e)
                {
                    // ignore
                }
            }
            
            templatingContext.put("owner_login", screenConfig.get("owner_login", ""));
            templatingContext.put("editing_enabled", screenConfig.getBoolean(
                "editing_enabled", false));
            templatingContext.put("add_document_visual_editor", screenConfig.getBoolean(
                "add_document_visual_editor", false));
            templatingContext.put("clear_org_if_not_match", screenConfig.getBoolean(
                    "clear_org_if_not_match", false));
            templatingContext.put("add_captcha", screenConfig.getBoolean(
                "add_captcha", false));

            templatingContext.put("cleanup_profile", screenConfig.get("cleanup_profile", ""));
            templatingContext.put("available_cleanup_profiles",
                htmlService.availableCleanupProfiles());

            addQueryObject("include", screenConfig, coralSession, templatingContext);
            addQueryObject("exclude", screenConfig, coralSession, templatingContext);
        }
        catch(Exception e)
        {
            throw new ProcessingException("Exception occurred", e);
        }
    }

    private void addQueryObject(String name, Parameters screenConfig, CoralSession coralSession,
        TemplatingContext templatingContext)
    {
        long includeQueryId = screenConfig.getLong(name + "_query_id", -1l);
        if(includeQueryId != -1l)
        {
            try
            {
                Resource includeQuery = coralSession.getStore().getResource(includeQueryId);
                if(includeQuery instanceof CategoryQueryResource)
                {
                    templatingContext.put(name + "_query", includeQuery);
                }
            }
            catch(EntityDoesNotExistException e)
            {
                // welp, query must have been deleted
            }
        }
    }
}
