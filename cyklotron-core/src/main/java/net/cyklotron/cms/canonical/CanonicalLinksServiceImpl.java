package net.cyklotron.cms.canonical;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.objectledge.coral.relation.Relation;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;

import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.util.PriorityComparator;

import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;

public class CanonicalLinksServiceImpl
    implements CanonicalLinksService
{
    public CanonicalLinksServiceImpl(CategoryService categoryService)
    {
        this.categoryService = categoryService;
    }

    private final CategoryService categoryService;

    @Override
    public String getCanonicalLink(NavigationNodeResource node, CoralSession coralSession)
    {
        List<CategoryResource> categories = Arrays.asList(categoryService.getCategories(
            coralSession, node, true));
        List<LinkCanonicalRuleResource> rules = new ArrayList<>();
        for(Resource res : coralSession.getStore().getResourceByPath("/cms/canonicalLinkRules/*"))
        {
            if(res instanceof LinkCanonicalRuleResource)
            {
                rules.add((LinkCanonicalRuleResource)res);
            }
        }
        if(categories.size() > 0 && rules.size() > 0)
        {
            Collections.sort(rules,
                Collections.reverseOrder(new PriorityComparator<LinkCanonicalRuleResource>()));
            for(LinkCanonicalRuleResource linkCanonicalRule : rules)
            {
                if(categories.contains(linkCanonicalRule.getCategory()))
                {
                    return linkCanonicalRule.getLinkPattern().replace("{id}", node.getIdString());
                }
            }
        }
        return null;
    }

    @Override
    public LongSet getCanonicalNodes(SiteResource site, CoralSession coralSession)
    {
        List<LinkCanonicalRuleResource> rules = new ArrayList<>();
        for(Resource res : coralSession.getStore().getResourceByPath("/cms/canonicalLinkRules/*"))
        {
            if(res instanceof LinkCanonicalRuleResource)
            {
                rules.add((LinkCanonicalRuleResource)res);
            }
        }
        LongSet result = new LongOpenHashSet();
        Relation rel = categoryService.getResourcesRelation(coralSession);
        // traverse the rules in increasing priority order
        Collections.sort(rules, new PriorityComparator<LinkCanonicalRuleResource>());
        for(LinkCanonicalRuleResource rule : rules)
        {
            Resource cat = rule.getCategory();
            // find all resources tagged with the rule's category
            // if the rule is associated with the rule of interest, include resource ids in the
            // output, otherwise exclude them
            if(site.equals(rule.getSite()))
            {
                LongSet resIds = rel.get(cat.getId());
                result.addAll(resIds);
            }
            else if(!result.isEmpty())
            {
                LongSet resIds = rel.get(cat.getId());
                result.removeAll(resIds);
            }
        }
        return result;
    }
}
