package net.cyklotron.cms.modules.views.search;

import org.objectledge.context.Context;
import org.objectledge.coral.modules.views.BaseCoralView;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.mvc.MVCContext;

public class ReindexDocuments extends BaseCoralView
{

    public ReindexDocuments(Context context)
    {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void process(Parameters parameters, TemplatingContext templatingContext,
        MVCContext mvcContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        // TODO Auto-generated method stub
        
    }

}
