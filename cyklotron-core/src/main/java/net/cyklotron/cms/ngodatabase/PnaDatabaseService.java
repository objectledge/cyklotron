package net.cyklotron.cms.ngodatabase;

import org.dom4j.Document;
import org.objectledge.coral.event.ResourceChangeListener;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.Resource;
import org.objectledge.forms.Form;


import net.cyklotron.cms.site.SiteResource;

/** DocumentService is used to operate on CMS documents.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: DocumentService.java,v 1.4 2007-11-18 21:23:07 rafal Exp $
 */
public interface PnaDatabaseService 
{
    /** The name of the service (<code>document</code>). */
    public static final String SERVICE_NAME = "pna_database";

    /** The logging facility where the service issues it's informational
     * messages. */
    public static final String LOGGING_FACILITY = "pna_database";
    
    /**
     * update ogranizations from metadata.
     */
    public void update();
    
    /**
     * Download medatata.
     */
    public void downloadDataSource();
}