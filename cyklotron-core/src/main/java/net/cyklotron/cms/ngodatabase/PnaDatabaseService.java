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
    /**
     * update ogranizations from metadata.
     */
    public void update();
    
    /**
     * Download medatata.
     */
    public void downloadDataSource();
}