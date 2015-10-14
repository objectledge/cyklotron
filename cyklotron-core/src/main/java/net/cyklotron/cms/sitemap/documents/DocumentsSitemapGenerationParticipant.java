package net.cyklotron.cms.sitemap.documents;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.query.MalformedQueryException;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;

import net.cyklotron.cms.canonical.CanonicalLinksService;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.sitemap.RetrievalException;
import net.cyklotron.cms.sitemap.SitemapGenerationParticipant;
import net.cyklotron.cms.sitemap.SitemapImage;
import net.cyklotron.cms.sitemap.SitemapItem;
import net.cyklotron.cms.sitemap.SitemapResourceIterator;
import net.cyklotron.cms.util.OfflineLinkRenderingService;

import bak.pcj.set.LongSet;

public class DocumentsSitemapGenerationParticipant
    implements SitemapGenerationParticipant
{
    private static final String PARTICIPANT_NAME = "documents";

    private final RelatedService relatedService;

    private final OfflineLinkRenderingService linkRenderer;

    private final Logger log;

    private CanonicalLinksService canonicalLinksService;

    public DocumentsSitemapGenerationParticipant(RelatedService relatedService,
        CanonicalLinksService canonicalLinksService, OfflineLinkRenderingService linkRenderer,
        Logger log)
    {
        this.relatedService = relatedService;
        this.canonicalLinksService = canonicalLinksService;
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

            final Iterator<QueryResults.Row> rmlQueryResultsIterator = results.iterator();

            final LongSet categoryQueryResults = canonicalLinksService.getCanonicalNodes(site,
                coralSession);

            final Iterator<QueryResults.Row> resultsIterator = new DocumentSetIterator(
                rmlQueryResultsIterator, categoryQueryResults, coralSession);            

            final Subject anon = coralSession.getSecurity().getSubject(Subject.ANONYMOUS);

            final String uriPattern = parameters.get(site.getName(), "/x/{id}").replace("{id}",
                "%d");

            return new SitemapResourceIterator<DocumentNodeResource>(resultsIterator,
                            DocumentNodeResource.class, log)
                {
                    protected SitemapItem item(DocumentNodeResource doc)
                        throws RetrievalException
                    {
                        if(doc.canView(coralSession, anon))
                        {
                            return new SitemapItem(doc.getId(), docUri(doc),
                                doc.getCustomModificationTime(), null, images(doc));
                        }
                        return null;
                    }

                    private URI docUri(DocumentNodeResource doc)
                        throws RetrievalException
                    {
                        try
                        {
                            if(doc.isQuickPathDefined())
                            {
                                return new URI(linkRenderer.getAbsoluteURL(coralSession, site,
                                    doc.getQuickPath()));
                            }
                            else
                            {
                                return new URI(linkRenderer.getApplicationURL(coralSession, site)
                                    + String.format(uriPattern, doc.getId()));
                            }
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
                };
        }
        catch(MalformedQueryException | EntityDoesNotExistException e)
        {
            throw new RuntimeException("internal error", e);
        }
    }
}
