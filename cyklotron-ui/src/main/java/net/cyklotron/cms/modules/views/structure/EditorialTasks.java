package net.cyklotron.cms.modules.views.structure;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.comparators.ReverseComparator;
import org.jcontainer.dna.Logger;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.coral.datatypes.AttributeHandlerBase;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.relation.Relation;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.comparator.CreationTimeComparator;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.modules.actions.structure.workflow.MoveToWaitingRoom;
import net.cyklotron.cms.modules.views.documents.DocumentStateTool;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;

/**
 *
 */
public class EditorialTasks
    extends BaseStructureScreen
{   
    protected UserManager userManager;
    
    private CategoryService categoryService;
    
      
    public EditorialTasks(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, StructureService structureService,
        StyleService styleService, SiteService siteService, RelatedService relatedService,
        UserManager userManager, CategoryService categoryService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        structureService, styleService, siteService, relatedService);
        this.userManager = userManager;
        this.categoryService = categoryService;
    }
    
    @Override
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        int offset = parameters.getInt("offset", httpContext.getSessionAttribute(
            "cms.structure.EditorialTasks.filter.offset", 21));
        long ownerId = parameters.getLong("owner_id", -2l); // -2 not defined parameter
        String ownerLogin = parameters.get("owner_login", httpContext.getSessionAttribute(
            "cms.structure.EditorialTasks.filter.owner_login", ""));
        if(ownerId == -1l) // -1 explicitly requested by 'created by all' click
        {
            ownerLogin = "";
        }

        templatingContext.put("offset", offset);
        httpContext.setSessionAttribute("cms.structure.EditorialTasks.filter.offset", offset);

        SiteResource site = getSite();
        LongSet classifiedNodes = new LongOpenHashSet();
        boolean showUnclassified = structureService.isShowUnclassifiedNodes();
        if(showUnclassified)
        {
            showUnclassified = structureService.showUnclassifiedInSite(site);
        }
        templatingContext.put("showUnclassified", showUnclassified);
        if(showUnclassified)
        {
            Relation refs = categoryService.getResourcesRelation(coralSession);
            Resource negativeCategory = structureService.getNegativeCategory();
            Resource positiveCategory = structureService.getPositiveCategory();
            classifiedNodes.addAll(refs.get(negativeCategory.getId()));
            classifiedNodes.addAll(refs.get(positiveCategory.getId()));
            templatingContext.put("positiveCategory", positiveCategory);
            templatingContext.put("negativeCategory", negativeCategory);
        }
        try
        {
            if(ownerLogin.length()> 0)
            {
                String dn = userManager.getUserByLogin(ownerLogin).getName();
                Subject owner = coralSession.getSecurity().getSubject(dn);
                ownerId = owner.getId();
            }
            if(ownerId > 0) // explicitly requested by 'created by me' click
            {
                Subject owner = coralSession.getSecurity().getSubject(ownerId);
                ownerLogin = userManager.getLogin(owner.getName());
            }
            templatingContext.put("owner_id",new Long(ownerId));
            templatingContext.put("owner_login", ownerLogin);
            httpContext.setSessionAttribute("cms.structure.EditorialTasks.filter.owner_login", ownerLogin);        

            Subject subject = coralSession.getUserSubject();
            Permission redactorPermission = coralSession.getSecurity().getUniquePermission("cms.structure.modify_own");
            Permission editorPermission = coralSession.getSecurity().getUniquePermission("cms.structure.modify");
            
            String query = "FIND RESOURCE FROM structure.navigation_node WHERE site = "
                + site.getIdString();
            String proposedChangesQuery = "FIND RESOURCE FROM documents.document_node WHERE site = "
                + site.getIdString();

            if(ownerId >= 0)
            {
                query = query + " AND owner = " + ownerId;
                proposedChangesQuery = proposedChangesQuery + " AND owner = " + ownerId;
                templatingContext.put("owner", coralSession.getSecurity().getSubject(ownerId));
            }
            
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -offset);
            SimpleDateFormat df = new SimpleDateFormat(AttributeHandlerBase.DATE_TIME_FORMAT);
            query = query + " AND creation_time > '" + df.format(calendar.getTime()) + "'";

            Resource expiredState = coralSession.getStore().getUniqueResourceByPath(
                "/cms/workflow/automata/structure.navigation_node/states/expired");
            proposedChangesQuery = proposedChangesQuery + " AND state != "
                + expiredState.getIdString() + " AND DEFINED proposedContent";
            
            QueryResults results = coralSession.getQuery().
                executeQuery(query);
           
            QueryResults proposedChangesResults = coralSession.getQuery().executeQuery(
                proposedChangesQuery);

            List<NavigationNodeResource> assignedNodes = new ArrayList<>();
            List<NavigationNodeResource> takenNodes = new ArrayList<>();
            List<NavigationNodeResource> lockedNodes = new ArrayList<>();
            List<NavigationNodeResource> rejectedNodes = new ArrayList<>();
            List<NavigationNodeResource> newNodes = new ArrayList<>();
            List<NavigationNodeResource> preparedNodes = new ArrayList<>();
            List<NavigationNodeResource> expiredNodes = new ArrayList<>();
            List<NavigationNodeResource> unclassifiedNodes = new ArrayList<>();
            List<NavigationNodeResource> proposedNodes = new ArrayList<>();
            List<NavigationNodeResource> unpublishedProposedNodes = new ArrayList<>();
            
            Resource homePage = getHomePage();
            Resource[] parents = coralSession.getStore().
                getResource(homePage,MoveToWaitingRoom.WAITING_ROOM_NAME);
            Resource waitingRoom = parents.length == 1 ? parents[0] : null;
            
            for(QueryResults.Row row : results)
            {
                NavigationNodeResource node = (NavigationNodeResource)row.get();
                // hide documents in waiting room
                if(waitingRoom != null)
                {
                	if(coralSession.getStore().isAncestor(waitingRoom, node))
                	{
                		continue;
                	}
                }
                if(node.getState() == null)
                {
                    continue;
                }
                // documents with proposed changes are handled in elsewhere
                if(node instanceof DocumentNodeResource
                    && ((DocumentNodeResource)node).isProposedContentDefined())
                {
                    continue;
                }
                String state = node.getState().getName();
                if(subject.hasPermission(node, redactorPermission))
                {
                    if(subject.equals(node.getOwner()) || subject.hasPermission(node, editorPermission))
                    {
                        if(state.equals("assigned"))
                        {
                            assignedNodes.add(node);
                            continue;
                        }
						if(state.equals("prepared"))
						{
                            preparedNodes.add(node);
                            continue;
						}
                        if(state.equals("taken"))
                        {
                            takenNodes.add(node);
                            continue;
                        }
                        if(state.equals("locked"))
                        {
                            lockedNodes.add(node);
                            continue;
                        }
                        if(state.equals("rejected"))
                        {
                            rejectedNodes.add(node);
                            continue;
                        }
                    }
                }
                if(subject.hasPermission(node, editorPermission))
                {
                    if(state.equals("new"))
                    {
                        newNodes.add(node);
                        continue;
                    }
                    if(state.equals("expired"))
                    {
                        expiredNodes.add(node);
                        continue;
                    }
                    if(showUnclassified && state.equals("published") && 
                      (!classifiedNodes.contains(node.getId())))
                    {
                        unclassifiedNodes.add(node);
                        continue;
                    }
                }
            }
            
            for(QueryResults.Row row : proposedChangesResults)
            {
                NavigationNodeResource node = (NavigationNodeResource)row.get();
                if(subject.hasPermission(node, redactorPermission))
                {
                    if(subject.equals(node.getOwner()) || subject.hasPermission(node, editorPermission))
                    {
                        if(node.getState() == null)
                        {
                            continue;
                        }
                        String state = node.getState().getName();
                        if(state.equals("published"))
                        {
                            proposedNodes.add(node);
                        }
                        else
                        {
                            unpublishedProposedNodes.add(node);
                        }
                    }
                }    
            }
            
            @SuppressWarnings("unchecked")
            Comparator<NavigationNodeResource> pc = new ReverseComparator(
                new CreationTimeComparator<NavigationNodeResource>());
            Collections.sort(assignedNodes, pc);
            Collections.sort(takenNodes, pc);
            Collections.sort(lockedNodes, pc);
            Collections.sort(rejectedNodes, pc);
            Collections.sort(newNodes, pc);
            Collections.sort(preparedNodes, pc);
            Collections.sort(expiredNodes, pc);
            Collections.sort(proposedNodes,pc);
            Collections.sort(unpublishedProposedNodes, pc);
            if(structureService.isShowUnclassifiedNodes())
            {
                Collections.sort(unclassifiedNodes, pc);
                Collections.reverse(unclassifiedNodes);
            }
            templatingContext.put("assigned_nodes", assignedNodes);
            templatingContext.put("taken_nodes", takenNodes);
            templatingContext.put("locked_nodes", lockedNodes);
            templatingContext.put("rejected_nodes", rejectedNodes);
            templatingContext.put("new_nodes", newNodes);
            templatingContext.put("prepared_nodes", preparedNodes);
            templatingContext.put("expired_nodes", expiredNodes);
            templatingContext.put("proposed_nodes", proposedNodes);
            templatingContext.put("unpublished_proposed_nodes", unpublishedProposedNodes);
            templatingContext.put("unclassified_nodes", unclassifiedNodes);
            
            templatingContext.put("documentState", new DocumentStateTool(coralSession,logger));
            
            templatingContext.put("related", relatedService.getRelation(coralSession));
        }
        catch(Exception e)
        {
            throw new ProcessingException("Exception occured", e);
        }
    }
    
    @Override
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = context.getAttribute(CoralSession.class);
        return coralSession.getUserSubject().hasRole(getSite().getTeamMember());
    }
}
