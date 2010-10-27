package net.cyklotron.cms.modules.views.poll;

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
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.poll.PollResource;
import net.cyklotron.cms.poll.PollResourceImpl;
import net.cyklotron.cms.poll.PollService;
import net.cyklotron.cms.preferences.PreferencesService;


/**
 *
 */
public class PollResults
    extends BasePollScreen
{
    
    public PollResults(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, PollService pollService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, pollService);
        
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        int pid = parameters.getInt("pid", -1);
        if(pid == -1)
        {
            throw new ProcessingException("Poll id not found");
        }
        try
        {
            Map questions = new HashMap();
            Map resultMap= new HashMap();
            Map percentMap= new HashMap();
            PollResource pollResource = PollResourceImpl.getPollResource(coralSession, pid);
            pollService.prepareMaps(coralSession, pollResource, questions, resultMap, percentMap);
            List questionKeys = new ArrayList();
            for(int i = 0; i< questions.size(); i++)
            {
                questionKeys.add(new Integer(i));
            }
            templatingContext.put("questionKeys",questionKeys);
            templatingContext.put("poll",pollResource);
            templatingContext.put("results",resultMap);
            templatingContext.put("percent",percentMap);
            templatingContext.put("questions",questions);
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            logger.error("PollException: ",e);
            return;
        }
    }
}
