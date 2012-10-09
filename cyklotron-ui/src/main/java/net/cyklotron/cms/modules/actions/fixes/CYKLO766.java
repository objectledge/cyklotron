package net.cyklotron.cms.modules.actions.fixes;

import java.util.Iterator;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;

import net.cyklotron.cms.periodicals.EmailPeriodicalResource;
import net.cyklotron.cms.periodicals.PeriodicalResource;

public class CYKLO766
    implements Valve
{
    private Logger logger;

    public CYKLO766(Logger logger)
    {
        this.logger = logger;
    }

    @Override
    public void process(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = context.getAttribute(CoralSession.class);
        try
        {
            QueryResults results = coralSession.getQuery().executeQuery("FIND RESOURCE FROM cms.periodicals.periodical");
            Iterator rows = results.iterator();
            while(rows.hasNext())
            {
                QueryResults.Row row = (QueryResults.Row)rows.next();
                PeriodicalResource p = (PeriodicalResource)row.get();
                if(p instanceof EmailPeriodicalResource && !((EmailPeriodicalResource)p).isSendEmptyDefined())
                {
                    ((EmailPeriodicalResource)p).setSendEmpty(false);
                }
            }
        }
        catch(Exception e)
        {
            logger.error("failed to lookup periodicals", e);
        }        
    }
}