package net.cyklotron.cms.structure.internal;

import static net.cyklotron.cms.documents.DocumentMetadataHelper.elm;
import static net.cyklotron.cms.documents.DocumentMetadataHelper.selectFirstText;
import static net.cyklotron.cms.structure.internal.ProposedDocumentData.dec;
import static net.cyklotron.cms.structure.internal.ProposedDocumentData.enc;
import static net.cyklotron.cms.structure.internal.ProposedDocumentData.stripTags;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;
import org.dom4j.Node;
import org.objectledge.parameters.Parameters;
import org.objectledge.templating.TemplatingContext;

public class OrganisationData
{
    public static final OrganisationData BLANK = new OrganisationData();

    private String name = "";

    private String province = "";

    private String postCode = "";

    private String city = "";

    private String street = "";

    private String phone = "";

    private String fax = "";

    private String email = "";

    private String www = "";

    private String id = "";

    public static OrganisationData get(List<OrganisationData> organisations, int index)
    {
        if(index < organisations.size())
        {
            return organisations.get(index);
        }
        else
        {
            return BLANK;
        }
    }

    public boolean isBlank()
    {
        return name.length() + province.length() + postCode.length() + city.length()
            + street.length() + phone.length() + fax.length() + email.length() + www.length()
            + id.length() == 0;
    }

    public void fromParmeters(Parameters parameters, String prefix)
    {
        name = stripTags(dec(parameters.get(prefix + "_by", "")));
        province = stripTags(dec(parameters.get(prefix + "_province", "")));
        postCode = stripTags(dec(parameters.get(prefix + "_postcode", "")));
        city = stripTags(dec(parameters.get(prefix + "_city", "")));
        street = stripTags(dec(parameters.get(prefix + "_street", "")));
        phone = stripTags(dec(parameters.get(prefix + "_phone", "")));
        fax = stripTags(dec(parameters.get(prefix + "_fax", "")));
        email = stripTags(dec(parameters.get(prefix + "_email", "")));
        www = stripTags(dec(parameters.get(prefix + "_www", "")));
        id = stripTags(dec(parameters.get(prefix + "_id", "0")));
    }

    public static List<OrganisationData> fromParameters(Parameters parameters)
    {
        List<OrganisationData> organisations = new ArrayList<OrganisationData>();
        int index = 1;
        while(parameters.isDefined("organized_" + index + "_by"))
        {
            OrganisationData organisation = new OrganisationData();
            organisation.fromParmeters(parameters, "organized_" + index);
            organisations.add(organisation);
        }
        return organisations;
    }

    public void toTemplatingContext(TemplatingContext templatingContext, String prefix)
    {
        templatingContext.put(prefix + "_by", enc(name));
        templatingContext.put(prefix + "_province", enc(province));
        templatingContext.put(prefix + "_postcode", enc(postCode));
        templatingContext.put(prefix + "_city", enc(city));
        templatingContext.put(prefix + "_street", enc(street));
        templatingContext.put(prefix + "_phone", enc(phone));
        templatingContext.put(prefix + "_fax", enc(fax));
        templatingContext.put(prefix + "_email", enc(email));
        templatingContext.put(prefix + "_www", enc(www));
        templatingContext.put(prefix + "_id", enc(id));
    }

    public static void toTemplatingContext(List<OrganisationData> organisations,
        TemplatingContext templatingContext)
    {
        int index = 1;
        for(OrganisationData organisation : organisations)
        {
            organisation.toTemplatingContext(templatingContext, "organised_" + index);
        }
        templatingContext.put("organisations_count", organisations.size());
    }

    public void fromMeta(Node node)
    {
        name = stripTags(selectFirstText(node, "/name"));
        province = stripTags(selectFirstText(node, "/address/province"));
        postCode = stripTags(selectFirstText(node, "/address/postcode"));
        city = stripTags(selectFirstText(node, "/address/city"));
        street = stripTags(selectFirstText(node, "/address/street"));
        phone = stripTags(selectFirstText(node, "/tel"));
        fax = stripTags(selectFirstText(node, "/fax"));
        email = stripTags(selectFirstText(node, "/e-mail"));
        www = stripTags(selectFirstText(node, "/url"));
        id = stripTags(selectFirstText(node, "/id"));
    }

    public static List<OrganisationData> fromMeta(Node metaNode, String xpath)
    {
        List<Node> nodes = (List<Node>)metaNode.selectNodes(xpath + "/organisation");
        List<OrganisationData> oragnisations = new ArrayList<OrganisationData>(nodes.size());
        for(Node node : nodes)
        {
            OrganisationData organisation = new OrganisationData();
            organisation.fromMeta(node);
            oragnisations.add(organisation);
        }
        return oragnisations;
    }

    public Node toMeta()
    {
        return elm("organisation", elm("name", enc(name)), elm("address",
            elm("street", enc(street)), elm("postcode", enc(postCode)), elm("city", enc(city)),
            elm("province", enc(province))), elm("tel", enc(phone)), elm("fax", enc(fax)), elm(
            "e-mail", enc(email)), elm("url", enc(www)), elm("id", enc(id)));
    }

    public static Node toMeta(List<OrganisationData> organisations)
    {
        Node[] nodes = new Node[organisations.size()];
        for(int i = 0; i < organisations.size(); i++)
        {
            nodes[i] = organisations.get(i).toMeta();
        }
        return elm("organisations", nodes);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getProvince()
    {
        return province;
    }

    public void setProvince(String province)
    {
        this.province = province;
    }

    public String getPostCode()
    {
        return postCode;
    }

    public void setPostCode(String postCode)
    {
        this.postCode = postCode;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getStreet()
    {
        return street;
    }

    public void setStreet(String street)
    {
        this.street = street;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getFax()
    {
        return fax;
    }

    public void setFax(String fax)
    {
        this.fax = fax;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getWww()
    {
        return www;
    }

    public void setWww(String www)
    {
        this.www = www;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void dump(StringBuilder buff)
    {
        buff.append("Name: ").append(name).append("\n");
        buff.append("Province: ").append(province).append("\n");
        buff.append("Code: ").append(postCode).append("\n");
        buff.append("City: ").append(city).append("\n");
        buff.append("Street: ").append(street).append("\n");
        buff.append("Phone: ").append(phone).append("\n");
        buff.append("Fax: ").append(fax).append("\n");
        buff.append("Email: ").append(email).append("\n");
        buff.append("URL: ").append(www).append("\n");
        buff.append("Id: ").append(id).append("\n");
    }

    public static void dump(List<OrganisationData> organisations, StringBuilder buff)
    {
        int index = 1;
        for(OrganisationData organisation : organisations)
        {
            buff.append("Oranisation " + index + ":\n");
            organisation.dump(buff);
        }
    }
}
