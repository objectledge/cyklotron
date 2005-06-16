package net.cyklotron.cms.modules.views.syndication;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.syndication.IncomingFeedResource;
import net.cyklotron.cms.syndication.IncomingFeedUtil;
import net.cyklotron.cms.syndication.OutgoingFeedResource;
import net.cyklotron.cms.syndication.OutgoingFeedUtil;
import net.cyklotron.cms.syndication.SyndicationService;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;

/**
 * Syndication application base screen.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseSyndicationScreen.java,v 1.1 2005-06-16 11:14:14 zwierzem Exp $
 */
public abstract class BaseSyndicationScreen extends BaseCMSScreen
{
    /** syndication service */
    protected SyndicationService syndicationService;

    public BaseSyndicationScreen(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        SyndicationService syndicationService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
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
