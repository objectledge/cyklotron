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

package net.cyklotron.cms.ngodatabase;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.picocontainer.Startable;

public class LocationDatabaseServiceImpl
    implements LocationDatabaseService, Startable
{  
    private final LocationsProvider provider;

    private final Set<Location> allLocations = new HashSet<Location>();
    
    private final Map<String, Set<Location>> locationsByProvince = new HashMap<String, Set<Location>>();

    private final Map<String, Set<Location>> locationsByCity = new HashMap<String, Set<Location>>();

    private final Map<String, Set<Location>> locationsByPostCode = new HashMap<String, Set<Location>>();

    public LocationDatabaseServiceImpl(LocationsProvider provider)
    {
        this.provider = provider;
    }

    @Override
    public void start()
    {
        load(provider.fromCache());
    }

    @Override
    public void stop()
    {
    
    }

    @Override
    public void update()
    {
        load(provider.fromSource());
    }
    
    @Override
    public Set<Location> getAllLocations()
    {
        return allLocations;
    }

    @Override
    public Set<String> getPostCodes()
    {
        return locationsByPostCode.keySet();
    }
    
    @Override
    public Set<String> getPostCodes(String substring)
    {
        return getMachingSubset(locationsByPostCode.keySet(), substring);
    }

    @Override
    public Set<Location> getLocationsByPostCode(String postCode)
    {
        return locationsByPostCode.get(postCode);
    }
    
    @Override
    public boolean containsPostCode(String postCode)
    {
        return locationsByPostCode.containsKey(postCode);
    }

    @Override
    public Set<String> getCities()
    {      
        return locationsByCity.keySet();
    }
    
    @Override
    public Set<String> getCities(String substring)
    {
        return getMachingSubset(locationsByCity.keySet(), substring);
    }

    @Override
    public Set<Location> getLocationsByCity(String city)
    {
        return locationsByCity.get(city);
    }
    
    @Override
    public boolean containsCity(String city)
    {
        return locationsByCity.containsKey(city);
    }

    @Override
    public Set<String> getProvinces()
    {
        return locationsByProvince.keySet();
    }
    
    @Override
    public Set<String> getProvinces(String substring)
    {
        return getMachingSubset(locationsByProvince.keySet(), substring);
    }

    @Override
    public Set<Location> getLocationsByProvince(String province)
    {
        return locationsByProvince.get(province);
    }
    
    @Override
    public boolean containsProvince(String province)
    {
        return locationsByProvince.containsKey(province);
    }

    @Override
    public Set<Location> getLocationByQuery(String postCode, String city, String province)
    {
        Set<Location> cities = locationsByCity.get(city);
        Set<Location> provinces = locationsByProvince.get(province);
        Set<Location> postCodes = locationsByPostCode.get(postCode);
        Set<Location> results = new HashSet<Location>();

        if(cities != null)
        {
            results = cities;
            if(provinces != null)
            {
                results.retainAll(provinces);
            }
            if(postCodes != null)
            {
                results.retainAll(postCodes);
            }
        }
        else if(provinces != null)
        {
            results = provinces;
            if(cities != null)
            {
                results.retainAll(cities);
            }
            if(postCodes != null)
            {
                results.retainAll(postCodes);
            }
        }
        else if(postCodes != null)
        {
            results = postCodes;
            if(cities != null)
            {
                results.retainAll(cities);
            }
            if(provinces != null)
            {
                results.retainAll(provinces);
            }
        }
        return results;
    }

    private void load(Collection<Location> locations)
    {
        allLocations.clear();
        locationsByProvince.clear();
        locationsByCity.clear();
        locationsByPostCode.clear();
        for(Location location : locations)
        {
            allLocations.add(location);
            add(location, location.getProvince(), locationsByProvince);
            add(location, location.getCity(), locationsByCity);
            add(location, location.getPostCode(), locationsByPostCode);
        }
    }

    private void add(Location item, String key, Map<String, Set<Location>> map)
    {
        Set<Location> set = map.get(key);
        if(set == null)
        {
            set = new HashSet<Location>();
            map.put(key, set);
        }
        set.add(item);
    }
    
    private Set<String> getMachingSubset(Set<String> keys, String substring)
    {
        Set<String> machingSubset = new HashSet<String>();
        for(String key : keys)
        {
            if(key.toLowerCase().contains(substring.toLowerCase()))
            {
                machingSubset.add(key);
            }
        }
        return machingSubset;
    }
}
