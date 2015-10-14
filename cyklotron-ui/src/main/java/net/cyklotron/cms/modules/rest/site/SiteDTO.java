package net.cyklotron.cms.modules.rest.site;

import java.util.ArrayList;
import java.util.List;

import net.cyklotron.cms.site.SiteResource;

public class SiteDTO
{
    private long id;

    private String name;

    private boolean isTemplate;

    public long getId()
    {
        return id;
    }

    public void setId(long id)
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

    public boolean isTemplate()
    {
        return isTemplate;
    }

    public void setTemplate(boolean isTemplate)
    {
        this.isTemplate = isTemplate;
    }

    public static SiteDTO create(SiteResource res)
    {
        final SiteDTO dto = new SiteDTO();
        dto.setId(res.getId());
        dto.setName(res.getName());
        dto.setTemplate(res.getTemplate());
        return dto;
    }

    public static List<SiteDTO> create(SiteResource... sites)
    {
        List<SiteDTO> dtos = new ArrayList<>();
        for(SiteResource site : sites)
        {
            dtos.add(create(site));
        }
        return dtos;
    }
}
