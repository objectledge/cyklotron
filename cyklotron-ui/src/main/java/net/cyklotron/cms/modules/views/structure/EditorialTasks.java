package net.cyklotron.cms.modules.views.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.generic.CrossReference;
import net.labeo.services.resource.query.QueryResults;
import net.labeo.services.resource.table.CreationTimeComparator;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.actions.structure.workflow.MoveToWaitingRoom;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.related.RelationshipsResource;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 *
 */
public class EditorialTasks
    extends BaseStructureScreen
{   
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        
        try
        {
            long ownerId = parameters.getLong("owner_id", -1);
            String ownerLogin = parameters.get("owner_login","");
            if(ownerLogin.length()> 0)
            {
                String dn = authenticationService.getUserByLogin(ownerLogin).getName();
                Subject owner = coralSession.getSecurity().getSubject(dn);
                ownerId = owner.getId();
            }
            templatingContext.put("owner_id",new Long(ownerId));
            Subject subject = coralSession.getUserSubject();
            Permission redactorPermission = coralSession.getSecurity().getUniquePermission("cms.structure.modify_own");
            Permission editorPermission = coralSession.getSecurity().getUniquePermission("cms.structure.modify");
            SiteResource site = getSite();
            
                       
            
            String query = null;
            if(ownerId == -1)
            {
                query = "FIND RESOURCE FROM structure.navigation_node WHERE site = "+site.getIdString();
            }
            else
            {
                query = "FIND RESOURCE FROM structure.navigation_node WHERE site = "+site.getIdString()+
                        " AND owner = "+ownerId;
                templatingContext.put("owner",coralSession.getSecurity().getSubject(ownerId));                        
            }
            
            // hack!!!
            Resource homePage = getHomePage();
            Resource[] parents = coralSession.getStore().
                getResource(homePage,MoveToWaitingRoom.WAITING_ROOM_NAME);
            if(parents.length > 0)
            {
                query = query + " AND parent != "+parents[0].getIdString();
            }
            // end of hack!!!                        
            
            QueryResults results = coralSession.getQuery().
                executeQuery(query);
                
            
            Resource[] nodes = results.getArray(1);
            List assignedNodes = new ArrayList();
            List takenNodes = new ArrayList();
            List lockedNodes = new ArrayList();
            List rejectedNodes = new ArrayList();
            List newNodes = new ArrayList();
            List preparedNodes = new ArrayList();
            List expiredNodes = new ArrayList();
            
            RelationshipsResource relatedResource = relatedService.getRelationshipsResource(site);
            if(relatedResource != null)
            {
                CrossReference related = relatedResource.getXref();
                templatingContext.put("related", related);
            }
            
            for(int i = 0; i < nodes.length; i++)
            {
                NavigationNodeResource node = (NavigationNodeResource)nodes[i];
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
            templatingContext.put("assigned_nodes", assignedNodes);
            templatingContext.put("taken_nodes", takenNodes);
            templatingContext.put("locked_nodes", lockedNodes);
            templatingContext.put("rejected_nodes", rejectedNodes);
            templatingContext.put("new_nodes", newNodes);
            templatingContext.put("prepared_nodes", preparedNodes);
            templatingContext.put("expired_nodes", expiredNodes);
        }
        catch(Exception e)
        {
            throw new ProcessingException("Exception occured", e);
        }
    }
    
    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        return coralSession.getUserSubject().hasRole(getSite().getTeamMember());
    }
}
