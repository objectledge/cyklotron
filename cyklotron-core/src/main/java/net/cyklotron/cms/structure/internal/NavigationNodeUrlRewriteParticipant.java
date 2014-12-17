package net.cyklotron.cms.structure.internal;

import java.util.Collections;
import java.util.List;

import org.objectledge.coral.session.CoralSessionFactory;

import net.cyklotron.cms.rewrite.ResourceBasedUrlRewriteParticipant;
import net.cyklotron.cms.rewrite.RewriteTarget;
import net.cyklotron.cms.rewrite.SitePath;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;

public class NavigationNodeUrlRewriteParticipant
    extends ResourceBasedUrlRewriteParticipant<NavigationNodeResource>
{

    private static final String PARTICIPANT_NAME = "NavigationNodeResource";

    public NavigationNodeUrlRewriteParticipant(CoralSessionFactory coralSessionFactory)
    {
        super(NavigationNodeResource.CLASS_NAME, NavigationNodeResource.class, coralSessionFactory);
    }

    @Override
    public String getName()
    {
        return PARTICIPANT_NAME;
    }

    @Override
    protected String pathAttribute()
    {
        return "quickPath";
    }

    @Override
    protected RewriteTarget getTarget(NavigationNodeResource resource, SitePath sitePath)
    {
        return new RewriteTarget(sitePath, null, resource, Collections.<String, List<String>> emptyMap());
    }

    @Override
    protected SiteResource getSite(NavigationNodeResource resource)
    {
        return resource.getSite();
    }

    @Override
    protected String getDescription(NavigationNodeResource resource)
    {
        return resource.getTitle();
    }
}
