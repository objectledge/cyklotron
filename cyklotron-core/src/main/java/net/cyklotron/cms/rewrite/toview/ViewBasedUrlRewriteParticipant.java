package net.cyklotron.cms.rewrite.toview;

import java.util.Collections;
import java.util.List;

import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;

import net.cyklotron.cms.rewrite.ResourceBasedUrlRewriteParticipant;
import net.cyklotron.cms.rewrite.RewriteTarget;
import net.cyklotron.cms.rewrite.SitePath;
import net.cyklotron.cms.site.SiteResource;

public class ViewBasedUrlRewriteParticipant
    extends ResourceBasedUrlRewriteParticipant<RewriteToViewResource>
{
    private static final String PARTICIPANT_NAME = "RewriteToViewResource";

    private static final String PATH_ATTRIBUTE = "prefix";

    public ViewBasedUrlRewriteParticipant(CoralSessionFactory coralSessionFactory)
    {
        super(RewriteToViewResource.CLASS_NAME, RewriteToViewResource.class, coralSessionFactory);
    }

    @Override
    public String getName()
    {
        return PARTICIPANT_NAME;
    }

    @Override
    protected String pathAttribute()
    {
        return PATH_ATTRIBUTE;
    }

    @Override
    protected RewriteTarget getTarget(RewriteToViewResource resource, SitePath sitePath)
    {
        return new RewriteTarget(sitePath, resource.getTargetView(), null,
            Collections.<String, List<String>> emptyMap());
    }

    @Override
    protected String getDescription(RewriteToViewResource resource)
    {
        return resource.getDescription();
    }

    @Override
    protected SiteResource getSite(RewriteToViewResource resource)
    {
        Resource r = resource;
        while(r != null && !(r instanceof SiteResource))
        {
            r = r.getParent();
        }
        return (SiteResource)r;
    }

}
