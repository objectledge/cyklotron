package net.cyklotron.cms.modules.rest.accesslimits.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.objectledge.coral.store.Resource;

import net.cyklotron.cms.accesslimits.ProtectedItemResource;

public class ProtectedItemDTO
{
    private long id;

    private String urlPattern;

    private List<RuleDTO> rules;

    public ProtectedItemDTO()
    {
    }

    public ProtectedItemDTO(ProtectedItemResource resource)
    {
        this.id = resource.getId();
        this.urlPattern = resource.getUrlPattern();
        this.rules = RuleDTO.create(resource.getChildren());
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getUrlPattern()
    {
        return urlPattern;
    }

    public void setUrlPattern(String urlPattern)
    {
        this.urlPattern = urlPattern;
    }

    public List<RuleDTO> getRules()
    {
        return rules;
    }

    public void setRules(List<RuleDTO> rules)
    {
        this.rules = rules;
    }

    private static final Comparator<ProtectedItemDTO> BY_ID = new Comparator<ProtectedItemDTO>()
        {
            @Override
            public int compare(ProtectedItemDTO o1, ProtectedItemDTO o2)
            {
                return o1.id > o2.id ? 1 : (o1.id < o2.id ? -1 : 0);
            }
        };

    public static List<ProtectedItemDTO> create(Resource[] items)
    {
        List<ProtectedItemDTO> result = new ArrayList<ProtectedItemDTO>(items.length);
        for(Resource item : items)
        {
            result.add(new ProtectedItemDTO((ProtectedItemResource)item));
        }
        Collections.sort(result, BY_ID);
        return result;
    }
}