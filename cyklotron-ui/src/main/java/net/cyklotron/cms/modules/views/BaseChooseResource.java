package net.cyklotron.cms.modules.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.query.MalformedQueryException;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.ParentsVisitor;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.CoralTableModel;
import org.objectledge.coral.table.ResourceTreeTableModel;
import org.objectledge.coral.table.comparator.NameComparator;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableException;
import org.objectledge.table.TableFilter;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StringUtils;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.files.DirectoryResource;
import net.cyklotron.cms.files.FilesException;
import net.cyklotron.cms.files.FilesMapResource;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.files.RootDirectoryResource;
import net.cyklotron.cms.integration.ApplicationResource;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.ResourceClassResource;
import net.cyklotron.cms.integration.ResourceClassResourceImpl;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.util.CmsPathFilter;
import net.cyklotron.cms.util.CmsResourceClassFilter;
import net.cyklotron.cms.util.ProtectedViewFilter;
import net.cyklotron.cms.util.SeeableFilter;

public abstract class BaseChooseResource
    extends BaseCMSScreen
{
    /** integration service */
    protected FilesService filesService;

    protected IntegrationService integrationService;

    protected ResourceClassResource resourceClassResource = null;

    public BaseChooseResource(Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, IntegrationService integrationService,
        FilesService filesService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.integrationService = integrationService;
        this.filesService = filesService;
    }

    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession)
        throws ProcessingException
    {
        initResClassChooser(templatingContext, coralSession, parameters);

        long resId = parameters.getLong("res_id", -1L);

        // lata
        boolean resetState = parameters.getBoolean("reset", false);
        boolean allExpanded = false;
        try
        {
            SiteResource site = getCmsData().getSite();

            Resource resource = null;
            try
            {
                resource = coralSession.getStore().getResource(resId);
                templatingContext.put("resource", resource);
            }
            catch(EntityDoesNotExistException e)
            {
            }

            String[] classes = resourceClassResource.getAggregationParentClassesList();
            String[] paths = resourceClassResource.getAggregationTargetPathsList();

            TableState state = getState(site, resetState, coralSession, resource);

            TableModel<Resource> model = new CoralTableModel<Resource>(coralSession,
                i18nContext.getLocale());
            templatingContext.put("res_class_filter", new CmsResourceClassFilter(coralSession,
                integrationService, new String[] { resourceClassResource.getName() }));

            ArrayList<TableFilter<Resource>> filters = new ArrayList<TableFilter<Resource>>();
            filters.add(new ProtectedViewFilter(coralSession, coralSession.getUserSubject()));
            filters.add(new SeeableFilter());
            String search = parameters.get("search", "");
            templatingContext.put("search", search);
            if(search.length() != 0)
            {
                String query = "FIND RESOURCE FROM " + resourceClassResource.getName()
                    + " WHERE name LIKE_NC '%" + search + "%'";
                if(resourceClassResource.getName().equals("documents.document_node"))
                {
                    query += " OR title LIKE_NC '%" + search + "%'";
                }

                QueryResults results = coralSession.getQuery().executeQuery(query);

                final Set<Resource> resources = new HashSet<>(results.getList(1));
                final Set<Resource> ancestors = new HashSet<>();
                ParentsVisitor visitor = new ParentsVisitor()
                    {
                        // accessed through reflection
                        @SuppressWarnings("unused")
                        public void visit(Resource rr)
                        {
                            ancestors.add(rr);
                        }
                    };
                for(Resource r : resources)
                {
                    visitor.traverseBreadthFirst(r);
                }
                resources.addAll(ancestors);
                model = new ResourceTreeTableModel<Resource>(new ArrayList<Resource>(resources),
                    i18nContext.getLocale());

                allExpanded = true;
            }
            else
            {
                filters.add(new CmsPathFilter(site, paths));
            }
            state.setAllExpanded(allExpanded);
            TableTool<Resource> helper = new TableTool<Resource>(state, filters, model);
            templatingContext.put("table", helper);
        }
        catch(TableException e)
        {
            throw new ProcessingException("Could not prepare table state", e);
        }
        catch(MalformedQueryException e)
        {
            throw new ProcessingException("Internal error", e);
        }
        catch(FilesException e)
        {
            throw new ProcessingException("Files directory configuration error", e);
        }
    }

    private void initResClassChooser(TemplatingContext templatingContext,
        CoralSession coralSession, Parameters parameters)
        throws ProcessingException
    {
        // resource class chooser
        ApplicationResource[] apps = integrationService.getApplications(coralSession);
        NameComparator comparator = new NameComparator(StringUtils.getLocale("en_US"));
        List appList = new ArrayList();
        Map map = new HashMap();
        long defaultResourceClassId = -1;
        for(int i = 0; i < apps.length; i++)
        {
            if(apps[i].getEnabled())
            {
                appList.add(apps[i]);
                ResourceClassResource[] resClasses = integrationService.getResourceClasses(
                    coralSession, apps[i]);
                ArrayList resClassesList = new ArrayList();
                CmsData cmsData = cmsDataFactory.getCmsData(context);
                for(int j = 0; j < resClasses.length; j++)
                {
                    if(isResourceClassSupported(resClasses[j])
                        && integrationService.isApplicationEnabled(coralSession, cmsData.getSite(),
                            (ApplicationResource)resClasses[j].getParent().getParent()))
                    {
                        if(resClasses[j].getName().equals("cms.files.file"))
                        {
                            defaultResourceClassId = resClasses[j].getId();
                        }
                        resClassesList.add(resClasses[j]);
                    }
                }
                Collections.sort(resClassesList, comparator);
                map.put(apps[i], resClassesList);
            }
        }
        Collections.sort(appList, new PriorityComparator());
        templatingContext.put("apps", appList);
        templatingContext.put("apps_map", map);

        long resClassResId = parameters.getLong("res_class_id", defaultResourceClassId);
        templatingContext.put("res_class_id", resClassResId);
        try
        {
            resourceClassResource = ResourceClassResourceImpl.getResourceClassResource(
                coralSession, resClassResId);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException(e);
        }
    }

    protected abstract boolean isResourceClassSupported(ResourceClassResource rClass);

    protected abstract String getStateName();

    protected TableState getState(SiteResource site, boolean resetState, CoralSession coralSession,
        Resource resource)
        throws ProcessingException, FilesException
    {
        String rootId = Long.toString(site.getId());
        TableState state = tableStateManager.getState(context, getStateName());
        if(state.isNew())
        {
            state.setRootId(rootId);
            state.setExpanded(rootId);
            state.setTreeView(true);
            state.setShowRoot(true);
            state.setSortColumnName("name");
        }
        if(resetState)
        {
            state.setRootId(rootId);
            state.clearExpanded();
            state.setExpanded(rootId);
        }
        if((resetState || state.isNew())
            && resourceClassResource.getName().equals("cms.files.file"))
        {
            CmsData cmsData = cmsDataFactory.getCmsData(context);
            FilesMapResource filesMap = filesService.getFilesRoot(coralSession, cmsData.getSite());
            if(filesMap.isExpandedDirectoryDefined())
            {
                Resource dr = (DirectoryResource)filesMap.getExpandedDirectory();
                state.setExpanded(dr.getIdString());
                while(dr.getParent() instanceof DirectoryResource
                    || dr.getParent() instanceof RootDirectoryResource
                    || dr.getParent() instanceof FilesMapResource)
                {
                    dr = dr.getParent();
                    state.setExpanded(dr.getIdString());
                }
            }
        }

        return state;
    }

    public static class PriorityComparator
        implements Comparator
    {
        public int compare(Object o1, Object o2)
        {
            ApplicationResource a1 = (ApplicationResource)o1;
            ApplicationResource a2 = (ApplicationResource)o2;
            return a1.getPriority() - a2.getPriority();
        }
    }

}
