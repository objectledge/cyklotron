package net.cyklotron.cms.modules.views.structure;

import java.util.Arrays;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;
import net.cyklotron.cms.util.IndexTitleComparator;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.datatypes.ResourceList;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.query.MalformedQueryException;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

/**
 * Navigation node information screen.
 */
public class NaviInfo
    extends BaseNodeListScreen
{
    
    private final IntegrationService integrationService;

    public NaviInfo(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, StructureService structureService,
        StyleService styleService, SiteService siteService, RelatedService relatedService, IntegrationService integrationService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        structureService, styleService, siteService, relatedService);
        this.integrationService = integrationService;
        
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        NavigationNodeResource homePage = getHomePage();
        NavigationNodeResource currentNode;
        if(parameters.get("action","").equals("structure,AddNode"))
        {
            try
            {
                currentNode = (NavigationNodeResource)coralSession.getStore().
                      getResource(parameters.getLong("node_id"));
            }
            catch(Exception e)
            {
                throw new ProcessingException("invalid node id", e);
            }
        }
        else
        {
            currentNode = getNode();
        }

        prepareTableState(context, homePage, currentNode);

       // TODO: Check if we need to have it?
        Long clipId = (Long)httpContext.getSessionAttribute(CLIPBOARD_KEY);
        if(clipId != null)
        {
            try
            {
                NavigationNodeResource clipboardNode = NavigationNodeResourceImpl
                    .getNavigationNodeResource(coralSession, clipId.longValue());
                templatingContext.put("clipboard", "true");
                templatingContext.put("clipboard_node", clipboardNode);
                templatingContext.put("clipboard_mode", httpContext.getSessionAttribute(CLIPBOARD_MODE));
            }
            catch (EntityDoesNotExistException e)
            {
                String msg = "Navigation node with id="+clipId.longValue()
                    +" stored in clipboard cannot be retrieved";
                logger.error(msg, e);
                throw new ProcessingException(msg, e);
            }
        }
        else
        {
            templatingContext.put("clipboard","false");
        }
        ResourceList<Resource> sequence = null;
        if(currentNode instanceof DocumentNodeResource)
        {
            sequence = ((DocumentNodeResource)currentNode).getRelatedResourcesSequence();
            try
            {
                String query = "FIND RESOURCE FROM documents.document_alias WHERE originalDocument = '"
                    + currentNode.getPath() + "'";
                QueryResults results = coralSession.getQuery().executeQuery(query);
                templatingContext.put("aliases",results.getList(1));
            }
            catch(MalformedQueryException e)
            {
                throw new ProcessingException("Query exception", e);
            }
        }
        Resource[] relatedTo = relatedService.getRelatedTo(coralSession, currentNode, sequence,
            new IndexTitleComparator(context, integrationService, i18nContext.getLocale()));
        templatingContext.put("related_to", Arrays.asList(relatedTo));
    }

    protected String getStateName()
        throws ProcessingException
    {
        SiteResource site = getSite();
        return "cms:screens:structure,NaviInfo:"+site.getIdString();
    }
    
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        SiteResource site = getSite();
        return coralSession.getUserSubject().hasRole(site.getTeamMember()) || checkAdministrator(coralSession);
    }
}
