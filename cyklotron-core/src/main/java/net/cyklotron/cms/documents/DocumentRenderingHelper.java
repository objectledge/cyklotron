package net.cyklotron.cms.documents;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.pipeline.ProcessingException;

import pl.caltha.encodings.HTMLEntityDecoder;

import com.sun.org.apache.xml.internal.utils.URI.MalformedURIException;

/**
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: DocumentRenderingHelper.java,v 1.4 2005-01-20 05:45:19 pablo Exp $
 */
public class DocumentRenderingHelper
{
    private HTMLService htmlService;

    private HTMLEntityDecoder entityDecoder = new HTMLEntityDecoder();

    private SiteService siteService;
    
    private StructureService structureService;
    
    /** owner document */
    private DocumentNodeResource doc;
    /** owner document's content's DOM */
    private Document contentDom;
    /** owner document's metadata's DOM */
    private Document metaDom;
    /** owner document's keywords  */
    private List keywords;
    /** serialized document pages */
    private String[] pages;

    public DocumentRenderingHelper(SiteService siteService, StructureService structureService, 
        HTMLService htmlService, DocumentNodeResource doc,
    	LinkRenderer linkRenderer, HTMLContentFilter filter)
        throws ProcessingException
    {
        this.siteService = siteService;
        this.structureService = structureService;
        this.doc = doc;
        this.htmlService = htmlService; 
        try
        {
        	// get HTML DOM and filter it
            contentDom = filter.filter(htmlService.parseHTML(doc.getContent()));
            // WARN: replace URIs
			// replace internal links
			replaceAnchorURIs(contentDom, linkRenderer);
			// replace image sources
			replaceImageURIs(contentDom, linkRenderer);
        }
        catch(HTMLException e)
        {
            contentDom = HTMLUtil.emptyHtmlDom();
        }
    }

    // public interface ///////////////////////////////////////////////////////

    public DocumentNodeResource getDocument()
    {
        return doc;
    }

    // content /////////////////////////////////////////////////////////////////////////////////////

    public String getContent()
    throws DocumentException, HTMLException
    {
        // compose from pages
        int numPages = getNumPages();
        int length = 0;
        for(int i=1; i <= numPages; i++)
        {
            length += getPageContent(i).length();
        }
        StringBuffer buf = new StringBuffer(length);
        for(int i=1; i <= numPages; i++)
        {
            if(i > 1)
            {
                buf.append("<hr class='page-break' />");
            }
            buf.append(getPageContent(i));
        }
        return buf.toString();
    }

    public int getNumPages()
    {
        if(pages == null)
        {
            Element srcBody = getContentDom().getRootElement().element("body");

            int numPages = 1;
            for(Iterator i=srcBody.nodeIterator(); i.hasNext();)
            {
                Node n = (Node)(i.next());
                // match page break
                if(isPageBreak(n))
                {
                    numPages++;
                }
            }
            pages = new String[numPages];
        }
        return pages.length;
    }

    public String getPageContent(int i)
    throws DocumentException, HTMLException
    {
        // i is a page number => i belongsto [1..numPages]
        if(i > 0 && i <= getNumPages())
        {
            int index = i - 1;
            if(pages[index] == null)
            {
                pages[index] = serialize(getPageDom(i));
            }
            return pages[index];
        }
        else
        {
            // TODO: What to do??
            return "";
        }
    }

    // meta ////////////////////////////////////////////////////////////////////////////////////////

    public List getKeywords()
    {
        if(keywords == null)
        {
            ArrayList keywords = new ArrayList(10);
            String value = doc.getKeywords();
            if(value != null && value.length() > 0)
            {
                StringTokenizer tokenizer = new StringTokenizer(value, ",");

                while(tokenizer.hasMoreTokens())
                {
                    keywords.add(tokenizer.nextToken());
                }
            }
        }
        return keywords;
    }

    // document DOM access methods ////////////////////////////////////////////////////////////

    public Document getContentDom()
    {
        return contentDom;
    }

    public Document getPageDom(int pageNum)
    {
        Element srcBody = getContentDom().getRootElement().element("body");

        Document destDocument = HTMLUtil.emptyHtmlDom();
        Element destBody = destDocument.getRootElement().element("body");

        int currentPage = 1;
        for(Iterator i=srcBody.nodeIterator(); i.hasNext();)
        {
            Node n = (Node)(i.next());
            // match page break
            if(isPageBreak(n))
            {
                currentPage++;
                if(currentPage > pageNum) // stop processing after required page
                {
                    break;
                }
            }
            else if(currentPage == pageNum)
            {
                Node newN = (Node)(n.clone());
                newN.detach();
                destBody.add(newN);
            }
        }
        return destDocument;
    }

    public Document getMetaDom()
    throws DocumentException
    {
        if(metaDom == null)
        {
            String meta = doc.getMeta();
            if(meta != null && meta.length() > 0)
            {
                meta = entityDecoder.decodeXML(meta);
                metaDom = HTMLUtil.parseXmlAttribute(meta, "meta");
            }
        }
        return metaDom;
    }

    // utility methods /////////////////////////////////////////////////////////////////////////////

    private boolean isPageBreak(Node n)
    {
        if(n instanceof Element)
        {
            Element e = (Element)n;
            if(!e.getName().equals("hr"))
            {
                return false;
            }
            if(e.attribute("class") == null)
            {
                return false;
            }
            String value = e.attribute("class").getValue();
            if(value != null && value.equals("page-break"))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    private String serialize(Document dom)
    throws DocumentException, HTMLException
    {
        String domHtml = "";
        if(dom != null)
        {
            domHtml = htmlService.serializeHTML(dom);
        }
        return domHtml;
    }

    // URI modification methods ////////////////////////////////////////////////////////////////////

    /**
     * This method replaces URIs defined in source document content to ones that are supported
     * by the browsers.
     * Supported URIs are:
     * <ul>
     * <li>Internal CMS links
     *   <ul>
     *   <li><code>cms:siteName/sitepath#fragment</code></li>
     *   <li><code>cms:siteName/sitepath#</code></li>
     *   <li><code>cms:siteName/sitepath</code></li>
     *   </ul>
     * </li>
     * <li>Internal document links
     *   <ul>
     *   <li><code>htmlarea:#fragment</code></li>
     *   </ul>
     * </li>
     * <li>External links
     *   <ul>
     *   <li>any other link - <code>http</code> or <code>ftp</code>, etc.</li>
     *   </ul>
     * </li>
     * </ul>
     *
     */
    private void replaceAnchorURIs(Document dom4jDoc, LinkRenderer linkRenderer)
    {
        // replace uris
        List anchors = dom4jDoc.selectNodes("//a");
        for(Iterator i=anchors.iterator(); i.hasNext();)
        {
            Element element = (Element)(i.next());
            Attribute attribute = element.attribute("href");

            // go further if this anchor is not a link
            if(attribute == null)
            {
                continue;
            }

            try
            {
                URI uri = new URI(attribute.getValue());

                String linkClassName = null;
                // in CMS link
                if(uri.getScheme().equals("cms"))
                {
                    linkClassName = "cms-lnk";

                    String wholePath = uri.getSchemeSpecificPart();
                    String fragment = uri.getFragment();
                    int breakIndex = wholePath.indexOf('/');
                    int breakIndex2 = (fragment != null)?
                                    wholePath.length() -fragment.length() -1 //-1 for # character
                                    :wholePath.length();

                    String siteName = wholePath.substring(0, breakIndex);
                    String pagePath = wholePath.substring(breakIndex+1, breakIndex2);

                    //1. get site
                    SiteResource site = siteService.getSite(siteName);
                    if(site != null)
                    {
                        //2. get linked node
                        NavigationNodeResource homepage = structureService.getRootNode(site);
                        Resource parent = homepage.getParent();
                        Resource[] temp = resourceService.getStore().getResourceByPath(
                                                                    parent.getPath()+'/'+pagePath);
                        if(temp.length == 1)
                        {
                            // set a virtual for this link
                            StringBuffer newUri = new StringBuffer(
                            	linkRenderer.getNodeURL((NavigationNodeResource)(temp[0])));
                            if(fragment != null)
                            {
                            	// TODO Add support for finding the document page
                            	//		to which a fragment leads
								newUri.append('#');
                                newUri.append(fragment);
                            }
                            attribute.setValue(newUri.toString());
                        }
                        else if(temp.length == 0)
                        {
                            // TODO: Report broken link
                            throw new DocumentException(
                                "Cannot find a page with this path - cannot link ");
                        }
                        else
                        {
                            throw new DocumentException(
                                "Multiple pages with the same path - cannot link ");
                        }
                    }
                }
                // in document link
                else if(uri.getScheme().equals("htmlarea"))
                {
                    linkClassName = "doc-lnk";

                    String wholePath = uri.getSchemeSpecificPart();
                    attribute.setValue(wholePath);
                }
                else if(uri.getScheme().equals("mailto"))
                {
                    linkClassName = "eml-lnk";
                }
                else // must be external link
                {
                    linkClassName = "ext-lnk";
                }

                if(linkClassName != null)
                {
                    Attribute classAttr = element.attribute("class");
                    if(classAttr == null)
                    {
                        element.addAttribute("class", linkClassName);
                    }
                    else
                    {
                        classAttr.setValue(classAttr.getValue()+" "+linkClassName);
                    }
                }
            }
            catch(Exception e)
            {
                // TODO: must be a broken link - replace it, and log it (?)
                attribute.setValue("javascript:alert('Problem in link generation occured:\\n"+
                                   e.getMessage()+"');");
            }
        }
    }

    public static void replaceImageURIs(CoralSession coralSession, Document dom4jDoc, LinkRenderer linkRenderer)
    {
        List images = dom4jDoc.selectNodes("//img");
        for(Iterator i=images.iterator(); i.hasNext();)
        {
            Element element = (Element)(i.next());
            Attribute attribute = element.attribute("src");

            boolean brokenImage = false;

            if(attribute == null)
            {
                brokenImage = true;
            }
            else
            {
                try
                {
                    URI uri = new URI(attribute.getValue());
                    String imageHost = uri.getHost();
                    if(siteService.isVirtualServer(coralSession, imageHost))
                    {
                        // we have an internal image
                        String restOfImageUri = uri.getPath(true, true);
                        attribute.setValue(restOfImageUri);
                    }
                }
                catch(MalformedURIException e)
                {
                    brokenImage = true;
                }
                catch(SiteException e)
                {
                    brokenImage = true;
                }
            }

            if(brokenImage)
            {
				String value = linkRenderer.getCommonResourceURL(null, "images/no_image.png");
                if(attribute == null)
                {
                    element.addAttribute("src", value);
                }
                else
                {
                    attribute.setValue(value);
                }
            }
        }
    }
}
