package net.cyklotron.cms.ngodatabase.organizations;

import static net.cyklotron.cms.documents.DocumentMetadataHelper.attr;
import static net.cyklotron.cms.documents.DocumentMetadataHelper.cdata;
import static net.cyklotron.cms.documents.DocumentMetadataHelper.doc;
import static net.cyklotron.cms.documents.DocumentMetadataHelper.elm;
import static net.cyklotron.cms.documents.DocumentMetadataHelper.enc;
import static net.cyklotron.cms.documents.DocumentMetadataHelper.text;
import static org.objectledge.filesystem.FileSystem.directoryPath;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.filesystem.FileSystem;

import net.cyklotron.cms.documents.DocumentNodeResource;

public class OutgoingOrganizationsService
{
    private static final String OUTGOING_FILE = "ngo/database/outgoing/update.xml";

    private static final OutputFormat OUTGOING_FORMAT = new OutputFormat("  ", true, "UTF-8");

    private final int outgoingQueryDays;

    private final Configuration outgoingSites;

    private final CoralSessionFactory coralSessionFactory;

    private final FileSystem fileSystem;

    private final Logger logger;

    private final DateFormat dateFormat;

    private final UpdatedDocumentsProvider updatedDocumentsProvider;

    public OutgoingOrganizationsService(Configuration outgoingConfig,
        UpdatedDocumentsProvider updatedDocumentsProvider, CoralSessionFactory coralSessionFactory,
        FileSystem fileSystem, Logger logger, DateFormat dateFormat)
        throws ConfigurationException
    {
        this.updatedDocumentsProvider = updatedDocumentsProvider;
        this.coralSessionFactory = coralSessionFactory;
        this.fileSystem = fileSystem;
        this.logger = logger;
        this.dateFormat = dateFormat;
        this.outgoingQueryDays = outgoingConfig.getChild("queryDays").getValueAsInteger();
        this.outgoingSites = outgoingConfig.getChild("sites");
    }

    public void updateOutgoing()
    {
        // query documents
        List<DocumentNodeResource> documents = null;
        Date endDate = updatedDocumentsProvider.offsetDate(new Date(), outgoingQueryDays);
        CoralSession coralSession = coralSessionFactory.getAnonymousSession();
        try
        {
            documents = updatedDocumentsProvider.queryDocuments(updatedDocumentsProvider.getSites(
                outgoingSites, coralSession), endDate, -1L, coralSession);
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

    private static Element organizationElm(Long organizationId)
    {
        return attr(elm("organization"), "id", organizationId.toString());
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

    private static List<Long> getOrganizationIds(DocumentNodeResource document)
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

    private static String nvl(String s)
    {
        return s != null ? s : "";
    }

    private static Element documentElm(DocumentNodeResource document, Long orgId,
        DateFormat dateFormat)
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
        @SuppressWarnings("unchecked")
        List<Element> content = ((Branch)event).content();
        content.add(0, elm("place", nvl(enc(document.getEventPlace()))));
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
}
