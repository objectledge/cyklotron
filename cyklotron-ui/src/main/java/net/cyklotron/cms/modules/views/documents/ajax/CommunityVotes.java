package net.cyklotron.cms.modules.views.documents.ajax;

import java.io.IOException;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.structure.NavigationNodeResource;

import org.codehaus.jackson.JsonGenerationException;
import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.web.json.AbstractJsonView;

public class CommunityVotes
    extends AbstractJsonView
{
    private final CmsDataFactory cmsDataFactory;

    public CommunityVotes(CmsDataFactory cmsDataFactory, Context context, Logger log)
    {
        super(context, log);
        this.cmsDataFactory = cmsDataFactory;
    }

    @Override
    protected void buildJsonStream()
        throws ProcessingException, JsonGenerationException, IOException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        CoralSession coralSession = context.getAttribute(CoralSession.class);
        NavigationNodeResource node = cmsData.getContentNode();
        if(node != null && node.canView(coralSession, coralSession.getUserSubject()))
        {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("positive",
                node.isVotesPositiveDefined() ? node.getVotesPositive() : 0);
            jsonGenerator.writeNumberField("negative",
                node.isVotesNegativeDefined() ? node.getVotesNegative() : 0);
            jsonGenerator.writeEndObject();
        }
        else
        {
            jsonGenerator.writeNull();
        }
    }

    @Override
    protected String getCallbackParameterName()
    {
        return "callback";
    }
}
