package net.cyklotron.cms.documents;

import net.labeo.services.Service;

import org.dom4j.Document;

/** DocumentService is used to operate on CMS documents.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: HTMLService.java,v 1.1 2005-01-12 20:44:39 pablo Exp $
 */
public interface HTMLService
	extends Service
{
	public static final String SERVICE_NAME = "html";

	public static final String LOGGING_FACILITY = "html";

	public String encodeHTML(String html, String encodingName);

	public String htmlToText(String html)
	throws HTMLException;

    public Document parseHTML(String html)
	throws HTMLException;
    
    public String serializeHTML(Document dom4jDoc)
	throws HTMLException;
}
