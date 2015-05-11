package net.cyklotron.cms.modules.rest.accesslimits.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.objectledge.coral.store.Resource;

import net.cyklotron.cms.accesslimits.RuleResource;

public class RuleDTO
{
    private Long id;

    private int priority;
    
    private String ruleName;

    private String ruleDefinition;

    public RuleDTO()
    {
    }

    public RuleDTO(RuleResource resource)
    {
        this.id = resource.getId();
        this.priority = resource.getPriority();
        this.ruleName = resource.getRuleName();
        this.ruleDefinition = resource.getRuleDefinition();
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }
    
    public String getRuleName()
    {
        return ruleName;
    }
    
    public void setRuleName(String ruleName)
    {
        this.ruleName = ruleName;
    }

    public int getPriority()
    {
        return priority;
    }

    public void setPriority(int priority)
    {
        this.priority = priority;
    }

    public String getRuleDefinition()
    {
        return ruleDefinition;
    }

    public void setRuleDefinition(String ruleDefinition)
    {
        this.ruleDefinition = ruleDefinition;
    }

    private static final Comparator<RuleDTO> BY_PRIORITY = new Comparator<RuleDTO>()
        {
            @Override
            public int compare(RuleDTO o1, RuleDTO o2)
            {
                return o1.priority - o2.priority;
            }
        };

    public static List<RuleDTO> create(Resource[] rules)
    {
        List<RuleDTO> result = new ArrayList<RuleDTO>(rules.length);
        for(Resource rule : rules)
        {
            result.add(new RuleDTO((RuleResource)rule));
        }
        Collections.sort(result, BY_PRIORITY);
        return result;
    }
}