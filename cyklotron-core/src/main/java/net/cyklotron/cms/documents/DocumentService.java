package net.cyklotron.cms.documents;

import org.dom4j.Document;
import org.objectledge.coral.event.ResourceChangeListener;
import org.objectledge.coral.store.Resource;

import pl.caltha.forms.Form;

/** DocumentService is used to operate on CMS documents.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: DocumentService.java,v 1.2 2005-01-19 08:22:23 pablo Exp $
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