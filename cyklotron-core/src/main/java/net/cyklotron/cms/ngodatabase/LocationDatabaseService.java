package net.cyklotron.cms.ngodatabase;

import java.io.IOException;
import java.util.Set;

/**
 * Provides access to a collection of location descriptors.
 * 
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: DocumentService.java,v 1.4 2007-11-18 21:23:07 rafal Exp $
 */
public interface LocationDatabaseService 
{
    /**
     * Update Locations data from source.
     */
    public void update();
    
    /**
     * Get all defined locations.
     * @return
     */
    public Set<Location> getAllLocations();
    
    /**
     * Get all defined post codes
     */
    public Set<String> getPostCodes();
    
    /**
     * get Location set by post code
     */
    public Set<Location> getLocationsByPostCode(String postCode);
    
    /**
     * Get all defined city names.
     */
    public Set<String> getCities();
    
    /**
     * get Location set by city name
     */
    public Set<Location> getLocationsByCity(String city);
    
    /**
     * Get all defined province names.
     */
    public Set<String> getProvinces();
    
    /**
     * get Location set by province name
     */
    public Set<Location> getLocationsByProvince(String area);
}