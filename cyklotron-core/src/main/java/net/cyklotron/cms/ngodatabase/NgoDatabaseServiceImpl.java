// 
// Copyright (c) 2004, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
// 
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
// 
// * Redistributions of source code must retain the above copyright notice,  
//       this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//       this list of conditions and the following disclaimer in the documentation  
//       and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//       nor the names of its contributors may be used to endorse or promote products  
//       derived from this software without specific prior written permission. 
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
// IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,  
// INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  
// BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
// OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  
// WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  
// POSSIBILITY OF SUCH DAMAGE. 
// 

package net.cyklotron.cms.ngodatabase;

import static net.cyklotron.cms.documents.DocumentMetadataHelper.attr;
import static net.cyklotron.cms.documents.DocumentMetadataHelper.cdata;
import static net.cyklotron.cms.documents.DocumentMetadataHelper.doc;
import static net.cyklotron.cms.documents.DocumentMetadataHelper.elm;
import static net.cyklotron.cms.documents.DocumentMetadataHelper.enc;
import static net.cyklotron.cms.documents.DocumentMetadataHelper.text;
import static org.objectledge.filesystem.FileSystem.directoryPath;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.query.MalformedQueryException;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.schema.AttributeHandler;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.UnsupportedCharactersInFilePathException;
import org.objectledge.i18n.DateFormatTool;
import org.objectledge.i18n.DateFormatter;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.MergingException;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StringUtils;
import org.picocontainer.Startable;

import net.cyklotron.cms.category.CategoryException;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.documents.LinkRenderer;
import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.util.OfflineLinkRenderingService;
import net.cyklotron.cms.util.ProtectedValidityFilter;

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

public class NgoDatabaseServiceImpl
    implements NgoDatabaseService, Startable
{
    private static final String INCOMING_FILE = "ngo/database/incoming/update.xml";

    private static final String OUTGOING_FILE = "ngo/database/outgoing/update.xml";

    private static final String FEEDS_DIR = "ngo/database/feeds/";

    private static final OutputFormat OUTGOING_FORMAT = new OutputFormat("  ", true, "UTF-8");

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm";

    private static final String DEFAULT_LOCALE = "pl_PL";

    private final Logger logger;

    private final String sourceURL;

    private final FileSystem fileSystem;

    private final CoralSessionFactory coralSessionFactory;

    private final OrganizationsIndex organizations;

    private final int outgoingQueryDays;

    private final Configuration outgoingSites;

    private final DateFormat dateFormat;

    private final String newsFeedType;

    private final String newsFeedTitle;

    private final int newsFeedQueryDays;

    private final String newsFeedURL;

    private final String newsFeedIdParam;

    private final Configuration newsFeedSites;

    private final String newsFeedDescription;

    private final long newsFeedCacheTime;

    private final Templating templating;

    private final OfflineLinkRenderingService offlineLinkRenderingService;

    private final DateFormatter dateFormatter;

    private final Locale locale;

    private final CategoryService categoryService;

    private final SiteService siteService;

    public NgoDatabaseServiceImpl(Configuration config, Logger logger, FileSystem fileSystem,
        SiteService siteService, CoralSessionFactory coralSessionFactory, Templating templating,
        OfflineLinkRenderingService offlineLinkRenderingService, DateFormatter dateFormatter,
        CategoryService categoryService)
        throws Exception
    {
        this.logger = logger;
        this.fileSystem = fileSystem;
        this.siteService = siteService;
        this.coralSessionFactory = coralSessionFactory;
        this.templating = templating;
        this.offlineLinkRenderingService = offlineLinkRenderingService;
        this.dateFormatter = dateFormatter;
        this.categoryService = categoryService;
        this.organizations = new OrganizationsIndex(fileSystem, logger);
        CoralSession coralSession = coralSessionFactory.getAnonymousSession();
        try
        {
            // date format
            Configuration dateFormatConfig = config.getChild("dateFormat");
            this.dateFormat = new SimpleDateFormat(dateFormatConfig.getChild("pattern").getValue(
                DEFAULT_DATE_FORMAT));
            this.locale = StringUtils.getLocale(dateFormatConfig.getChild("locale").getValue(
                DEFAULT_LOCALE));
            // incoming
            Configuration incomingConfig = config.getChild("incoming");
            this.sourceURL = incomingConfig.getChild("sourceURL").getValue();
            // outgoing
            Configuration outgoingConfig = config.getChild("outgoing");
            this.outgoingQueryDays = outgoingConfig.getChild("queryDays").getValueAsInteger();
            this.outgoingSites = outgoingConfig.getChild("sites");                 
            // news feed
            Configuration newsFeedConfig = config.getChild("newsFeed");
            this.newsFeedURL = newsFeedConfig.getChild("baseURL").getValue();
            this.newsFeedIdParam = newsFeedConfig.getChild("idParameter").getValue();
            this.newsFeedType = newsFeedConfig.getChild("type").getValue();
            this.newsFeedTitle = newsFeedConfig.getChild("title").getValue();
            this.newsFeedDescription = newsFeedConfig.getChild("description").getValue();
            this.newsFeedQueryDays = newsFeedConfig.getChild("queryDays").getValueAsInteger();
            this.newsFeedCacheTime = newsFeedConfig.getChild("cacheTime").getValueAsLong();
            this.newsFeedSites = newsFeedConfig.getChild("sites");
        }
        finally
        {
            coralSession.close();
        }
    }

    private void downloadIncoming()
        throws IOException
    {
        HttpClient client = new HttpClient();
        HttpMethod method = new GetMethod(sourceURL);
        client.executeMethod(method);
        String incomingTempFile = INCOMING_FILE + ".tmp";
        try
        {
            if(!fileSystem.isDirectory(directoryPath(INCOMING_FILE)))
            {
                fileSystem.mkdirs(directoryPath(INCOMING_FILE));
            }
            fileSystem.write(incomingTempFile, method.getResponseBodyAsStream());
            method.releaseConnection();

            fileSystem.rename(incomingTempFile, INCOMING_FILE);
        }
        catch(UnsupportedCharactersInFilePathException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateIncoming()
    {
        readIncoming(true);
    }

    private void readIncoming(boolean updateFromSource)
    {
        try
        {
            boolean sourceUpdated = false;
            if(updateFromSource || !fileSystem.isFile(INCOMING_FILE))
            {
                downloadIncoming();
                sourceUpdated = true;
            }
            if(sourceUpdated || organizations.isEmpty())
            {
                organizations.startUpdate();
                SAXReader saxReader = new SAXReader();
                Document doc = saxReader.read(fileSystem.getInputStream(INCOMING_FILE));
                for(Element ogranization : (List<Element>)doc
                    .selectNodes("/organizacje/organizacjaInfo"))
                {
                    String name = ogranization.selectSingleNode("Nazwa_polska").getStringValue();
                    Long id = Long.parseLong(ogranization.selectSingleNode("ID_Adresowego")
                        .getStringValue());
                    String city = ogranization.selectSingleNode("Miasto").getStringValue();
                    String province = ogranization.selectSingleNode("Wojewodztwo").getStringValue();
                    String street = ogranization.selectSingleNode("Ulica").getStringValue();
                    String post_code = ogranization.selectSingleNode("Kod_pocztowy")
                        .getStringValue();
                    this.organizations.addItem(new Organization(id, name, province, city, street,
                        post_code));
                }
                organizations.endUpdate();
            }
        }
        catch(DocumentException e)
        {
            logger.info("Could not read source file ", e);
        }
        catch(IOException e)
        {
            logger.info("Could not read source file ", e);
        }
    }

    @Override
    public void start()
    {
        readIncoming(false);
    }

    @Override
    public void stop()
    {

    }

    // incoming organization data

    @Override
    public Organization getOrganization(long id)
    {
        return organizations.getOrganization(id);
    }

    @Override
    public List<Organization> getOrganizations(String substring)
    {
        return organizations.getOrganizations(substring);
    }

    // outgoing organization data

    @Override
    public void updateOutgoing()
    {
        // query documents
        List<DocumentNodeResource> documents = null;
        Date endDate = offsetDate(new Date(), outgoingQueryDays);
        CoralSession coralSession = coralSessionFactory.getAnonymousSession();
        try
        {
            documents = queryDocuments(getSites(outgoingSites, siteService,
                coralSession), endDate, -1L, coralSession);
        }
        catch(Exception e)
        {
            logger.error("failed to retrieve documents", e);
            return;
        }
        finally
        {
            coralSession.close();
        }

        // group documents by organization id
        Map<Long, List<DocumentNodeResource>> orgMap = new HashMap<Long, List<DocumentNodeResource>>();
        for(DocumentNodeResource doc : documents)
        {
            List<Long> orgIds = getOrganizationIds(doc);
            for(Long orgId : orgIds)
            {
                List<DocumentNodeResource> docList = orgMap.get(orgId);
                if(docList == null)
                {
                    docList = new ArrayList<DocumentNodeResource>();
                    orgMap.put(orgId, docList);
                }
                docList.add(doc);
            }
        }

        // build DOM tree
        List<Long> organizationIdList = new ArrayList<Long>(orgMap.keySet());
        Collections.sort(organizationIdList);
        DateFormat dateFormat = (DateFormat)this.dateFormat.clone();
        Element update = attr(elm("update"), "time", dateFormat.format(new Date()));
        for(Long organizationId : organizationIdList)
        {
            Element orgElm = organizationElm(organizationId);
            List<DocumentNodeResource> docList = orgMap.get(organizationId);
            Collections.sort(docList, DocumentComparator.INSTANCE);
            for(DocumentNodeResource doc : docList)
            {
                try
                {
                    orgElm.add(documentElm(doc, organizationId, dateFormat));
                }
                catch(DocumentException e)
                {
                    logger.error("invalid metadata in document #" + doc.getIdString(), e);
                    orgElm.add(attr(elm("invalidDocument"), "id", doc.getIdString()));
                }
            }
            update.add(orgElm);
        }
        Document doc = doc(update);

        // serialize DOM to XML
        try
        {
            if(!fileSystem.isDirectory(directoryPath(OUTGOING_FILE)))
            {
                fileSystem.mkdirs(directoryPath(OUTGOING_FILE));
            }
            OutputStream outputStream = fileSystem.getOutputStream(OUTGOING_FILE);
            XMLWriter xmlWriter = new XMLWriter(outputStream, OUTGOING_FORMAT);
            xmlWriter.write(doc);
            outputStream.close();
        }
        catch(IOException e)
        {
            logger.error("failed to write outgoing data", e);
        }
    }

    private static SiteResource[] getSites(Configuration config, SiteService siteService,
        CoralSession coralSession)
        throws SiteException, ConfigurationException
    {
        Configuration[] siteConfigElm = config.getChildren("site");
        SiteResource[] sites = new SiteResource[siteConfigElm.length];
        for(int i = 0; i < siteConfigElm.length; i++)
        {
            sites[i] = siteService.getSite(coralSession, siteConfigElm[i].getValue());
        }
        return sites;
    }

    @SuppressWarnings("unchecked")
    private static String getDateLiteral(Date date, CoralSession coralSession)
    {
        try
        {
            AttributeHandler<Date> handler = (AttributeHandler<Date>)coralSession.getSchema()
                .getAttributeClass("date").getHandler();
            return handler.toExternalString(date);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new RuntimeException("internal error", e);
        }
    }

    private static Date offsetDate(Date date, int offsetDays)
    {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, -offsetDays);
        return cal.getTime();
    }

    private List<DocumentNodeResource> queryDocuments(SiteResource[] sites, Date endDate,
        long organizationId, CoralSession coralSession)
    {
        try
        {
            StringBuilder query = new StringBuilder();
            query.append("FIND RESOURCE FROM documents.document_node ");
            query.append("WHERE (");
            for(int i = 0; i < sites.length; i++)
            {
                query.append("site = ").append(sites[i].getId());
                if(i < sites.length - 1)
                {
                    query.append(" OR ");
                }
            }
            query.append(") ");
            if(organizationId != -1L)
            {
                query.append("AND organizationIds LIKE '%," + organizationId + ",%' ");
            }
            else
            {
                query.append("AND organizationIds != '' ");
            }
            query.append("AND customModificationTime > ");
            query.append(getDateLiteral(endDate, coralSession));
            QueryResults results = coralSession.getQuery().executeQuery(query.toString());
            List<DocumentNodeResource> documents = new ArrayList<DocumentNodeResource>();

            // trim down the results to publicly visible documents
            Subject anonymousSubject = coralSession.getSecurity().getSubject(Subject.ANONYMOUS);
            ProtectedValidityFilter filter = new ProtectedValidityFilter(coralSession,
                anonymousSubject, new Date());
            for(QueryResults.Row row : results)
            {
                if(filter.accept(row.get()))
                {
                    documents.add((DocumentNodeResource)row.get());
                }
            }
            return documents;
        }
        catch(MalformedQueryException e)
        {
            throw new RuntimeException("internal error", e);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new RuntimeException("internal error", e);
        }
    }

    private List<Long> getOrganizationIds(DocumentNodeResource document)
    {
        List<Long> organizationIds = new ArrayList<Long>();
        for(String token : document.getOrganizationIds().split(","))
        {
            if(token.trim().length() > 0)
            {
                organizationIds.add(Long.parseLong(token));
            }
        }
        return organizationIds;
    }

    private Element organizationElm(Long organizationId)
    {
        return attr(elm("organization"), "id", organizationId.toString());
    }

    private Element documentElm(DocumentNodeResource document, Long orgId, DateFormat dateFormat)
        throws DocumentException
    {
        Document meta = DocumentHelper.parseText(document.getMeta());
        Element doc = attr(elm("document"), "id", document.getIdString());
        doc.add(elm("creationTime", dateFormat.format(document.getCreationTime())));
        doc.add(elm("modificationTime", dateFormat.format(document.getCustomModificationTime())));
        doc.add(elm("createdBy", getUid(document.getCreatedBy())));
        doc.add(elm("modifiedBy", getUid(document.getModifiedBy())));
        doc.add(elm("title", text(nvl(enc(document.getTitle())))));
        doc.add(elm("subTitle", text(nvl(enc(document.getSubTitle())))));
        doc.add(elm("abstract", cdata(nvl(document.getAbstract()))));
        doc.add(elm("content", cdata(nvl((document.getContent())))));
        Node authors = meta.selectSingleNode("/meta/authors");
        authors.detach();
        doc.add(authors);
        Node sources = meta.selectSingleNode("/meta/sources");
        sources.detach();
        doc.add(sources);
        Node event = meta.selectSingleNode("/meta/event");
        event.detach();
        ((Branch)event).content().add(0, elm("place", nvl(enc(document.getEventPlace()))));
        doc.add(event);
        Node organization = meta.selectSingleNode("/meta/organizations/organization[id='" + orgId
            + "']");
        organization.detach();
        doc.add(organization);
        return doc;
    }

    private static class DocumentComparator
        implements Comparator<DocumentNodeResource>
    {
        public static final DocumentComparator INSTANCE = new DocumentComparator();

        @Override
        public int compare(DocumentNodeResource doc1, DocumentNodeResource doc2)
        {
            return doc1.getCustomModificationTime().compareTo(doc2.getCustomModificationTime());
        }
    }

    private static String getUid(Subject subject)
    {
        if(subject == null)
        {
            return "";
        }
        else
        {
            String dn = subject.getName();
            return dn.substring(4, dn.indexOf(','));
        }
    }

    private static String nvl(String s)
    {
        return s != null ? s : "";
    }

    // RSS/Atom news feed for organization

    public String getOrganizationNewsFeed(Parameters parameters)
        throws IOException, FeedException, ProcessingException, CategoryException, SiteException, ConfigurationException
    {
        long organizationId = parameters.getLong(newsFeedIdParam);
        String feedContents = loadCachedFeed(organizationId);
        if(feedContents == null)
        {
            CoralSession coralSession = coralSessionFactory.getAnonymousSession();
            try
            {
                Organization organization = organizations.getOrganization(organizationId);
                if(organization == null)
                {
                    throw new ProcessingException("organization " + organizationId + " not found");
                }
                Date startDate = new Date();
                Date endDate = offsetDate(startDate, newsFeedQueryDays);
                List<DocumentNodeResource> documents = queryDocuments(
                    getSites(newsFeedSites, siteService, coralSession), endDate, organizationId,
                    coralSession);
                SyndFeed feed = buildFeed(organization, documents, startDate, endDate, coralSession);
                feedContents = saveCachedFeed(organizationId, feed);
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
