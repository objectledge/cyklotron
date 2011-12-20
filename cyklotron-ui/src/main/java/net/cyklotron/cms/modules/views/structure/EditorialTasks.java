package net.cyklotron.cms.modules.views.structure;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.coral.datatypes.DateAttributeHandler;
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
            
            String query;
            if(ownerId < 0)
            {
                query = "FIND RESOURCE FROM structure.navigation_node WHERE site = "+site.getIdString();
            }
            else
            {
                query = "FIND RESOURCE FROM structure.navigation_node WHERE site = "+site.getIdString()+
                        " AND owner = "+ownerId;
                templatingContext.put("owner",coralSession.getSecurity().getSubject(ownerId));                        
            }
            String publishedNodesQuery = query;
            
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -offset);
            SimpleDateFormat df = new SimpleDateFormat(DateAttributeHandler.DATE_TIME_FORMAT);
            query = query + " AND creation_time > '" + df.format(calendar.getTime()) + "'";

            Resource publishedState = coralSession.getStore().getUniqueResourceByPath(
                "/cms/workflow/automata/structure.navigation_node/states/published");
            publishedNodesQuery = publishedNodesQuery + " AND state = "+publishedState.getIdString(); 
            
            QueryResults results = coralSession.getQuery().
                executeQuery(query);
            Resource[] nodes = results.getArray(1);
           
            QueryResults publishedNodeResults = coralSession.getQuery().executeQuery(
                publishedNodesQuery);
            Resource[] publishedNodes = publishedNodeResults.getArray(1);

            List assignedNodes = new ArrayList();
            List takenNodes = new ArrayList();
            List lockedNodes = new ArrayList();
            List rejectedNodes = new ArrayList();
            List newNodes = new ArrayList();
            List preparedNodes = new ArrayList();
            List expiredNodes = new ArrayList();
            List unclassifiedNodes = new ArrayList();
            List proposedNodes = new ArrayList();
            List unpublishedProposedNodes = new ArrayList();
            
            Resource homePage = getHomePage();
            Resource[] parents = coralSession.getStore().
                getResource(homePage,MoveToWaitingRoom.WAITING_ROOM_NAME);
            Resource waitingRoom = parents.length == 1 ? parents[0] : null;
            
            for(int i = 0; i < nodes.length; i++)
            {
                NavigationNodeResource node = (NavigationNodeResource)nodes[i];
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
                String state = node.getState().getName();
                if(subject.hasPermission(node, redactorPermission))
                {
                    if(subject.equals(node.getOwner()) || subject.hasPermission(node, editorPermission))
                    {
                        if(state.equals("assigned"))
                        {
                            if(((DocumentNodeResource)node).isProposedContentDefined())
                            {
                                unpublishedProposedNodes.add(node);
                            }
                            else
                            {
                                assignedNodes.add(node);
                            }
                            continue;
                        }
						if(state.equals("prepared"))
						{
                            if(((DocumentNodeResource)node).isProposedContentDefined())
                            {
                                unpublishedProposedNodes.add(node);
                            }
                            else
                            {
                                preparedNodes.add(node);
                            }
                            continue;
						}
                        if(state.equals("taken"))
                        {
                            if(((DocumentNodeResource)node).isProposedContentDefined())
                            {
                                unpublishedProposedNodes.add(node);
                            }
                            else
                            {
                                takenNodes.add(node);
                            }
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
                        if(((DocumentNodeResource)node).isProposedContentDefined())
                        {
                            unpublishedProposedNodes.add(node);
                        }
                        else
                        {
                            newNodes.add(node);
                        }
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
            
            for(int i = 0; i < publishedNodes.length; i++)
            {
                NavigationNodeResource node = (NavigationNodeResource)publishedNodes[i];
                if(subject.hasPermission(node, redactorPermission))
                {
                    if(subject.equals(node.getOwner()) || subject.hasPermission(node, editorPermission))
                    {
                        if(((DocumentNodeResource)node).isProposedContentDefined())
                        {
                              proposedNodes.add(node);
                        }
                    }
                }    
            }
            
            CreationTimeComparator pc = new CreationTimeComparator();
            Collections.sort(assignedNodes, pc);
            Collections.reverse(assignedNodes);
            Collections.sort(takenNodes, pc);
            Collections.reverse(takenNodes);
            Collections.sort(lockedNodes, pc);
            Collections.reverse(lockedNodes);
            Collections.sort(rejectedNodes, pc);
            Collections.reverse(rejectedNodes);
            Collections.sort(newNodes, pc);
            Collections.reverse(newNodes);
            Collections.sort(preparedNodes, pc);
            Collections.reverse(preparedNodes);
            Collections.sort(expiredNodes, pc);
            Collections.reverse(expiredNodes);
            Collections.sort(proposedNodes,pc);
            Collections.reverse(proposedNodes);
            Collections.sort(unpublishedProposedNodes, pc);
            Collections.reverse(unpublishedProposedNodes);
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
