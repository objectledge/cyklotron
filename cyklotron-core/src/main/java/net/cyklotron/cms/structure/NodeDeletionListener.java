package net.cyklotron.cms.structure;

import org.objectledge.coral.session.CoralSession;

/**
 * Implementations of this interface can be registered with StructureService to perform necessary
 * cleanup before nodes are removed from storage.
 * 
 * @author rafal.krzewski@objectledge.org
 */
public interface NodeDeletionListener
{
    void beforeDeletion(NavigationNodeResource node, CoralSession coralSession)
        throws Exception;

    void afterDeletion(NavigationNodeResource node, CoralSession coralSession)
        throws Exception;
}
