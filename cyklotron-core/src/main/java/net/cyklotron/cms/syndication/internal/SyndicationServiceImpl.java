package net.cyklotron.cms.syndication.internal;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.Resource;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.templating.Templating;

import net.cyklotron.cms.CmsNodeResourceImpl;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.syndication.CannotCreateSyndicationRootException;
import net.cyklotron.cms.syndication.IncomingFeedsManager;
import net.cyklotron.cms.syndication.OutgoingFeedsManager;
import net.cyklotron.cms.syndication.SyndicationService;
import net.cyklotron.cms.syndication.TooManySyndicationRootsException;
import net.cyklotron.cms.util.OfflineLinkRenderingService;

/**
 * Implementation of Syndication Service.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SyndicationServiceImpl.java,v 1.3 2006-02-09 13:00:48 pablo Exp $
 */
public class SyndicationServiceImpl 
implements SyndicationService
{
    private Logger log;

    private IncomingFeedsManager incomingFeedsManager;

    private OutgoingFeedsManager outgoingFeedsManager;

    public SyndicationServiceImpl(FileSystem fileService, CategoryQueryService categoryQueryService,
        OfflineLinkRenderingService offlineLinkRenderingService, Templating templating,
        CoralSessionFactory coralSessionFactory, IntegrationService integrationService,
        SiteService siteService, Logger log)
    {
        this.log = log;

        incomingFeedsManager = new DefaultIncomingFeedsManager(this, fileService);
        outgoingFeedsManager = new DefaultOutgoingFeedsManager(coralSessionFactory, this, fileService, categoryQueryService,
            offlineLinkRenderingService, templating, integrationService, siteService);
    }

    public IncomingFeedsManager getIncomingFeedsManager()
    {
        return incomingFeedsManager;
    }

    public OutgoingFeedsManager getOutgoingFeedsManager()
    {
        return outgoingFeedsManager;
    }

    public synchronized Resource getAppParent(CoralSession coralSession, SiteResource site)
    throws TooManySyndicationRootsException, CannotCreateSyndicationRootException
    {
        Resource[] res = coralSession.getStore().getResource(site, SYNDICATION_ROOT);
        if(res.length > 1)
        {
            throw new TooManySyndicationRootsException("more than one '"+SYNDICATION_ROOT+
                "' root found for site '"+site.getName()+"' with id "+site.getIdString());
        }
        else if(res.length == 0)
        {
            try
            {
                return CmsNodeResourceImpl.createCmsNodeResource(coralSession, SYNDICATION_ROOT, site);
            }
            catch(InvalidResourceNameException e)
            {
                throw new CannotCreateSyndicationRootException("wrong name", e);
            }
        }
        return res[0];
    }
}
