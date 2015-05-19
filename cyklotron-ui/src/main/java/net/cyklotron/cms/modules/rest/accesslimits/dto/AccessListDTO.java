package net.cyklotron.cms.modules.rest.accesslimits.dto;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.objectledge.coral.store.Resource;

import net.cyklotron.cms.accesslimits.AccessListResource;

public class AccessListDTO
{
    private Long id;

    private String name;

    private String description;

    private List<AccessListItemDTO> items;
    
    public AccessListDTO()
    {        
    }

    public AccessListDTO(AccessListResource resource, boolean includeItems)
        throws UnknownHostException
    {
        this.id = resource.getIdObject();
        this.name = resource.getName();
        this.description = resource.getDescription();
        if(includeItems)
        {
            this.items = AccessListItemDTO.create(resource);
        }
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

    public List<AccessListItemDTO> getItems()
    {
        return items;
    }

    public void setItems(List<AccessListItemDTO> items)
    {
        this.items = items;
    }

    public static List<AccessListDTO> create(Resource[] resources)
    {
        try
        {
            List<AccessListDTO> results = new ArrayList<>();
            for(Resource resource : resources)
            {
                if(resource instanceof AccessListResource)
                {
                    results.add(new AccessListDTO((AccessListResource)resource, false));
                }
            }
            return results;
        }
        catch(UnknownHostException e)
        {
            // AccessListDTO is invoked with includeItems = false, so CIDRBlocks are not processed
            throw new RuntimeException("unexpected", e);
        }
    }
}
