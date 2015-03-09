package net.cyklotron.cms;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.cyklotron.cms.canonical.LinkCanonicalRuleResource;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.util.PriorityComparator;

import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.tools.PageTool;

/**
 * A context tool used for CMS applications using JavaScript and CSS files, provides support for
 * site skins.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: CmsPageTool.java,v 1.9 2005-12-10 02:38:48 pablo Exp $
 */
public class CmsPageTool
    extends PageTool
{
    private final Context context;
    
    private final CategoryService categoryService;

    /**
     * Component constructor.
     * 
     * @param parentLinkTool the link tool used to generate links to page content resources.
     */
    public CmsPageTool(CmsLinkTool parentLinkTool, HttpContext httpContext,
        PageTool.Configuration config, CategoryService categoryService, Context context)
    {
        super(parentLinkTool, httpContext, config);        
        this.categoryService = categoryService;
        this.context = context;
    }

    // -------------------------------
    // LINK CLASS

    /**
     * This class add ability to link skin specific content resources.
     */
    public class SkinContentLink extends PageTool.ContentLink
    {
        public SkinContentLink(String href)
        {
            super(href);
        }

        /**
         * {@inheritDoc}
         */
        public String toString()
        {
            return ((CmsLinkTool)linkTool).skinResource(href).toString();
        }
    }

    /*
     * set Canonical link from node. 
     */
    public void setCanonicalLink(NavigationNodeResource node)
    {
        String link = null;
        if(node.isQuickPathDefined())
        {
            link = ((CmsLinkTool)linkTool).setNode(node).absolute().toString();
        }
        else
        {
            CoralSession coralSession = getCoralSession();
            List<CategoryResource> categories = Arrays.asList(categoryService.getCategories(
                coralSession, node, true));
            List<Resource> rules = Arrays.asList(coralSession.getStore().getResourceByPath(
                "/cms/canonicalLinkRules/*"));
            if(categories.size() > 0 && rules.size() > 0)
            {
                Collections.sort(rules, Collections.reverseOrder(new PriorityComparator()));
                for(Resource rule : rules)
                {
                    if(rule instanceof LinkCanonicalRuleResource)
                    {
                        LinkCanonicalRuleResource linkCanonicalRule = (LinkCanonicalRuleResource)rule;
                        if(categories.contains(linkCanonicalRule.getCategory()))
                        {
                            link = linkCanonicalRule.getLinkPattern().replace("{id}",
                                node.getIdString());
                            break;
                        }
                    }
                }
            }
            if(link == null)
            {
                link = ((CmsLinkTool)linkTool).setNode(node).absolute().toString();
            }
        }
        setCanonicalLink(link);
    }
    
    public CoralSession getCoralSession()
    {   
        return (CoralSession)context.getAttribute(CoralSession.class);
    }

    // -------------------------------
    // STYLE LINKS

    /** Adds a style link to skin styles with a default priority equal to <code>0</code>. */
    public void addSkinStyleLink(String href)
    {
        addSkinStyleLink(href, 0);
    }

    /** Adds a style link to skin styles with a given priority. */
    public void addSkinStyleLink(String href, int priority)
    {
        addStyleLink(new SkinContentLink(href), priority, null, null, null);
    }

    /** Adds a style link to skin styles with a given priority. */
    public void addSkinStyleLink(String href, int priority, String media)
    {
        addStyleLink(new SkinContentLink(href), priority, media, null, null);
    }
    
    /** Adds a style link to skin styles with a given priority. */
    public void addSkinStyleLink(String href, int priority, String media, String rel)
    {
        addStyleLink(new SkinContentLink(href), priority, media, rel, null);
    }

    /** Adds a style link to skin styles with a given priority. */
    public void addSkinStyleLink(String href, int priority, String media, String rel, String type)
    {
        addStyleLink(new SkinContentLink(href), priority, media, rel, type);
    }
    
    //-------------------------------
    // SCRIPT LINKS

    /** Adds a common script link, with no charset attribute defined. */
    public void addSkinScriptLink(String src)
    {
        addSkinScriptLink(src, null);
    }

    /** Adds a common script link, with charset attribute defined. */
    public void addSkinScriptLink(String src, String charset)
    {
        addScriptLink(new SkinContentLink(src), charset);
    }
}
