package net.cyklotron.cms.modules.views.fixes;

import static net.cyklotron.cms.documents.DocumentMetadataHelper.selectFirstText;
import static net.cyklotron.cms.documents.DocumentMetadataHelper.elm;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.dom.DOMDocument;
import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.modules.views.BaseCoralView;
import org.objectledge.coral.query.MalformedQueryException;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.html.HTMLException;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.documents.DocumentMetadataHelper;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.ngodatabase.Location;
import net.cyklotron.cms.ngodatabase.LocationDatabaseService;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id$
 */
public class ParseEventPlaceField
    extends BaseCoralView
{
    private Logger logger;

    private LocationDatabaseService locationDatabaseService;

    public ParseEventPlaceField(Context context, LocationDatabaseService locationDatabaseService,
        Logger logger)
    {
        super(context);
        this.logger = logger;
        this.locationDatabaseService = locationDatabaseService;
    }

    /**
     * Performs the action.
     */
    @Override
    public void process(Parameters parameters, TemplatingContext templatingContext,
        MVCContext mvcContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
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
        Resource[] resources = results.getArray(1);
        String organizedAddress;
        DocumentNodeResource node;
        Map<Long, String> adresses = new HashMap<Long, String>();
        Map<Long, Location> locations = new HashMap<Long, Location>();

        for(Resource res : resources)
        {
            try
            {
                node = (DocumentNodeResource)res;
                if(node.getMeta() == null)
                {
                    continue;
                }
                if(!node.getMeta().trim().isEmpty())
                {
                    ConvertMetaDom(node);
                }
            }
            catch(HTMLException e)
            {
                throw new RuntimeException("malformed metadada in resource ", e);
            }
        }

        templatingContext.put("adresses", adresses);
        templatingContext.put("locations", locations);
    }

    private void ConvertMetaDom(DocumentNodeResource node)
        throws HTMLException
    {

        Document doc = textToDom4j(node.getMeta());
        String organizedBy = selectFirstText(doc, "/meta/organisation/name");        
        String organizedProvince = selectFirstText(doc, "/meta/organisation/address/province");
        String organizedPostCode = selectFirstText(doc, "/meta/organisation/address/postcode");
        String organizedCity = selectFirstText(doc, "/meta/organisation/address/city");
        String organizedStreet = selectFirstText(doc, "/meta/organisation/address/street");
        String organizedPhone = selectFirstText(doc, "/meta/organisation/tel");
        String organizedFax = selectFirstText(doc, "/meta/organisation/fax");
        String organizedEmail = selectFirstText(doc, "/meta/organisation/e-mail");
        String organizedWww = selectFirstText(doc, "/meta/organisation/url");
        String organizedId = selectFirstText(doc, "/meta/organisation/id");
        String sourceName = selectFirstText(doc, "/meta/sources/source/name");
        String sourceUrl = selectFirstText(doc, "/meta/sources/source/url");
        String proposerCredentials = selectFirstText(doc, "/meta/authors/author/name");
        String proposerEmail = selectFirstText(doc, "/meta/authors/author/e-mail");

        Element element = elm("meta", elm("authors", elm("author",
            elm("name", proposerCredentials), elm("e-mail", proposerEmail))), elm("sources", elm(
            "source", elm("name", sourceName), elm("url", sourceUrl))), elm("editor"), elm(
            "eventdata", elm("address", elm("street", ""), elm("postcode", ""), elm("city", ""),
                elm("province", ""))), elm("organisation", elm("name", organizedBy), elm("address",
            elm("street", organizedStreet), elm("postcode", organizedPostCode), elm("city",
                organizedCity), elm("province", organizedProvince)), elm("tel", organizedPhone),
            elm("fax", organizedFax), elm("e-mail", organizedEmail), elm("url", organizedWww), elm(
                "id", organizedId)));

        Document convertedDoc = DocumentMetadataHelper.doc(element);
        String metaDom = DocumentMetadataHelper.dom4jToText(convertedDoc);
        node.setMeta(metaDom);
        node.update();
    }

    private static Document textToDom4j(String meta)
        throws HTMLException
    {
        if(meta != null && meta.trim().length() > 0)
        {
            try
            {
                return DocumentHelper.parseText(meta.replaceAll("&", ""));
            }
            catch(org.dom4j.DocumentException e)
            {
                throw new HTMLException("document metadata contains invalid XML", e);
            }
        }
        else
        {
            return new DOMDocument();
        }
    }

    private static String stripTags(String s)
    {
        return s == null ? s : s.replaceAll("<[^>]*?>", " ");
    }
}
