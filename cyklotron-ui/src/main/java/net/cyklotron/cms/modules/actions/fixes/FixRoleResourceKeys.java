package net.cyklotron.cms.modules.actions.fixes;

import java.util.HashMap;

import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.security.RoleResource;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

/**
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: FixRoleResourceKeys.java,v 1.1 2005-01-24 04:34:29 pablo Exp $
 */
public class FixRoleResourceKeys extends BaseCMSAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Subject subject = coralSession.getUserSubject();
        
        // to create
        // cms.category.administrator
        
        // to remove
        // cms.rich_content.administrator
        
        // to rename
        HashMap names = new HashMap();
        names.put("team_member", "cms.site.team_member");
        names.put("site.administrator", "cms.site.administrator");
        names.put("rich_content.administrator", "cms.rich_content.administrator");
        names.put("layout.administrator", "cms.layout.administrator");
        names.put("structure.administrator", "cms.structure.administrator");
        names.put("structure.moderator", "cms.structure.moderator");
        names.put("structure.editor", "cms.structure.editor");
        names.put("structure.visitor", "cms.structure.visitor");
        names.put("files.administrator", "cms.media.administrator");
        names.put("polls.administrator", "cms.forum.administrator");
        names.put("forum.administrator", "cms.forum.administrator");
        names.put("forum.discussion.administrator", "cms.forum.discussion.administrator");
        names.put("forum.discussion.moderator", "cms.forum.discussion.moderator");
        names.put("forum.discussion.participant", "cms.forum.discussion.participant");
        names.put("cms.polls.administrator", "cms.poll.polls.administrator");
        names.put("cms.banners.administrator", "cms.banner.banners.administrator");
        
        SiteService siteService = (SiteService)(broker.getService(SiteService.SERVICE_NAME));
        SecurityService cmsSecurityService = (SecurityService)(broker.getService(SecurityService.SERVICE_NAME));
        SiteResource[] sites = siteService.getSites();
        for(int i=0; i<sites.length; i++)
        {
            Resource root = cmsSecurityService.getRoleInformationRoot(sites[i]);
            Resource[] roles = coralSession.getStore().getResource(root);
            for(int j=0; j<roles.length; j++)
            {
                changeKeys(names, (RoleResource)(roles[j]), subject);
            }
        }
    }
    
    private void changeKeys(HashMap names, RoleResource role, Subject subject)
    {
        System.out.println("Role key: "+role.getDescriptionKey()+" "+names.get(role.getDescriptionKey())+"\n");
        if(names.containsKey(role.getDescriptionKey()))
        {
            role.setDescriptionKey((String)(names.get(role.getDescriptionKey())));
            role.update(subject);
        }

        Resource[] roles = coralSession.getStore().getResource(role);
        for(int j=0; j<roles.length; j++)
        {
            changeKeys(names, (RoleResource)(roles[j]), subject);
        }
    }
}
