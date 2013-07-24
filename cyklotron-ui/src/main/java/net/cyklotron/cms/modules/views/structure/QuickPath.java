package net.cyklotron.cms.modules.views.structure;

import org.objectledge.context.Context;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.web.mvc.builders.AbstractBuilder;
import org.objectledge.web.mvc.security.SecurityChecking;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.site.SiteResource;

public class QuickPath
    extends AbstractBuilder
    implements SecurityChecking
{
    private final CmsDataFactory cmsDataFactory;

    private final CoralSessionFactory coralSessionFactory;

    public QuickPath(Context context, CmsDataFactory cmsDataFactory,
        CoralSessionFactory coralSessionFactory)
    {
        super(context);
        this.cmsDataFactory = cmsDataFactory;
        this.coralSessionFactory = coralSessionFactory;
    }

    @Override
    public boolean requiresSecureChannel(Context context)
        throws Exception
    {
        return false;
    }

    @Override
    public boolean requiresAuthenticatedUser(Context context)
        throws Exception
    {
        return true;
    }

    @Override
    public boolean checkAccessRights(Context context)
        throws Exception
    {
        CoralSession coralSession = coralSessionFactory.getCurrentSession();
        Role cmsAdministrator = coralSession.getSecurity().getUniqueRole("cms.administrator");
        final Subject currentSubject = coralSession.getUserSubject();
        if(currentSubject.hasRole(cmsAdministrator))
        {
            return true;
        }
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        final SiteResource site = cmsData.getSite();
        if(site != null)
        {
            return currentSubject.hasRole(site.getAdministrator());
        }
        return false;
    }

}
