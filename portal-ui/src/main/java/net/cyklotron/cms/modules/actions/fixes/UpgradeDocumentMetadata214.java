package net.cyklotron.cms.modules.actions.fixes;

import static net.cyklotron.cms.documents.DocumentMetadataHelper.elm;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.QName;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.dom4j.xpath.DefaultXPath;
import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.query.MalformedQueryException;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;
import org.objectledge.templating.TemplatingContext;

import net.cyklotron.cms.documents.DocumentMetadataHelper;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.locations.Location;
import net.cyklotron.cms.locations.LocationDatabaseService;

public class UpgradeDocumentMetadata214
    implements Valve
{
    private static final QName ORGANIZATION_QNAME = new QName("organization");

    private static final Location BLANK_LOCATION = new Location("", "", "", "");

    private static final XPath ADDRESS_XPATH = new DefaultXPath("/meta/organisation/address");

    private static final XPath ORGANIZATION_XPATH = new DefaultXPath("/meta/organisation");
    
    private static final XPath ORGANIZATIONS_XPATH = new DefaultXPath("/meta/organizations");

    private Logger logger;

    private LocationDatabaseService locationDatabaseService;

    public UpgradeDocumentMetadata214(LocationDatabaseService locationDatabaseService, Logger logger)
    {
        this.logger = logger;
        this.locationDatabaseService = locationDatabaseService;
    }

    @Override
    public void process(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = context.getAttribute(CoralSession.class);
        TemplatingContext templatingContext = context.getAttribute(TemplatingContext.class);

        QueryResults results;

        try
        {
            results = coralSession.getQuery().executeQuery(
                "FIND RESOURCE FROM documents.document_node");
        }
        catch(MalformedQueryException e)
        {
            throw new ProcessingException("cannot get 'documents.document_node' resources", e);
        }

        int counter = 0;
        SAXReader saxReader = new SAXReader();
        for(QueryResults.Row row : results)
        {
            try
            {
                DocumentNodeResource node = (DocumentNodeResource)row.get();
                synchronized(node)
                {
                    if(node.getMeta() != null && !node.getMeta().trim().isEmpty())
                    {
                        Document doc = saxReader.read(new StringReader(node.getMeta().replaceAll(
                            "&", "")));

                        if(ORGANIZATIONS_XPATH.selectSingleNode(doc) != null)
                        {
                            // already upgraded
                            continue;
                        }

                        convertMetaDom(doc);
                        
                        node.setMeta(DocumentMetadataHelper.dom4jToText(doc));
                        node.update();
                    }
                }
                if(counter % 100 == 0)
                {
                    System.out.println("converted " + counter + " documents");
                }
                counter++;
            }
            catch(Exception e)
            {
                logger.error("malformed metadada in resource #" + row.getId(), e);
            }
        }
        templatingContext.put("result", "success");
    }

    private Location parseOrganisationAddress(String organizationAddress)
        throws IOException
    {
        String[] fields = organizationAddress.replaceAll("\\s*[-]\\s*", "-").replaceAll(",", " ")
            .split("\\s+");
        String province = "";
        String city = "";
        String postCode = "";
        String street = "";

        for(String field : fields)
        {
            if(!field.isEmpty())
            {
                if(locationDatabaseService.exactMatchExists("city", field.substring(0, 1).toUpperCase()
                    + field.substring(1).toLowerCase()))
                {
                    city = field;
                }
                else if(locationDatabaseService.exactMatchExists("postCode", field))
                {
                    postCode = field;
                }
                else
                {
                    street += field + " ";
                }
            }
        }
        return new Location(province, city, street.trim(), postCode);
    }

    private void convertMetaDom(Document doc)
        throws DocumentException, IOException
    {
        Element address = (Element)ADDRESS_XPATH.selectSingleNode(doc);
        String organizationAddress = address.getTextTrim().replaceAll("<[^>]*?>", " ");
        address.detach();

        Location location = BLANK_LOCATION;        
        if(!organizationAddress.trim().isEmpty())
        {
            location = parseOrganisationAddress(organizationAddress);
        }

        Element organization = (Element)ORGANIZATION_XPATH.selectSingleNode(doc);
        // rename organisation -> organization
        organization.setQName(ORGANIZATION_QNAME);
        // find <name> element
        int nameIndex = 0;
        for(Node domNode : (List<Node>)organization.content())
        {
            if(domNode instanceof Element && ((Element)domNode).getName().equals("name"))
            {
                break;
            }
            nameIndex++;
        }
        // insert <address> after <name>
        ((List<Element>)organization.content()).add(nameIndex+1, elm("address", elm("street", location
            .getStreet()), elm("postcode", location.getPostCode()),
            elm("city", location.getCity()), elm("province", location.getProvince())));
        organization.detach();

        // append <event>
        doc.getRootElement().add(
            elm("event", elm("address", elm("street"), elm("postcode"), elm("city"),
                elm("province"))));

        // append <organizations> with single <organization> child
        doc.getRootElement().add(elm("organizations", organization));        
    }
}