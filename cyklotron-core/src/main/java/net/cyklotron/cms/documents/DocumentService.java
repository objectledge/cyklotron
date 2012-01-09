package net.cyklotron.cms.documents;

import org.dom4j.Document;
import org.objectledge.coral.event.ResourceChangeListener;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.Resource;
import org.objectledge.forms.Form;
import org.objectledge.html.HTMLContentFilter;

import net.cyklotron.cms.site.SiteResource;

/** DocumentService is used to operate on CMS documents.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: DocumentService.java,v 1.4 2007-11-18 21:23:07 rafal Exp $
 */
public interface DocumentService
    extends ResourceChangeListener
{
    /** The name of the service (<code>document</code>). */
    public static final String SERVICE_NAME = "document";

    /** The logging facility where the service issues it's informational
     * messages. */
    public static final String LOGGING_FACILITY = "document";

    /** Document Edit form name. */
    public static final String FORM_NAME = "cyklotron.cms.document.edit.form";

    /** Returns a form tool service's form definition object for editing CMS documents. */
    public Form getDocumentEditForm();

    /** Copies the contents of a dom4j Document object
     * into a given DocumentNodeResource. */
    public void copyToDocumentNode(Resource doc, Document srcDoc)
    throws DocumentException;

    /** Copies the contents of a DocumentNodeResource
     * into a given dom4j Document object. */
    public void copyFromDocumentNode(Resource doc, Document destDoc)
        throws DocumentException;

    /**
     * Returns the root node of documents application.
     * <p>
     * This is the currently /cms/sites/<i>site</i>/applications/documents.
     * </p>
     * <p>
     * Note that the site's documents reside under /cms/sites/<i>site</i>/structure node
     * </p>
     * 
     * @param coralSession coral session.
     * @param site the site.
     * @return root node of documents application.
     */
    public Resource getDocumentsApplicationRoot(CoralSession coralSession, SiteResource site);

    /**
     * Returns the parent node of keyword resources.
     * 
     * @param coralSession coral session.
     * @param site the site.
     * @return parent node of keyword resources.
     */
    public Resource getKeywordsRoot(CoralSession coralSession, SiteResource site);

    /**
     * Returns the parent node of footer resources.
     * 
     * @param coralSession coral session.
     * @param site the site.
     * @return parent node of footer resources.
     */
    public Resource getFootersRoot(CoralSession coralSession, SiteResource site);

    /**
     * Returns contents of the selected footer.
     * 
     * @param coralSession coral session.
     * @param site the site.
     * @param name footer resource name.
     * @return contents of the selected footer, or empty string if no such footer exists, or the
     *         selected footer is disabled.
     * @throws InvalidResourceNameException
     */
    public String getFooterContent(CoralSession coralSession, SiteResource site, String name);

    /**
     * Returns HTMLContentFilter applicable to the specified document.
     * 
     * @param doc the document.
     * @param linkRenderer a LinkRenderer instance.
     * @param coralSession Coral session.
     * @return HTMLContentFilter insntance.
     */
    public HTMLContentFilter getContentFilter(DocumentNodeResource doc, LinkRenderer linkRenderer,
        CoralSession coralSession);

/*    public long[] getEmbededImageResourceIds(DocumentNodeResource doc)
    throws DocumentException;

    public long[] getLinkedDocumentResourceIds(DocumentNodeResource doc)
    throws DocumentException;

    public void replaceEmbeddedImageResourceIds(DocumentNodeResource doc, long[] srcIds, long destId)
    throws DocumentException;

    public void replaceLinkedDocumentResourceIds(DocumentNodeResource doc, long[] srcIds, long destId)
    throws DocumentException;

    public void removeEmbeddedImageResources(DocumentNodeResource doc, long[] ids)
    throws DocumentException;

    public void removeLinkedDocumentResources(DocumentNodeResource doc, long[] ids)
    throws DocumentException;

    public void addRelatedDocumentResources(DocumentNodeResource doc, long[] relatedDocIds)
    throws DocumentException;

    public void removeRelatedDocumentResources(DocumentNodeResource doc, long[] relatedDocIds)
    throws DocumentException;
*/    
}