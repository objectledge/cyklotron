package net.cyklotron.cms.modules.actions.fixes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.structure.StructureService;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.naming.ContextFactory;
import org.objectledge.naming.ContextHelper;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

public class BAZYNGO318LdapCleanUp
    extends BaseCMSAction
{
    private final ContextHelper directory;

    private final static List<ModificationItem> EMPTY_LIST = Collections.emptyList();

    private final static List<String> ATTRIBUTES_FOR_REMOVAL = Arrays.asList("maildirQuota",
        "localMail", "homeDirectory");

    private final static List<String> CLASSES_FOR_REMOVAL = Arrays.asList("cyklotronMailAccount",
        "cyklotronSystemUser", "cyklotronPerson");

    private final static List<String> CLASSES_FOR_ADDITION = Arrays.asList("shadowAccount",
        "inetOrgPerson", "logonTracking");

    public BAZYNGO318LdapCleanUp(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, ContextFactory factory)
        throws NamingException
    {
        super(logger, structureService, cmsDataFactory);
        directory = new ContextHelper(factory, "people", logger);
    }

    @Override
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        logger.info("LDAP CLEANUP RAN");
        // for testing purposes I gonna do just clean up on one specific person and after confirming
        // that it works
        // change for all people
        try
        {
            DirContext dirContext = directory.getBaseDirContext();
            javax.naming.Context userContext = (javax.naming.Context)dirContext
                .lookup("uid=marwald");
            processUser(userContext);
            /*
             * NamingEnumeration<NameClassPair> list = dirContext.list(""); int i = 0; while
             * (list.hasMore()) { NameClassPair nc = list.next(); javax.naming.Context userContext =
             * (javax.naming.Context) dirContext.lookup(nc.getName()); processUser(userContext);
             * System.out.println(nc); i++; if ( i > 10) { break; }
             * System.out.println(nc.getNameInNamespace()); System.out.println(nc.getName()); } i =
             * 0; NamingEnumeration<Binding> bindings = dirContext.listBindings(""); Object object =
             * dirContext.lookup("uid=marwald"); System.out.println("retrived object");
             * System.out.println(object);
             */
            /*
             * while(bindings.hasMore()) { Binding binding = bindings.next();
             * System.out.println(binding); i++; if ( i > 10) { break; } }
             */
        }
        catch(NamingException e)
        {
            throw new RuntimeException("cleanup failed", e);
        }

    }

    private void processUser(javax.naming.Context userContext)
        throws NamingException
    {
        DirContext dirContext = (DirContext)userContext;
        Attributes attributes = dirContext.getAttributes("");

        List<ModificationItem> modificationItems = new ArrayList<>();
 
        modificationItems.addAll(addClasses(CLASSES_FOR_ADDITION));
        modificationItems.addAll(modifyObjectClass(attributes, CLASSES_FOR_REMOVAL,
            DirContext.REMOVE_ATTRIBUTE));
        modificationItems.addAll(mergeMail(attributes));
        modificationItems.addAll(cleanAddress(attributes));
        modificationItems.addAll(cleanCity(attributes));
        modificationItems.addAll(cleanCountry(attributes));
        modificationItems.addAll(removeAttributes(ATTRIBUTES_FOR_REMOVAL, attributes));

        dirContext.modifyAttributes("", (ModificationItem[])modificationItems
            .toArray(new ModificationItem[modificationItems.size()]));
        dirContext.close();
        // debug
        /*
         * while(allAttributes.hasMore()) { Attribute next = allAttributes.next();
         * System.out.println(next.getID()); }
         */
    }

    private Collection<? extends ModificationItem> modifyObjectClass(Attributes attributes,
        List<String> classesForRemoval, int operationMode)
        throws NamingException
    {
        List<ModificationItem> result = new ArrayList<>();
        for(String clazz : classesForRemoval)
        {
            result.addAll(modifyClass(clazz, attributes, operationMode));
        }
        return result;
    }
    
    private Collection<? extends ModificationItem> addClasses(List<String> classes)
    {
        List<ModificationItem> result = new ArrayList<>();
        for(String clazz : classes)
        {
            result.add(new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute("objectClass",
                clazz)));
        }
        return result;
    }

    private Collection<? extends ModificationItem> modifyClass(String clazz, Attributes attributes,
        int operationMode)
        throws NamingException
    {
        Attribute attribute = attributes.get("objectClass");
        if(attribute != null)
        {
            for(NamingEnumeration<?> e = attribute.getAll(); e.hasMore();)
            {
                if(clazz.equals((String)e.next()))
                {
                    List<ModificationItem> items = new ArrayList<>(1);
                    items.add(new ModificationItem(operationMode, new BasicAttribute("objectClass",
                        clazz)));
                    return items;
                }
            }
        }
        return EMPTY_LIST;
    }

    private Collection<? extends ModificationItem> removeAttributes(
        List<String> attributesForRemoval, Attributes attributes)
    {
        List<ModificationItem> result = new ArrayList<>();
        for(String attrName : attributesForRemoval)
        {
            result.addAll(removeAttribute(attrName, attributes));
        }
        return result;
    }

    private Collection<? extends ModificationItem> removeAttribute(String name,
        Attributes attributes)
    {
        Attribute attribute = attributes.get(name);
        if(attribute != null)
        {
            List<ModificationItem> items = new ArrayList<>(1);
            items.add(new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute(name)));
            return items;
        }
        return EMPTY_LIST;
    }

    private Collection<? extends ModificationItem> cleanCountry(Attributes attributes)
        throws NamingException
    {
        final String attrName = "c";
        Attribute attribute = attributes.get(attrName);
        if(attribute != null)
        {
            String country = (String)attribute.get();
            if(country.trim().toLowerCase().equals("kraj"))
            {
                List<ModificationItem> items = new ArrayList<>(1);
                items.add(new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute(
                    attrName)));
                return items;
            }

        }
        return EMPTY_LIST;
    }

    private Collection<? extends ModificationItem> cleanCity(Attributes attributes)
        throws NamingException
    {
        final String attrName = "l";
        Attribute attribute = attributes.get(attrName);
        if(attribute != null)
        {
            String city = (String)attribute.get();
            if(city.trim().toLowerCase().equals("miasto"))
            {
                List<ModificationItem> items = new ArrayList<>(1);
                items.add(new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute(
                    attrName)));
                return items;
            }

        }
        return EMPTY_LIST;
    }

    private Collection<? extends ModificationItem> cleanAddress(Attributes attributes)
        throws NamingException
    {
        final String attrName = "postalCode";
        Attribute attribute = attributes.get(attrName);
        if(attribute != null)
        {
            String postalCode = (String)attribute.get();
            if(postalCode.trim().toLowerCase().equals("00-000"))
            {
                List<ModificationItem> items = new ArrayList<>(2);
                items.add(new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute(
                    attrName)));
                if(attributes.get("postalAddress") != null)
                {
                    items.add(new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute(
                        "postalAddress")));
                }
                return items;
            }

        }
        return EMPTY_LIST;
    }

    private Collection<? extends ModificationItem> mergeMail(Attributes attributes)
        throws NamingException
    {
        final String attrName = "altMail";
        Attribute attribute = attributes.get(attrName);
        if(attribute != null)
        {
            List<ModificationItem> items = new ArrayList<>(2);
            Set<String> mails = new HashSet<>();
            Attribute mailAttribute = attributes.get("mail");
            for(NamingEnumeration<?> e = mailAttribute.getAll(); e.hasMore();)
            {
                mails.add((String)e.next());
            }

            for(NamingEnumeration<?> e = attribute.getAll(); e.hasMore();)
            {
                String altMail = (String)e.next();
                if(mails.add(altMail))
                {
                    // new mail need to be added to mail attribute
                    items.add(new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute(
                        "mail", altMail)));
                }
            }
            // remove all values from altMail
            items.add(new ModificationItem(DirContext.REMOVE_ATTRIBUTE,
                new BasicAttribute(attrName)));
            return items;

        }
        return EMPTY_LIST;
    }

}
