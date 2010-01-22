package net.cyklotron.cms.modules.actions.fixes;

import org.objectledge.context.Context;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.mvc.security.SecurityChecking;

import net.cyklotron.cms.security.SubtreeRoleResource;

public class CYKLO622
    implements Valve, SecurityChecking
{

    public void process(Context context)
        throws ProcessingException
    {
        try
        {
            CoralSession coralSession = context.getAttribute(CoralSession.class);
            TemplatingContext templatingContext = context.getAttribute(TemplatingContext.class);
            Resource[] resources = coralSession
                .getQuery()
                .executeQuery(
                    "FIND RESOURCE FROM 'cms.security.subtree_role' WHERE descriptionKey = 'cms.structure.reporter'")
                .getArray(1);
            Permission modifyOwn = coralSession.getSecurity().getUniquePermission(
                "cms.structure.modify_own");
            for(Resource resource : resources)
            {
                SubtreeRoleResource roleResource = (SubtreeRoleResource)resource;
                coralSession.getSecurity().grant(roleResource.getSubtreeRoot(),
                    roleResource.getRole(), modifyOwn, true);
            }
            templatingContext.put("result", "success");
        }
        catch(Exception e)
        {
            throw new ProcessingException(e);
        }
    }

    public boolean checkAccessRights(Context context)
        throws Exception
    {
        CoralSession coralSession = context.getAttribute(CoralSession.class);
        return coralSession.getUserSubject().getId() == Subject.ROOT;
    }

    public boolean requiresAuthenticatedUser(Context context)
        throws Exception
    {
        return true;
    }

    public boolean requiresSecureChannel(Context context)
        throws Exception
    {
        return false;
    }
}
