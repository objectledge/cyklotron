package net.cyklotron.cms.structure;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.datatypes.Node;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.SubtreeVisitor;
import org.objectledge.event.EventWhiteboard;
import org.picocontainer.Startable;

import net.cyklotron.cms.site.SiteCreationListener;
import net.cyklotron.cms.site.SiteService;

public class StructureSiteCreationListener
    implements SiteCreationListener, Startable
{
    private final CoralSessionFactory coralSessionFactory;
    
    private final Logger log;

    /**
     * Creates a StructureSiteCreationListener insntance.
     * 
     * @param coralSessionFactory the Coral session factory component.
     * @param eventWhiteboard the event whiteboard component.
     * @param log the log.
     */
    public StructureSiteCreationListener(CoralSessionFactory coralSessionFactory, 
        EventWhiteboard eventWhiteboard, Logger log)
    {
        this.coralSessionFactory = coralSessionFactory;
        this.log = log;
        eventWhiteboard.addListener(SiteCreationListener.class, this, null);
    }
    
    /**
     * {@inheritDoc}
     */
    public void createSite(SiteService siteService, String template, String name)
    {
        CoralSession coralSession = null;
        try
        {
            coralSession = coralSessionFactory.getRootSession();
            Node homePage = (Node)coralSession.getStore().
                getUniqueResourceByPath("/cms/sites/"+name+"/structure"); 
            new SubtreeVisitor() 
            {
                public void visit(NavigationNodeResource node)
                {
                    if(node.getCustomModificationTime() == null)
                    {
                        node.setCustomModificationTime(node.getModificationTime());
                        node.update();
                    }
                }
            }.traverseBreadthFirst(homePage);
        }
        catch(Exception e)
        {
            log.error("failed to update node timestamps", e);
        }
        finally
        {
            if(coralSession != null)
            {
                coralSession.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void start()
    {
    }

    /**
     * {@inheritDoc}
     */
    public void stop()
    {
    }
}
