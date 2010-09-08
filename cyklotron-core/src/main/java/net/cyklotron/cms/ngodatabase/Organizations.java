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

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectledge.coral.store.Resource;

import net.cyklotron.cms.CmsNodeResource;
import net.cyklotron.cms.ProtectedResource;
import net.cyklotron.cms.search.IndexableResource;
import net.cyklotron.cms.workflow.StatefulResource;

/**
 * @author Coral Maven plugin
 */
public class Organizations
{
    // constants /////////////////////////////////////////////////////////////

    /** The name of the Coral resource class. */    
    public static final String CLASS_NAME = "cms.ngodatabase.organizations";
    
    private Map<Long,Organization> organizations; 
    private Map<Long,String> organizationsNames; 
    
    public Organizations()
    {
        organizations = new HashMap();
        organizationsNames = new HashMap();
    }
    
    public Organization getOrganization(Long id)
    {
        return organizations.get(id);
    }
    
    public void addOrganization(Organization organization)
    {
        if(!organizations.containsKey(organization.getId()))
        {
            organizations.put(organization.getId(), organization);
            organizationsNames.put(organization.getId(), organization.getName());
        }
    }

    public Organization[] getOrganizations()
    {
        return organizations.values().toArray(new Organization[organizations.size()]);
    }

    public Organization[] getOrganizations(String substring)
    {
        if(substring.isEmpty())
        {
            return getOrganizations();
        }
        else
        {
            Set<Organization> maches = new HashSet<Organization>();
            for(Organization org : organizations.values())
            {
                if(org.matches(substring))
                {
                    maches.add(org);
                }
            }
            return maches.toArray(new Organization[maches.size()]);
        }
    }
    
    public String[] getOrganizationsNames()
    {
        return organizationsNames.values().toArray(new String[organizationsNames.size()]);
    }
    
    /*
     *  return string array with names of organizations;
     */
    public String[] getOrganizationsNames(String substring)
    {
        if(substring.isEmpty())
        {
            return getOrganizationsNames();
        }
        else
        {   
            Set<String> maches = new HashSet<String>();
            for(String orgName : organizationsNames.values())
            {
                if(orgName.matches(substring))
                {
                    maches.add(orgName);
                }
            }
            return maches.toArray(new String[maches.size()]);
        }
    }   
}