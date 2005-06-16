package net.cyklotron.cms.modules.actions.syndication;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.syndication.IncomingFeedResource;
import net.cyklotron.cms.syndication.IncomingFeedUtil;
import net.cyklotron.cms.syndication.OutgoingFeedResource;
import net.cyklotron.cms.syndication.OutgoingFeedUtil;
import net.cyklotron.cms.syndication.SyndicationService;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;

/**
 * Syndication application base action.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseSyndicationAction.java,v 1.1 2005-06-16 11:14:13 zwierzem Exp $
 */
public abstract class BaseSyndicationAction extends BaseCMSAction
{
    protected SyndicationService syndicationService;

    public BaseSyndicationAction(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SyndicationService syndicationService)
    {
        super(logger, structureService, cmsDataFactory);
        this.syndicationService = syndicationService;
    }

    public IncomingFeedResource getIncomingFeed(CoralSession coralSession, Parameters parameters)
    throws ProcessingException
    {
        return IncomingFeedUtil.getFeed(coralSession, parameters);
    }

    public OutgoingFeedResource getOutgoingFeed(CoralSession coralSession, Parameters parameters)
    throws ProcessingException
    {
        return OutgoingFeedUtil.getFeed(coralSession, parameters);
    }
}


