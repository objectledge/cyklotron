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

package net.cyklotron.cms.organizations;

public class Organization
{
    private Long id;

    private String name;

    private String province;

    private String county;

    private String city;

    private String street;

    private String postCode;

    private String tel;

    private String fax;

    private String url;

    private String email;

    private String nip;

    private String regon;

    private String krs;

    public Organization(long id, String name, String province, String county, String city,
        String street, String postCode, String tel, String fax, String url, String email,
        String nip, String regon, String krs)
    {
        this.id = id;
        this.name = name;
        this.province = province;
        this.county = county;
        this.city = city;
        this.street = street;
        this.postCode = postCode;
        this.tel = tel;
        this.fax = fax;
        this.url = url;
        this.email = email;
        this.nip = nip;
        this.regon = regon;
        this.krs = krs;
    }

    public Organization(long id, String name, String province, String county, String city,
        String street, String postCode)
    {
        this(id, name, province, county, city, street, postCode, "", "", "", "", "", "", "");
    }

    public Long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getProvince()
    {
        return province;
    }

    public void setProvince(String province)
    {
        this.province = province;
    }

    public String getCounty()
    {
        return county;
    }

    public void setCounty(String county)
    {
        this.county = county;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getStreet()
    {
        return street;
    }

    public void setStreet(String street)
    {
        this.street = street;
    }

    public String getPostCode()
    {
        return postCode;
    }

    public void setPostCode(String postCode)
    {
        this.postCode = postCode;
    }

    public String getTel()
    {
        return tel;
    }

    public void setTel(String tel)
    {
        this.tel = tel;
    }

    public String getFax()
    {
        return fax;
    }

    public void setFax(String fax)
    {
        this.fax = fax;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getNip()
    {
        return nip;
    }

    public void setNip(String nip)
    {
        this.nip = nip;
    }

    public String getRegon()
    {
        return regon;
    }

    public void setRegon(String regon)
    {
        this.regon = regon;
    }

    public String getKrs()
    {
        return krs;
    }

    public void setKrs(String krs)
    {
        this.krs = krs;
    }
}
