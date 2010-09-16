package net.cyklotron.cms.modules.views.fixes;

import static net.cyklotron.cms.documents.DocumentMetadataHelper.selectFirstText;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
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
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.ngodatabase.Location;
import net.cyklotron.cms.ngodatabase.LocationDatabaseService;
import net.cyklotron.cms.ngodatabase.LocationDatabaseServiceImpl;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id$
 */
public class ParseAddressMetadata
    extends BaseCoralView
{
    private Logger logger;

    private LocationDatabaseService locationDatabaseService;

    public ParseAddressMetadata(Context context,
        LocationDatabaseService locationDatabaseService, Logger logger)
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
                String[] fields;
                String province;
                String city;
                String postCode;
                String street;

                node = (DocumentNodeResource)res;
                Document metaDom = textToDom4j(node.getMeta());
                organizedAddress = stripTags(selectFirstText(metaDom, "/meta/organisation/address"));

                if(!organizedAddress.trim().isEmpty())
                {
                    fields = organizedAddress.replaceAll("\\s*[-]\\s*", "-").replaceAll(",", " ").split("\\s+");
                    province = "";
                    city = "";
                    postCode = "";
                    street = "";
                    for(String field : fields)
                    {
                        if(!field.isEmpty())
                        {
                            if(locationDatabaseService.containsCity(
                                field.substring(0, 1).toUpperCase()
                                + field.substring(1).toLowerCase()))
                            {
                                city = field;
                            }
                            else if(locationDatabaseService.containsPostCode(field))
                            {
                                postCode = field;
                            }
                            else
                            {
                                street += field + " ";
                            }
                        }
                    }
                    adresses.put(node.getId(), organizedAddress);
                    locations.put(node.getId(), new Location(province, city, street.trim(), postCode));
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

    public static Document textToDom4j(String meta)
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

    public static String stripTags(String s)
    {
        return s == null ? s : s.replaceAll("<[^>]*?>", " ");
    }
}
