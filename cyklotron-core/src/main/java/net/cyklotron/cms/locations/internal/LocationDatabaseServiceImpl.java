// 
// Copyright (c) 2004, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
// 
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
// 
// * Redistributions of source code must retain the above copyright notice,  
//       this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//       this list of conditions and the following disclaimer in the documentation  
//       and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//       nor the names of its contributors may be used to endorse or promote products  
//       derived from this software without specific prior written permission. 
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
// IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,  
// INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  
// BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
// OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  
// WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  
// POSSIBILITY OF SUCH DAMAGE. 
// 

package net.cyklotron.cms.locations.internal;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.filesystem.FileSystem;
import org.picocontainer.Startable;

import net.cyklotron.cms.locations.Location;
import net.cyklotron.cms.locations.LocationDatabaseService;
import net.cyklotron.cms.locations.LocationsProvider;

public class LocationDatabaseServiceImpl
    implements LocationDatabaseService, Startable
{
    private final LocationsProvider locationsProvider;

    private final LocationsIndex index;

    private final Logger logger;

    public LocationDatabaseServiceImpl(LocationsProvider pnaProvider, FileSystem fileSystem,
        Logger logger)
        throws IOException
    {
        this.locationsProvider = pnaProvider;
        this.logger = logger;
        this.index = new LocationsIndex(fileSystem, logger);
    }

    @Override
    public void start()
    {
        try
        {
            if(index.isEmpty())
            {
                load(locationsProvider.fromCache());
            }            
        }
        catch(IOException e)
        {
            logger.error("failed to rebuild location index");
        }
    }

    @Override
    public void stop()
    {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update()
    {
        try
        {
            load(locationsProvider.fromSource());
        }
        catch(IOException e)
        {
            logger.error("failed to rebuild location index");
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<Location> getLocations(String requestedField, String province, String district,
        String commune, String city, String area, String street, String postCode, String terc, String sym)
    {
        return index.getLocations(requestedField, province, district, commune, city, area, street,
            postCode);
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getAllTerms(String field)
    {
        return index.getAllTerms(field);
    }

    /**
     * {@inheritDoc}
     */
    public boolean exactMatchExists(String field, String value)
        throws IOException
    {
        return index.exactMatchExists(field, value);
    }

    private void load(Collection<Location> locations)
        throws IOException
    {
        index.startUpdate();
        for(Location location : locations)
        {
            index.addItem(location);
        }
        index.endUpdate();
    }
}
