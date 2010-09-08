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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Coral Maven plugin
 */

public class PoolPna
{
    /** The name of the Coral resource class. */
    public static final String CLASS_NAME = "cms.ngodatabase.pnas";

    private PnaCollection areas;

    private PnaCollection cities;

    private PnaCollection postCodes;

    public PoolPna()
    {
        areas = new PnaCollection();
        cities = new PnaCollection();
        postCodes = new PnaCollection();
    }

    public void AddPna(Pna pna)
    {
        this.areas.addPna(pna.getArea(), pna);
        this.cities.addPna(pna.getCity(), pna);
        this.postCodes.addPna(pna.getPostCode(), pna);
    }

    public void AddPna(String area, String city, String street, String postCode)
    {
        Pna pna = new Pna(area, city, street, postCode);
        this.areas.addPna(area, pna);
        this.cities.addPna(city, pna);
        this.postCodes.addPna(postCode, pna);
    }

    public Set<Pna> getPnaSetByPostCode(String postCode)
    {
        return postCodes.getPnaSet(postCode);
    }

    public Set<Pna> getPnaSetByCity(String city)
    {
        return cities.getPnaSet(city);
    }

    public Set<Pna> getPnaSetByArea(String area)
    {
        return areas.getPnaSet(area);
    }

    private class PnaCollection
    {
        private Map<String, Set<Pna>> pnaCollection;

        public PnaCollection()
        {
            pnaCollection = new HashMap<String, Set<Pna>>();
        }

        public void addPna(String key, Pna pna)
        {
            if(!pnaCollection.containsKey(key))
            {
                pnaCollection.get(key).add(pna);
            }
            else
            {
                pnaCollection.put(key, new HashSet<Pna>(Arrays.asList(pna)));
            }
        }

        public Set<Pna> getPnaSet(String key)
        {
            return pnaCollection.get(key);
        }
    }

}
