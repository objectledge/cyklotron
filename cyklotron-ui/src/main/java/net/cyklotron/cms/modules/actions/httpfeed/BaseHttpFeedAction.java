package net.cyklotron.cms.modules.actions.httpfeed;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.httpfeed.HttpFeedResource;
import net.cyklotron.cms.httpfeed.HttpFeedService;
import net.cyklotron.cms.httpfeed.HttpFeedUtil;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.structure.StructureService;

/**
 * Http feed application base action.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseHttpFeedAction.java,v 1.2 2005-01-24 10:27:54 pablo Exp $
 */
public abstract class BaseHttpFeedAction 
    extends BaseCMSAction
{
    /** http feed service */
    protected HttpFeedService httpFeedService;

    public BaseHttpFeedAction(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, HttpFeedService httpFeedService)
    {
        super(logger, structureService, cmsDataFactory);
        this.httpFeedService = httpFeedService;
    }

    public HttpFeedResource getFeed(CoralSession coralSession, Parameters parameters)
    throws ProcessingException
    {
        return HttpFeedUtil.getFeed(coralSession, parameters);
    }
}


