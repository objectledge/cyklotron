package net.cyklotron.cms.modules.components.related;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.datatypes.ResourceList;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableRow;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.documents.DocumentAliasResource;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.components.SkinableCMSComponent;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.util.CmsResourceClassFilter;
import net.cyklotron.cms.util.CmsResourceListTableModel;
import net.cyklotron.cms.util.IndexTitleComparator;
import net.cyklotron.cms.util.ProtectedViewFilter;

/**
 * Related component.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: Related.java,v 1.7 2008-10-07 16:48:41 rafal Exp $
 */

public class Related
    extends SkinableCMSComponent
{
    private RelatedService relatedService;

    private TableStateManager tableStateManager;
    
    private IntegrationService integrationService;

    
    public Related(org.objectledge.context.Context context, Logger logger, Templating templating,
        CmsDataFactory cmsDataFactory, SkinService skinService, MVCFinder mvcFinder,
        RelatedService relatedService, TableStateManager tableStateManager,
        IntegrationService integrationService)
    {
        super(context, logger, templating, cmsDataFactory, skinService, mvcFinder);
        this.relatedService = relatedService;
        this.tableStateManager = tableStateManager;
        this.integrationService = integrationService;
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession) throws ProcessingException
    {
        if(getNode() == null)
        {
            componentError(context, "No node selected");
            return;
        }
        NavigationNodeResource currentNode = getNode();
        try
        {
            Parameters componentConfig = getConfiguration();
            String resPath = componentConfig.get("related_path", "");
            DocumentNodeResource resource = null;

            if(currentNode instanceof DocumentAliasResource)
            {
                resource = (DocumentNodeResource)((DocumentAliasResource)currentNode)
                    .getOriginalDocument();
            }
            else
            {
                resource = (DocumentNodeResource)currentNode;
            }
            if(resPath.length() != 0)
            {
                DocumentNodeResource[] resources = (DocumentNodeResource[])coralSession.getStore()
                    .getResourceByPath(resPath);
                if(resources.length == 1)
                {
                    resource = resources[0];
                }
            }
            
            Resource[] related = relatedService.getRelatedTo(coralSession, resource, resource.getRelatedResourcesSequence(), 
                new IndexTitleComparator(context, integrationService, i18nContext.getLocale()));
            
            TableState state = tableStateManager.getState(context, "cms:components:related,Related");
            String[] resourceClassResourceNames = componentConfig.getStrings("related_classes");
            if(state.isNew())
            {
                state.setTreeView(false);
                state.setPageSize(0);
                state.setSortColumnName("unsorted"); // RelatedService.getRelated() takes care of the order
                state.setAscSort(true);
            }
            TableModel model = new CmsResourceListTableModel(context, integrationService, related, i18nContext.getLocale());
            ArrayList filters = new ArrayList();            
            filters.add(new ProtectedViewFilter(coralSession, coralSession.getUserSubject(), cmsDataFactory.getCmsData(context)));
            filters.add(new CmsResourceClassFilter(coralSession, integrationService, resourceClassResourceNames));
            TableTool helper = new TableTool(state, filters, model);
            
            templatingContext.put("table", helper);
            templatingContext.put("header", componentConfig.get("header",""));
        }
        catch(Exception e)
        {
            componentError(context, "Related Exception", e);
        }
    }
}
