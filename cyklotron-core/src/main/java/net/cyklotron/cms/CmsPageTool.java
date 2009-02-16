package net.cyklotron.cms;

import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.tools.PageTool;


/**
 * A context tool used for CMS applications using JavaScript and CSS files, provides support for
 * site skins.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: CmsPageTool.java,v 1.9 2005-12-10 02:38:48 pablo Exp $
 */
public class CmsPageTool extends PageTool
{
    /** 
     * Component constructor.
     * @param parentLinkTool the link tool used to generate links to page content resources.
     */
    public CmsPageTool(CmsLinkTool parentLinkTool, HttpContext httpContext)
    {
        super(parentLinkTool, httpContext);
    }

    //-------------------------------
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

    //-------------------------------
    // STYLE LINKS

    /** Adds a style link to skin styles with a default priority equal to <code>0</code>. */
    public void addSkinStyleLink(String href)
    {
        addSkinStyleLink(href, 0);
    }

    /** Adds a style link to skin styles with a given priority. */
    public void addSkinStyleLink(String href, int priority)
    {
        addStyleLink(new SkinContentLink(href), priority, null, null);
    }

    /** Adds a style link to skin styles with a given priority. */
    public void addSkinStyleLink(String href, int priority, String media)
    {
        addStyleLink(new SkinContentLink(href), priority, media, null);
    }
    
    /** Adds a style link to skin styles with a given priority. */
    public void addSkinStyleLink(String href, int priority, String media, String rel)
    {
        addStyleLink(new SkinContentLink(href), priority, media, rel);
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
