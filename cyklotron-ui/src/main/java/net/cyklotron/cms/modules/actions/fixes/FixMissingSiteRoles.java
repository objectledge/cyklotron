package net.cyklotron.cms.modules.actions.fixes;

import org.objectledge.context.Context;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.SubtreeVisitor;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.mvc.security.SecurityChecking;

import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.SchemaRoleResource;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.SiteResource;

public class FixMissingSiteRoles
    implements Valve, SecurityChecking
{
    private final SecurityService cmsSecuritySevice;

    private final IntegrationService integrationService;

    public FixMissingSiteRoles(SecurityService cmsSecuritySevice,
        IntegrationService integrationService)
    {
        this.cmsSecuritySevice = cmsSecuritySevice;
        this.integrationService = integrationService;
    }

    @Override
    public void process(Context context)
        throws ProcessingException
    {
        TemplatingContext templatingContext = context.getAttribute(TemplatingContext.class);
        try
        {
            final CoralSession coralSession = context.getAttribute(CoralSession.class);
            Resource[] sites = coralSession.getStore().getResourceByPath("/cms/sites/*");
            ResourceClass<SiteResource> resourceClass = coralSession.getSchema().getResourceClass(
                "site.site");
            Resource schemaRoleRoot = integrationService.getSchemaRoleRoot(coralSession,
                resourceClass);
            for(Resource siteRes : sites)
            {
                final SiteResource site = (SiteResource)siteRes;
                if(site.getTemplate() == false)
                {
                    new SubtreeVisitor()
                    {
                        @SuppressWarnings("unused")
                        public void visit(SchemaRoleResource schemaRole)
                        throws Exception
                        {
                            cmsSecuritySevice.createRole(coralSession, null, schemaRole.getName(),
                                site);
                        }
                    }.traverseBreadthFirst(schemaRoleRoot);
                }
            }
            templatingContext.put("result", "success");
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
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
