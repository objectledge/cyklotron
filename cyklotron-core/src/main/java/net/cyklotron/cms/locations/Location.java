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

/**
 * A location descriptor.
 * <p>
 * Locations are equal in the sense of {@link Object#equals(Object)} and {@link Object#hashCode()}
 * if their post codes are equal.
 * </p>
 */
public class Location
{
    private String province;

    private String district;

    private String commune;

    private String city;

    private String area;

    private String street;

    private String postCode;

    public Location(String province, String district, String commune, String city, String area,
        String street, String postCode)
    {
        this.province = province;
        this.district = district;
        this.commune = commune;
        this.city = city;
        this.area = area;
        this.street = street;
        this.postCode = postCode;
    }

    public String getProvince()
    {
        return province;
    }

    public String getCity()
    {
        return city;
    }

    public String getStreet()
    {
        return street;
    }

    public String getPostCode()
    {
        return postCode;
    }

    public int hashCode()
    {
        return postCode.hashCode();
    }

    public boolean equals(Object obj)
    {
        if(obj != null && obj instanceof Location)
        {
            return postCode.equals(((Location)obj).postCode);
        }
        return false;
    }

    public String getDistrict()
    {
        return district;
    }

    public String getCommune()
    {
        return commune;
    }

    public String getArea()
    {
        return area;
    }

}
