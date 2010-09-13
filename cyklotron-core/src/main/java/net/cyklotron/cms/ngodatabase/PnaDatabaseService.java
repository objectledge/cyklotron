package net.cyklotron.cms.ngodatabase;

import java.io.IOException;
import java.util.Set;

/**
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: DocumentService.java,v 1.4 2007-11-18 21:23:07 rafal Exp $
 */
public interface PnaDatabaseService 
{
    /**
     * update Pna data from source.
     */
    public void update();
    
    /**
     * download Pna source.
     */
    public void downloadSource() 
    throws IOException;
    
    /**
     * get Pna set by Post Code
     */
    public Set<Pna> getPnaSetByPostCode(String postCode);
    
    /**
     * get Pna set by City
     */
    public Set<Pna> getPnaSetByCity(String city);
    
    /**
     * get Pna set by Area
     */
    public Set<Pna> getPnaSetByProvince(String area);
}