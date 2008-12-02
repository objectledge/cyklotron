package net.cyklotron.cms.modules.views.security;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.ResourceListTableModel;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.security.CmsSecurityException;
import net.cyklotron.cms.security.RoleResource;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.SiteResource;

public class GroupList
    extends BaseSecurityScreen
{

    public GroupList(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        SecurityService securityService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        securityService);
    }

    @Override
    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18Context,
        CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            CmsData cmsData = cmsDataFactory.getCmsData(context);
            SiteResource site = cmsData.getSite();
            TableModel<Resource> model = new ResourceListTableModel(cmsSecurityService.getGroups(
                coralSession, site), i18Context.getLocale());
            TableState state = tableStateManager.getState(context, "view:security,GroupList");
            if(state.isNew())
            {
                state.setTreeView(false);
                state.setPageSize(0);
            }
            templatingContext.put("table", new TableTool<Resource>(state, null, model));
            templatingContext.put("teamMember", cmsSecurityService.getRole(coralSession, site, site.getTeamMember()));
            templatingContext.put("security", new SecurityServiceHelper(cmsSecurityService));
        }
        catch(Exception e)
        {
            throw new ProcessingException("internal error", e);
        }
    }
    
    public static class SecurityServiceHelper
    {
        private final SecurityService cmsSecurityService;
        
        public SecurityServiceHelper(SecurityService cmsSecurityService)
        {
            this.cmsSecurityService = cmsSecurityService;
        }
        
        public String getGroupName(RoleResource roleResource)
            throws CmsSecurityException
        {
            return cmsSecurityService.getShortGroupName(roleResource);
        }
    }
}
