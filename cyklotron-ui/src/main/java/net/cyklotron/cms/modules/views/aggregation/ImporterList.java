package net.cyklotron.cms.modules.views.aggregation;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.table.ResourceListTableModel;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableException;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.aggregation.AggregationService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

/**
 * 
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: ImporterList.java,v 1.7 2007-11-18 21:24:54 rafal Exp $
 */
public class ImporterList
    extends BaseAggregationScreen
{
    public ImporterList(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        SiteService siteService, AggregationService aggregationService,
        SecurityService securityService, TableStateManager tableStateManager)
    {
        super(context, logger, preferencesService, cmsDataFactory, siteService, aggregationService,
                        securityService, tableStateManager);
        
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            SiteResource[] sites = siteService.getSites(coralSession);
            TableState state = tableStateManager.getState(context, "cms:screens:aggregation:ImporterList");
            if(state.isNew())
            {
                state.setTreeView(false);
                state.setPageSize(10);
            }
            TableModel model = new ResourceListTableModel(sites, i18nContext.getLocale());
            templatingContext.put("table", new TableTool(state, null, model));
            Role importerRole = coralSession.getSecurity().getUniqueRole("cms.aggregation.importer");
            templatingContext.put("importer_role",importerRole);
        }
        catch(TableException e)
        {
            throw new ProcessingException("failed to initialize table toolkit", e);
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Role role = coralSession.getSecurity().getUniqueRole("cms.administrator");
        return coralSession.getUserSubject().hasRole(role);
    }
}
