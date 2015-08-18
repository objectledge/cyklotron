package net.cyklotron.cms.canonical;

import org.objectledge.coral.session.CoralSession;

import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;

import bak.pcj.set.LongSet;

public interface CanonicalLinksService
{
    /**
     * Returns a canonical link for a navigation node based on defined canonical link rules.
     * 
     * @param node a navigation node.
     * @param coralSession Coral session.
     * @return canonical link, or {@code null} if the none of the defined canonical link rules apply
     *         to the given node.
     */
    String getCanonicalLink(NavigationNodeResource node, CoralSession coralSession);
}
