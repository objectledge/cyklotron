package net.cyklotron.cms.modules.views.security;

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

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.security.RoleResource;
import net.cyklotron.cms.security.RoleResourceImpl;
import net.cyklotron.cms.security.SecurityService;

public class EditGroup
    extends BaseSecurityScreen
{

    public EditGroup(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        SecurityService securityService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        securityService);
    }

    @Override
    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext context,
        CoralSession coralSession)
        throws ProcessingException
    {
        RoleResource group;
        try
        {
            long groupId = parameters.getLong("group_id");
            group = RoleResourceImpl.getRoleResource(coralSession, groupId);
            templatingContext.put("group", group);
            templatingContext.put("name", parameters.get("name", cmsSecurityService
                .getShortGroupName(group)));
            templatingContext.put("description", parameters.get("description", group
                .getDescription()));
            templatingContext.put("sharingWorkgroup", parameters.getBoolean("sharingWorkgroup", group
                .getSharingWorkgroup()));            
        }
        catch(Exception e)
        {
            throw new ProcessingException("invalid parameters", e);
        }
    }
}
