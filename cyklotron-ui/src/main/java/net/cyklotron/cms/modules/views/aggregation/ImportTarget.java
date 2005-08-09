package net.cyklotron.cms.modules.views.aggregation;

import java.util.ArrayList;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.CoralTableModel;
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
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.ResourceClassResource;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteResourceImpl;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.util.CmsPathFilter;
import net.cyklotron.cms.util.CmsResourceClassFilter;
import net.cyklotron.cms.util.ProtectedViewFilter;

/**
 * Common import target screen.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: ImportTarget.java,v 1.5.6.2 2005-08-09 04:29:52 rafal Exp $
 */
public class ImportTarget
    extends BaseAggregationScreen
{
    /** integration service */
    private IntegrationService integrationService;

    
    public ImportTarget(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        SiteService siteService, AggregationService aggregationService,
        SecurityService securityService, TableStateManager tableStateManager,
        IntegrationService integrationService)
    {
        super(context, logger, preferencesService, cmsDataFactory, siteService, aggregationService,
                        securityService, tableStateManager);
        this.integrationService = integrationService;
    }
    
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        long siteId = parameters.getLong("site_id", -1L);
        long resourceId = parameters.getLong("res_id", -1L);
        try
        {
            SiteResource site = SiteResourceImpl.getSiteResource(coralSession,siteId);
            Resource resource = coralSession.getStore().getResource(resourceId);
            templatingContext.put("resource", resource);
            
            ResourceClassResource resourceClassResource = integrationService.
                getResourceClass(coralSession, resource.getResourceClass());
            templatingContext.put("copy_action", resourceClassResource.getAggregationCopyAction());
            String recursiveAction = resourceClassResource.getAggregationRecursiveCopyAction();
            if(recursiveAction != null)
            {
                templatingContext.put("recursive_enabled", true);
                templatingContext.put("recursive_action", recursiveAction);
            }
            else
            {
                templatingContext.put("recursive_enabled", false);
            }
            
            
            
            String[] classes = resourceClassResource.getAggregationParentClassesList();
            String[] paths = resourceClassResource.getAggregationTargetPathsList();
            TableState state = tableStateManager.getState(context, "cms:screens:aggregation,ImportTarget");
            if(state.isNew())
            {
                state.setTreeView(true);
                state.setShowRoot(true);
                state.setSortColumnName("name");
            }
            String rooId = site.getIdString();
            state.setRootId(rooId);
            state.setExpanded(rooId);

            TableModel model = new CoralTableModel(coralSession, i18nContext.getLocale());
            ArrayList filters = new ArrayList();
            filters.add(new ProtectedViewFilter(coralSession, coralSession.getUserSubject()));
            filters.add(new CmsPathFilter(site, paths));
            TableTool helper = new TableTool(state, filters, model);
            templatingContext.put("table", helper);
            templatingContext.put("resclass_filter",
                new CmsResourceClassFilter(coralSession, integrationService, classes));
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("FilesException ",e);
        }
        catch(TableException e)
        {
            throw new ProcessingException("FilesException ",e);
        }
    }    

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return true;
    }
}
