package net.cyklotron.cms.modules.views.aggregation;

import java.util.ArrayList;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
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
import net.cyklotron.cms.aggregation.AggregationService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.util.ProtectedValidityViewFilter;
import net.cyklotron.cms.util.SiteRejectFilter;

/**
 * This screen shows a list of sites that are visible by anonymous subject, it is used as a starting
 * point for importing data from this sites.
 *
 * @author <a href="mailo:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: ExporterList.java,v 1.6 2007-02-25 14:16:39 pablo Exp $
 */
public class ExporterList extends BaseAggregationScreen
{
    protected StructureService structureService;

    public ExporterList(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        SiteService siteService, AggregationService aggregationService,
        SecurityService securityService, TableStateManager tableStateManager,
        StructureService structureService)
    {
        super(context, logger, preferencesService, cmsDataFactory, siteService, aggregationService,
                        securityService, tableStateManager);
        this.structureService = structureService;
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            CmsData cmsData =  getCmsData();
            SiteResource[] sites = siteService.getSites(coralSession);
            NavigationNodeResource[] homePages = new NavigationNodeResource[sites.length];
            
            for(int i=0; i< sites.length; i++)
            {
                homePages[i] = structureService.getRootNode(coralSession, sites[i]);
            }
            
            TableState state = tableStateManager.getState(context,
                "cms:screens:aggregation:ExporterList");
            if(state.isNew())
            {
                state.setTreeView(false);
                state.setPageSize(10);
            }
            TableModel model = new ResourceListTableModel(homePages, i18nContext.getLocale());
            ArrayList filters = new ArrayList();
            Subject anonymous = coralSession.getSecurity().getSubject(Subject.ANONYMOUS);
            filters.add(new ProtectedValidityViewFilter(coralSession, cmsData, anonymous));
            filters.add(new SiteRejectFilter(new SiteResource[] { cmsData.getSite() } ));
            TableTool helper = new TableTool(state, filters, model);
            templatingContext.put("table", helper);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to prepare model", e);
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        if(!cmsData.isApplicationEnabled("aggregation"))
        {
            logger.debug("Application 'aggregation' not enabled in site");
            return false;
        }
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Role role = coralSession.getSecurity().getUniqueRole("cms.aggregation.importer");
        return coralSession.getUserSubject().hasRole(getCmsData().getSite().getTeamMember()) &&
               coralSession.getUserSubject().hasRole(role);
    }
}
