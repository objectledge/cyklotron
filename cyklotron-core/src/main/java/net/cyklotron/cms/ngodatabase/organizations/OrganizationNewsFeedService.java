package net.cyklotron.cms.ngodatabase.organizations;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.comparator.TimeComparator;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.i18n.DateFormatTool;
import org.objectledge.i18n.DateFormatter;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.MergingException;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;

import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndCategoryImpl;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

import net.cyklotron.cms.category.CategoryException;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.documents.LinkRenderer;
import net.cyklotron.cms.ngodatabase.Organization;
import net.cyklotron.cms.structure.table.CustomModificationTimeComparator;
import net.cyklotron.cms.util.OfflineLinkRenderingService;

public class OrganizationNewsFeedService
{
    private static final String FEEDS_DIR = "ngo/database/feeds/";

    private final String newsFeedType;

    private final String newsFeedTitle;

    private final int newsFeedQueryDays;

    private final String newsFeedURL;

    private final String newsFeedIdParam;

    private final Configuration newsFeedSites;

    private final String newsFeedDescription;

    private final long newsFeedCacheTime;

    private final CoralSessionFactory coralSessionFactory;

    private final OrganizationsIndex organizationsIndex;

    private final UpdatedDocumentsProvider updatedDocumentsProvider;

    private final FileSystem fileSystem;

    private final OfflineLinkRenderingService offlineLinkRenderingService;

    private final Templating templating;

    private final CategoryService categoryService;

    private final DateFormat dateFormat;

    private final Locale locale;

    private final DateFormatter dateFormatter;

    private final Logger logger;

    public OrganizationNewsFeedService(Configuration newsFeedConfig, DateFormat dateFormat,
        Locale locale, OrganizationsIndex organizationsIndex,
        UpdatedDocumentsProvider updatedDocumentsProvider, CategoryService categoryService,
        CoralSessionFactory coralSessionFactory, FileSystem fileSystem,
        DateFormatter dateFormatter, OfflineLinkRenderingService offlineLinkRenderingService,
        Templating templating, Logger logger)
        throws ConfigurationException
    {
        this.dateFormat = dateFormat;
        this.locale = locale;
        this.updatedDocumentsProvider = updatedDocumentsProvider;
        this.coralSessionFactory = coralSessionFactory;
        this.organizationsIndex = organizationsIndex;
        this.fileSystem = fileSystem;
        this.dateFormatter = dateFormatter;
        this.offlineLinkRenderingService = offlineLinkRenderingService;
        this.templating = templating;
        this.categoryService = categoryService;
        this.logger = logger;
        this.newsFeedURL = newsFeedConfig.getChild("baseURL").getValue();
        this.newsFeedIdParam = newsFeedConfig.getChild("idParameter").getValue();
        this.newsFeedType = newsFeedConfig.getChild("type").getValue();
        this.newsFeedTitle = newsFeedConfig.getChild("title").getValue();
        this.newsFeedDescription = newsFeedConfig.getChild("description").getValue();
        this.newsFeedQueryDays = newsFeedConfig.getChild("queryDays").getValueAsInteger();
        this.newsFeedCacheTime = newsFeedConfig.getChild("cacheTime").getValueAsLong();
        this.newsFeedSites = newsFeedConfig.getChild("sites");
    }

    public String getOrganizationNewsFeed(Parameters parameters)
        throws ProcessingException
    {
        long organizationId = parameters.getLong(newsFeedIdParam);
        String feedContents;
        try
        {
            feedContents = loadCachedFeed(organizationId);
        }
        catch(IOException e)
        {
            throw new ProcessingException("failed to load cached feed for organization "
                + organizationId, e);
        }
        if(feedContents == null)
        {
            CoralSession coralSession = coralSessionFactory.getAnonymousSession();
            try
            {
                Organization organization = organizationsIndex.getOrganization(organizationId);
                if(organization == null)
                {
                    throw new ProcessingException("organization " + organizationId + " not found");
                }
                Date startDate = new Date();
                Date endDate = updatedDocumentsProvider.offsetDate(startDate, newsFeedQueryDays);
                List<DocumentNodeResource> documents = updatedDocumentsProvider.queryDocuments(
                    updatedDocumentsProvider.getSites(newsFeedSites, coralSession), endDate,
                    organizationId, coralSession);
                Collections.sort(documents, new CustomModificationTimeComparator(
                    TimeComparator.Direction.ASC));
                SyndFeed feed = buildFeed(organization, documents, startDate, endDate, coralSession);
                feedContents = saveCachedFeed(organizationId, feed);
            }
            catch(Exception e)
            {
                throw new ProcessingException("failed to generate feed for organization "
                    + organizationId, e);
            }
            finally
            {
                coralSession.close();
            }
        }
        return feedContents;
    }

    private String cachedFeedPath(String id)
    {
        return FEEDS_DIR + id.substring(0, Math.min(2, id.length())) + "/" + id + ".xml";
    }

    private String loadCachedFeed(long organizationId)
        throws IOException
    {
        String path = cachedFeedPath(Long.toString(organizationId));
        if(fileSystem.exists(path))
        {
            if((System.currentTimeMillis() - fileSystem.lastModified(path)) / 1000 < newsFeedCacheTime)
            {
                return fileSystem.read(path, "UTF-8");
            }
        }
        return null;
    }

    private String saveCachedFeed(long organizationId, SyndFeed feed)
        throws IOException, FeedException
    {
        String path = cachedFeedPath(Long.toString(organizationId));
        if(!fileSystem.exists(FileSystem.directoryPath(path)))
        {
            fileSystem.mkdirs(FileSystem.directoryPath(path));
        }
        SyndFeedOutput feedOutput = new SyndFeedOutput();
        String feedContents = feedOutput.outputString(feed);
        fileSystem.write(path, feedContents, "UTF-8");
        return feedContents;
    }

    private SyndFeed buildFeed(Organization organization, List<DocumentNodeResource> documents,
        Date startDate, Date endDate, CoralSession coralSession)
        throws CategoryException
    {
        List<SyndEntry> entries = new ArrayList<SyndEntry>();
        SyndEntry entry;
        SyndContent description;
        LinkRenderer linkRenderer = offlineLinkRenderingService.getLinkRenderer();

        for(DocumentNodeResource doc : documents)
        {
            entry = new SyndEntryImpl();
            entry.setTitle(doc.getTitle());
            try
            {
                entry.setLink(linkRenderer.getNodeURL(coralSession, doc));
            }
            catch(ProcessingException e)
            {
                throw new RuntimeException("internal error", e);
            }

            if(doc.getValidityStart() == null)
            {
                entry.setPublishedDate(doc.getCreationTime());
            }
            else
            {
                entry.setPublishedDate(doc.getValidityStart());
            }
            String docDescription = "";
            if(doc.getAbstract() != null)
            {
                docDescription = doc.getAbstract();
            }
            description = new SyndContentImpl();
            description.setType("text/plain");
            description.setValue(docDescription);
            entry.setDescription(description);
            entry.setCategories(documentCategories(doc, coralSession));
            entries.add(entry);
        }

        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType(newsFeedType);
        feed.setEncoding("UTF-8");
        feed.setLink(newsFeedURL + organization.getId());
        feed.setTitle(renderFeedDescription(newsFeedTitle, organization, startDate, endDate));
        feed.setDescription(renderFeedDescription(newsFeedDescription, organization, startDate,
            endDate));

        feed.setPublishedDate(endDate);
        feed.setEncoding("UTF-8");
        feed.setEntries(entries);

        return feed;
    }

    private String renderFeedDescription(String template, Organization organization,
        Date startDate, Date endDate)
    {
        TemplatingContext templatingContext = templating.createContext();
        templatingContext.put("organization", organization);
        DateFormatTool dateFormatTool = new DateFormatTool(dateFormatter, locale, dateFormat);
        templatingContext.put("dateFormat", dateFormatTool);
        templatingContext.put("startDate", startDate);
        templatingContext.put("endDate", endDate);
        StringWriter writer = new StringWriter();
        try
        {
            templating.merge(templatingContext, new StringReader(template), writer, "<inline>");
            return writer.toString();
        }
        catch(MergingException e)
        {
            logger.error("error while rendering feed description", e);
            return "";
        }
    }

    private List<SyndCategory> documentCategories(DocumentNodeResource document,
        CoralSession coralSession)
        throws CategoryException
    {
        List<SyndCategory> syndCategories = new ArrayList<SyndCategory>();
        CategoryResource[] categories = categoryService
            .getCategories(coralSession, document, false);
        Resource globalCategoryRoot = categoryService.getCategoryRoot(coralSession, null);
        Resource siteCategoryRoot = categoryService.getCategoryRoot(coralSession, document
            .getSite());
        for(CategoryResource category : categories)
        {
            String categoryURI = null;
            if(coralSession.getStore().isAncestor(globalCategoryRoot, category))
            {
                categoryURI = category.getPath().substring(globalCategoryRoot.getPath().length());
            }
            if(coralSession.getStore().isAncestor(siteCategoryRoot, category))
            {
                categoryURI = "/" + document.getSite().getName()
                    + category.getPath().substring(siteCategoryRoot.getPath().length());
            }
            if(categoryURI != null)
            {
                SyndCategory syndCategory = new SyndCategoryImpl();
                syndCategory.setName(category.getName());
                syndCategory.setTaxonomyUri(categoryURI);
                syndCategories.add(syndCategory);
            }
        }
        return syndCategories;
    }
}
