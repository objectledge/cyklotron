package net.cyklotron.cms.sitemap.documents;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.query.MalformedQueryException;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;

import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.sitemap.RetrievalException;
import net.cyklotron.cms.sitemap.SitemapGenerationParticipant;
import net.cyklotron.cms.sitemap.SitemapImage;
import net.cyklotron.cms.sitemap.SitemapItem;
import net.cyklotron.cms.util.OfflineLinkRenderingService;

public class DocumentsSitemapGenerationParticipant
    implements SitemapGenerationParticipant
{
    private static final String PARTICIPANT_NAME = "documents";

    private final RelatedService relatedService;

    private final OfflineLinkRenderingService linkRenderer;

    private final Logger log;

    public DocumentsSitemapGenerationParticipant(RelatedService relatedService,
        OfflineLinkRenderingService linkRenderer, Logger log)
    {
        this.relatedService = relatedService;
        this.linkRenderer = linkRenderer;
        this.log = log;
    }

    @Override
    public String name()
    {
        return PARTICIPANT_NAME;
    }

    @Override
    public boolean supportsConfiguration()
    {
        return true;
    }

    @Override
    public Iterator<SitemapItem> items(final SiteResource site, final String domain,
        final Parameters parameters, final CoralSession coralSession)
    {
        try
        {
            QueryResults results = coralSession.getQuery().executeQuery(
                "FIND RESOURCE FROM documents.document_node WHERE site = " + site.getIdString());

            final Subject anon = coralSession.getSecurity().getSubject(Subject.ANONYMOUS);

            final Iterator<QueryResults.Row> rowIterator = results.iterator();

            final String uriScheme = site.getRequiresSecureChannel() ? "https" : "http";

            final String pattern = parameters.get(domain, "/x/{id}").replace("{id}", "%d");

            final String uriPattern = uriScheme + "://" + domain + pattern;

            return new Iterator<SitemapItem>()
                {
                    private SitemapItem next;

                    private boolean done = false;

                    private SitemapItem fetchNext()
                    {
                        while(rowIterator.hasNext())
                        {
                            DocumentNodeResource doc = (DocumentNodeResource)rowIterator.next()
                                .get();
                            try
                            {
                                SitemapItem item = item(doc);
                                if(item != null)
                                {
                                    return item;
                                }
                            }
                            catch(RetrievalException e)
                            {
                                log.error("failed to generate site map item for document #"
                                    + doc.getIdString());
                            }
                        }
                        done = true;
                        return null;
                    }

                    private URI docUri(DocumentNodeResource doc)
                        throws RetrievalException
                    {
                        try
                        {
                            return new URI(String.format(uriPattern, doc.getId()));
                        }
                        catch(URISyntaxException e)
                        {
                            throw new RetrievalException("failed to generate URI", e);
                        }
                    }

                    private List<SitemapImage> images(DocumentNodeResource doc)
                        throws RetrievalException
                    {
                        List<Resource> attachments = new ArrayList<>();
                        if(doc.isThumbnailDefined())
                        {
                            attachments.add(doc.getThumbnail());
                        }
                        for(Resource related : relatedService.getRelatedTo(coralSession, doc, null,
                            null))
                        {
                            attachments.add(related);
                        }
                        List<SitemapImage> images = new ArrayList<>();
                        for(Resource attachment : attachments)
                        {
                            if(attachment instanceof FileResource)
                            {
                                FileResource file = (FileResource)attachment;
                                if(file.canView(coralSession, anon)
                                    && file.getMimetype("").startsWith("image/"))
                                {
                                    try
                                    {
                                        URI uri = new URI(linkRenderer.getFileURL(coralSession,
                                            file));
                                        images.add(new SitemapImage(file.getId(), uri, file
                                            .getDescription()));
                                    }
                                    catch(URISyntaxException e)
                                    {
                                        throw new RetrievalException(
                                            "unable to generate image URI", e);
                                    }
                                }
                            }
                        }
                        return images;
                    }

                    private SitemapItem item(DocumentNodeResource doc)
                        throws RetrievalException
                    {
                        if(doc.canView(coralSession, anon))
                        {
                            return new SitemapItem(doc.getId(), docUri(doc),
                                doc.getCustomModificationTime(), null, images(doc));
                        }
                        return null;
                    }

                    @Override
                    public boolean hasNext()
                    {
                        if(next == null && !done)
                        {
                            next = fetchNext();
                        }
                        return next != null;
                    }

                    @Override
                    public SitemapItem next()
                    {
                        if(next == null && !done)
                        {
                            next = fetchNext();
                        }
                        if(next == null)
                        {
                            throw new NoSuchElementException();
                        }
                        else
                        {
                            SitemapItem tmp = next;
                            next = null;
                            return tmp;
                        }
                    }

                    @Override
                    public void remove()
                    {
                        throw new UnsupportedOperationException();
                    }
                };
        }
        catch(MalformedQueryException | EntityDoesNotExistException e)
        {
            throw new RuntimeException("internal error", e);
        }
    }
}
