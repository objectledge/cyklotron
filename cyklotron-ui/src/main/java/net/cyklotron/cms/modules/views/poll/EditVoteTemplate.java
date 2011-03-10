/*
 * Created on Oct 29, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.modules.views.poll;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.MergingException;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.confirmation.EmailConfirmationService;
import net.cyklotron.cms.periodicals.PeriodicalsService;
import net.cyklotron.cms.periodicals.PeriodicalsTemplatingService;
import net.cyklotron.cms.poll.PollService;
import net.cyklotron.cms.poll.VoteResource;
import net.cyklotron.cms.poll.VoteResourceImpl;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;

/**
 * @author fil To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EditVoteTemplate
    extends BasePollScreen
{
    private Templating templating;

    private PollService pollService;

    public EditVoteTemplate(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, Templating templating, PollService pollService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, pollService);
        this.templating = templating;
        this.pollService = pollService;
    }

    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession)
        throws ProcessingException
    {

        int vid = parameters.getInt("vid", -1);
        if(vid == -1)
        {
            throw new ProcessingException("Vote id not found");
        }
        try
        {
            VoteResource vote = VoteResourceImpl.getVoteResource(coralSession, vid);
            String contents = pollService.getVoteConfiramationTicketContents(vote, i18nContext
                .getLocale());
            templatingContext.put("name", vote.getName());

            if(contents == null)
            {
                contents = "";
            }
            if(!templatingContext.containsKey("result"))
            {
                TemplatingContext blankContext = templating.createContext();
                StringReader in = new StringReader(contents);
                StringWriter out = new StringWriter();

                templating.merge(blankContext, in, out, "<component template>");

            }
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
            logger.error("VoteException: ", e);
            return;
        }
        catch(MergingException e)
        {
            templatingContext.put("result", "template_parse_error");
            templatingContext.put("parse_trace", new StackTrace(e));
        }
    }
}
