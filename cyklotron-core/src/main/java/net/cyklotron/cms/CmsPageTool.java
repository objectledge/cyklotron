package net.cyklotron.cms;

import org.objectledge.web.mvc.tools.PageTool;


/**
 * A context tool used for CMS applications using JavaScript and CSS files, provides support for
 * site skins.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: CmsPageTool.java,v 1.3 2005-01-18 17:38:09 pablo Exp $
 */
public class CmsPageTool extends PageTool
{
    //-------------------------------
    // LINK BASE CLASS

    public static short SKIN_RESOURCE = 3;

    /**
     * This class add ability to link skin specific resources.
     */
    public class SkinResourceLink extends PageTool.ResourceLink
    {
        public SkinResourceLink(String href, short type)
        {
            super(href, type);
        }

        /** Getter for href attribute value.
         * @return Value of href attribute.
         */
        public String getHref()
        {
            if(type == SKIN_RESOURCE)
            {
                return ((CmsLinkTool)linkTool).skinResource(href).toString();
            }
            else
            {
                return super.getHref();
            }
        }
    }

    protected ResourceLink getResourceLink(String href, short type)
    {
        return new SkinResourceLink(href, type);
    }

    //-------------------------------
    // STYLE LINKS

    /** Adds a style link to skin styles with a default priority equal to <code>0</code>. */
    public void addSkinStyleLink(String href)
    {
        addStyleLink(href, SKIN_RESOURCE, 0);
    }

    /** Adds a style link to skin styles with a given priority. */
    public void addSkinStyleLink(String href, int priority)
    {
        addStyleLink(href, SKIN_RESOURCE, priority);
    }

    //-------------------------------
    // SCRIPT LINKS

    /** Adds a common script link, with no charset attribute defined. */
    public void addSkinScriptLink(String src)
    {
        this.addScriptLink(src, SKIN_RESOURCE, null);
    }

    /** Adds a common script link, with charset attribute defined. */
    public void addSkinScriptLink(String src, String charset)
    {
        this.addScriptLink(src, SKIN_RESOURCE, charset);
    }
}
