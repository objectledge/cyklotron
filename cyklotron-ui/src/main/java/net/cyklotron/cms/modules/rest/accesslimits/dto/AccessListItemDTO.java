package net.cyklotron.cms.modules.rest.accesslimits.dto;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.objectledge.coral.store.Resource;
import org.objectledge.net.CIDRBlock;

import net.cyklotron.cms.accesslimits.AccessList;
import net.cyklotron.cms.accesslimits.AccessListItemResource;
import net.cyklotron.cms.accesslimits.AccessListResource;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class AccessListItemDTO
{
    private Long id;

    private String name;

    private String description;

    private Date creationTime;

    private String addressBlock;

    private CIDRBlock cidrBlock;
    
    public AccessListItemDTO()
    {        
    }

    public AccessListItemDTO(AccessListItemResource resource)
        throws UnknownHostException
    {
        this.id = resource.getIdObject();
        this.name = resource.getName();
        this.description = resource.getDescription();
        this.creationTime = resource.getCreationTime();
        this.addressBlock = resource.getAddressBlock();
        this.cidrBlock = AccessList.parse(addressBlock);
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Date getCreationTime()
    {
        return creationTime;
    }

    public void setCreationTime(Date creationTime)
    {
        this.creationTime = creationTime;
    }

    public String getAddressBlock()
    {
        return addressBlock;
    }

    public void setAddressBlock(String addressBlock)
    {
        this.addressBlock = addressBlock;
    }

    @JsonIgnore
    public CIDRBlock getCidrBlock()
    {
        return cidrBlock;
    }

    @JsonIgnore
    public void setCidrBlock(CIDRBlock cidrBlock)
    {
        this.cidrBlock = cidrBlock;
    }

    public static List<AccessListItemDTO> create(AccessListResource list)
        throws UnknownHostException
    {
        List<AccessListItemDTO> result = new ArrayList<>();
        for(Resource resource : list.getChildren())
        {
            if(resource instanceof AccessListItemResource)
            {
                result.add(new AccessListItemDTO((AccessListItemResource)resource));
            }
        }
        return result;
    }

    public static final Comparator<AccessListItemDTO> BY_CREATION_TIME = new Comparator<AccessListItemDTO>()
        {
            @Override
            public int compare(AccessListItemDTO item1, AccessListItemDTO item2)
            {
                return item1.creationTime.compareTo(item2.creationTime);
            }
        };

    public static final Comparator<AccessListItemDTO> BY_ADDRESS_BLOCK = new Comparator<AccessListItemDTO>()
        {
            @Override
            public int compare(AccessListItemDTO item1, AccessListItemDTO item2)
            {
                return item1.addressBlock.compareTo(item2.addressBlock);
            }
        };
}
