package net.cyklotron.cms.ngodatabase;

import java.util.Collection;

public interface LocationsProvider
{   
    /**
     * Return all locations in the database, updated from source.
     * 
     * @return TODO
     */
    public Collection<Location> fromSource(); 
    
    /**
     * Return all locations in the database.
     *  
     * @return
     */
    public Collection<Location> fromCache();
}
