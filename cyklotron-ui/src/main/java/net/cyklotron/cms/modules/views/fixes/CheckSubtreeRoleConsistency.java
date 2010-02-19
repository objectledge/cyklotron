package net.cyklotron.cms.modules.views.fixes;

import org.objectledge.context.Context;
import org.objectledge.coral.modules.views.BaseCoralView;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.security.SecurityChecking;

import net.cyklotron.cms.security.CmsSecurityException;
import net.cyklotron.cms.security.SecurityService;

public class CheckSubtreeRoleConsistency
    extends BaseCoralView
    implements SecurityChecking
{
    final private SecurityService cmsSecurity;

    public CheckSubtreeRoleConsistency(Context context, SecurityService cmsSecurity)
    {
        super(context);       
        this.cmsSecurity = cmsSecurity;        
    }

    @Override
    public void process(Parameters parameters, TemplatingContext templatingContext,
        MVCContext mvcContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {            
            String script = cmsSecurity.subtreeRoleConsistencyUpdate(coralSession, parameters.getBoolean("plan", false));
            if(script.trim().length() == 0)
            {
                templatingContext.put("result", "check_successfult");
            }
            else
            {
                templatingContext.put("result","incosistencies_detected");
                templatingContext.put("script", script);
            }
        }
        catch(CmsSecurityException e)
        {   
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
        }
    }
    
    /**
     * @{inheritDoc}
     */
    public boolean checkAccessRights(Context context)
        throws Exception
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Role cmsAdministrator = coralSession.getSecurity().
            getUniqueRole("cms.administrator");
        return coralSession.getUserSubject().hasRole(cmsAdministrator);
    }

    @Override
    public boolean requiresAuthenticatedUser(Context context)
        throws Exception
    {
        return true;
    }

    @Override
    public boolean requiresSecureChannel(Context context)
        throws Exception
    {
        // TODO Auto-generated method stub
        return false;
    }
}
