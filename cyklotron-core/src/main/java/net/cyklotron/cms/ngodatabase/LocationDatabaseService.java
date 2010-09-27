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
     * Get post codes matches substring.
     */
    public Set<String> getPostCodes(String substring);
    
    /**
     * get Location set by post code
     */
    public Set<Location> getLocationsByPostCode(String postCode);
    
    /**
     * check if Location set contains post code
     */
    public boolean containsPostCode(String postCode);
    
    /**
     * Get all defined city names.
     */
    public Set<String> getCities();
    
    /**
     * Get city names matches substring.
     */
    public Set<String> getCities(String substring);
    
    /**
     * get Location set by city name
     */
    public Set<Location> getLocationsByCity(String city);
    
    /**
     * check if Location set contains city name
     */
    public boolean containsCity(String city);
    
    /**
     * Get all defined province names.
     */
    public Set<String> getProvinces();
    
    /**
     * Get province names matches substring.
     */
    public Set<String> getProvinces(String substring);
    
    /**
     * get Location set by province name
     */
    public Set<Location> getLocationsByProvince(String area);
    
    /**
     * check if Location set contains province
     */
    public boolean containsProvince(String province);
    
    /**
     * get Location subset with postCode, city, province
     */
    public Set<Location> getLocationByQuery(String postCode, String city, String province);
}