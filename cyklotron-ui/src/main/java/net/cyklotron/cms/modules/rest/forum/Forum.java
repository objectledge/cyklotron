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
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.objectledge.authentication.AuthenticationException;
import org.objectledge.authentication.UserManager;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.query.MalformedQueryException;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.SubtreeVisitor;
import org.objectledge.coral.web.rest.RequireAtLeastOneRole;
import org.objectledge.coral.web.rest.RequireCoralRole;

import net.cyklotron.cms.forum.DiscussionResource;
import net.cyklotron.cms.forum.MessageResource;

@Path("forum")
public class Forum
{

    private static final int LIMIT_OF_POSTS = 20;

    @Inject
    private CoralSessionFactory coralSessionFactory;
    
    @Inject
    private UserManager userManager;

    private Logger logger = Logger.getLogger(getClass());

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RequireAtLeastOneRole({ @RequireCoralRole("cms.administrator"),
                    @RequireCoralRole("cms.registered") })
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
                    final MessageResource messageResource = MessageResource.class.cast(row.get());
                    final DiscussionResource discussion = messageResource.getDiscussion();
                    final Date creationTime = messageResource.getCreationTime();
                    final MessageVisitor messageVisitor = new MessageVisitor(coralSession,
                        anonymous, creationTime);
                    messageVisitor.traverseDepthFirst(discussion);
                    PostDto postDto = new PostDto();
                    final DateTimeFormatter formatter = DateTimeFormat.fullTime();
                    postDto.setCreatedAt(formatter.print(creationTime.getTime()));
                    postDto.setLastReplyAt(formatter.print(messageVisitor.getLastReplyAt()
                        .getTime()));
                    postDto.setTitle(messageResource.getTitle());
                    postDto.setReplies(messageVisitor.getPostCount());
                    postDto.setUrl("SOME URL");
                    posts.add(postDto);
                }
            }
            catch(AuthenticationException e)
            {
                logger.error("Could not find user: '"+ user + "'", e);
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
