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

package net.cyklotron.cms.locations;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * A location descriptor.
 * <p>
 * Locations are equal in the sense of {@link Object#equals(Object)} and {@link Object#hashCode()}
 * if their post codes are equal.
 * </p>
 */
public class Location
    implements Iterable<Map.Entry<String, String>>
{
    private final Map<String, String> entries;

    private final String pkField;

    private final String[] fields;

    public Location(String[] fields, Map<String, String> entries)
    {
        this.fields = fields;
        this.entries = entries;
        this.pkField = fields.length > 0 ? entries.get(fields[0]) : "";
    }

    public Location(Location location1, Location location2)
    {
        this.entries = getMatchingEntries(location1, location2);
        this.fields = new String[entries.size()];
        this.entries.keySet().toArray(this.fields);
        this.pkField = fields.length > 0 ? entries.get(fields[0]) : "";
    }

    public String get(String field)
    {
        return entries.get(field);
    }
    
    public Map<String, String> getEntries()
    {
        return entries;
    }

    public Iterator<Map.Entry<String, String>> iterator()
    {
        return new Iterator<Map.Entry<String, String>>()
            {
                private final Iterator<Map.Entry<String, String>> i = entries.entrySet().iterator();

                @Override
                public boolean hasNext()
                {
                    return i.hasNext();
                }

                @Override
                public Entry<String, String> next()
                {
                    return i.next();
                }

                @Override
                public void remove()
                {
                    throw new UnsupportedOperationException();
                }
            };
    }

    @Override
    public int hashCode()
    {
        return pkField.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof Location && ((Location)obj).pkField.equals(pkField);
    }

    @Override
    public String toString()
    {
        StringBuilder buff = new StringBuilder();
        for(int i = 1; i <= fields.length; i++)
        {
            buff.append(entries.get(fields[fields.length - i]));
            if(i < fields.length)
            {
                buff.append(" ");
            }
        }
        return buff.toString();
    }

    /**
     * Return non empty matching fields map
     * 
     * @param location1 Location class
     * @param location2 Location class
     * @return <code>Map<String, String></code>
     * @author lukasz
     */
    private Map<String, String> getMatchingEntries(Location location1, Location location2)
    {
        Map<String, String> matching = new HashMap<String, String>();
        if(location1 != null)
        {
            Iterator i = location1.iterator();
            if(location2 == null)
            {
                while(i.hasNext())
                {
                    Entry<String, String> e = (Entry)i.next();
                    if(e.getValue() != null)
                    {
                        matching.put(e.getKey(), e.getValue());
                    }
                }
            }
            else
            {
                while(i.hasNext())
                {
                    Entry<String, String> e = (Entry)i.next();
                    if(e.getValue() != null && e.getValue().equals(location2.get(e.getKey())))
                    {
                        matching.put(e.getKey(), e.getValue());
                    }
                }
            }
        }
        return matching;
    }
}
