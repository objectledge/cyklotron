package net.cyklotron.cms.modules.rest.forum;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.objectledge.authentication.AuthenticationException;
import org.objectledge.authentication.UserManager;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.query.MalformedQueryException;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.SubtreeVisitor;
import org.objectledge.coral.web.rest.RequireAny;
import org.objectledge.coral.web.rest.RequireCoralRole;
import org.objectledge.encodings.HTMLEntityDecoder;

import net.cyklotron.cms.forum.CommentaryResource;
import net.cyklotron.cms.forum.DiscussionResource;
import net.cyklotron.cms.forum.MessageResource;
import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * Returns Posts for given user
 * 
 * @author Marek Lewandowski
 */
@Path("forum")
public class Forum
{
    private static final int LIMIT_OF_POSTS = 20;

    @Inject
    private CoralSessionFactory coralSessionFactory;

    @Inject
    private UserManager userManager;

    @Inject
    private SiteService siteService;

    private Logger logger = Logger.getLogger(getClass());

    private DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis();

    private final HTMLEntityDecoder decoder = new HTMLEntityDecoder();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RequireAny(roles = { @RequireCoralRole("cms.registered"),
                    @RequireCoralRole("cms.administrator") })
    public Collection<PostDto> getUserPosts(@QueryParam("user") String user,
        @QueryParam("limit") @DefaultValue("20") int requestedLimit,
        @QueryParam("offset") @DefaultValue("0") int offset)
    {
        final int limit = hardLimit(requestedLimit);
        final Collection<PostDto> posts = new ArrayList<>();
        try(CoralSession coralSession = coralSessionFactory.getRootSession())
        {
            try
            {
                final Principal principal = userManager.getUserByLogin(user);
                final QueryResults results = coralSession.getQuery().executeQuery(
                    buildQuery(principal, limit, offset));
                final Subject anonymous = findAnonymousSubject(coralSession);

                for(QueryResults.Row row : results.getList())
                {
                    try
                    {
                        final MessageResource messageResource = MessageResource.class.cast(row
                            .get());
                        final DiscussionResource discussion = messageResource.getDiscussion();
                        final Date creationTime = messageResource.getCreationTime();
                        final MessageVisitor messageVisitor = new MessageVisitor(coralSession,
                            anonymous, creationTime);
                        messageVisitor.traverseDepthFirst(discussion);

                        PostDto postDto = new PostDto();
                        postDto.setCreatedAt(dateTimeFormatter.print(creationTime.getTime()));
                        postDto.setLastReplyAt(dateTimeFormatter.print(messageVisitor
                            .getLastReplyAt().getTime()));
                        postDto.setTitle(decoder.decode(messageResource.getTitle()));
                        postDto.setReplies(messageVisitor.getPostCount());
                        postDto.setUrl(buildUrl(coralSession, messageResource, discussion));
                        posts.add(postDto);
                    }
                    catch(SiteException e)
                    {
                        logger.error("Could not produce post", e);
                    }
                }
            }
            catch(AuthenticationException e)
            {
                logger.error("Could not find user: '" + user + "'", e);
            }
            catch(MalformedQueryException e)
            {
                throw new RuntimeException("Unexpected error - malformed query", e);
            }
            catch(EntityDoesNotExistException e)
            {
                throw new RuntimeException("Unexpected error - could not find anonymous", e);
            }
        }
        return posts;
    }

    protected String buildUrl(CoralSession coralSession, final MessageResource messageResource,
        final DiscussionResource discussion)
        throws EntityDoesNotExistException, SiteException
    {
        final NavigationNodeResource navigationNode = getNavigationNode(discussion, coralSession);
        final SiteResource site = navigationNode.getSite();

        final StringBuilder urlBuilder = new StringBuilder("//");
        urlBuilder.append(siteService.getPrimaryMapping(coralSession, site));
        urlBuilder.append("/x/");
        urlBuilder.append(navigationNode.getIdString());
        urlBuilder.append("?mid=");
        urlBuilder.append(messageResource.getIdString());
        urlBuilder.append("&state=message");
        return urlBuilder.toString();
    }

    private NavigationNodeResource getNavigationNode(DiscussionResource discussion,
        CoralSession coralSession)
    {
        final NavigationNodeResource forumNode = discussion.getForum().getForumNode();
        if(discussion instanceof CommentaryResource)
        {
            final CommentaryResource commentary = CommentaryResource.class.cast(discussion);
            try
            {
                final Resource resource = coralSession.getStore().getResource(
                    commentary.getResourceId());
                if(resource instanceof NavigationNodeResource)
                {
                    return NavigationNodeResource.class.cast(resource);
                }
            }
            catch(EntityDoesNotExistException e)
            {

            }
        }
        return forumNode;
    }

    private Subject findAnonymousSubject(CoralSession coralSession)
        throws EntityDoesNotExistException, AuthenticationException
    {
        return coralSession.getSecurity().getSubject(
            userManager.getUserByLogin("anonymous").getName());
    }

    private String buildQuery(Principal principal, int limit, int offset)
    {
        return "FIND RESOURCE FROM " + MessageResource.CLASS_NAME + " WHERE created_by='"
            + principal.getName() + "' LIMIT " + String.valueOf(limit) + " OFFSET "
            + String.valueOf(offset);
    }

    private int hardLimit(int requestedLimit)
    {
        return requestedLimit < LIMIT_OF_POSTS ? requestedLimit : LIMIT_OF_POSTS;
    }

    private final static class MessageVisitor
        extends SubtreeVisitor
    {
        private final CoralSession rootSession;

        private final Subject anonymous;

        private Date lastReplyAt;

        public MessageVisitor(CoralSession rootSession, Subject anonymous, Date lastReplyAt)
        {
            this.rootSession = rootSession;
            this.anonymous = anonymous;
            this.lastReplyAt = lastReplyAt;
        }

        private int postCount = 0;

        public Date getLastReplyAt()
        {
            return lastReplyAt;
        }

        public int getPostCount()
        {
            return postCount;
        }

        @SuppressWarnings("unused")
        public void visit(MessageResource messageResource)
        {
            if(messageResource.canView(rootSession, anonymous))
            {
                postCount = postCount + 1;
                final Date modificationTime = messageResource.getModificationTime();
                if(modificationTime.after(lastReplyAt))
                {
                    lastReplyAt = modificationTime;
                }
            }
        }
    }

}
