package net.cyklotron.cms.modules.views;

import org.objectledge.context.Context;
import org.objectledge.coral.modules.views.BaseCoralView;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.builders.EnclosingView;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;

public class RenderComponent
    extends BaseCoralView
{
    private final CmsDataFactory cmsDataFactory;

    public RenderComponent(Context context, CmsDataFactory cmsDataFactory)
    {
        super(context);
        this.cmsDataFactory = cmsDataFactory;
    }

    @Override
    public void process(Parameters parameters, TemplatingContext templatingContext,
        MVCContext mvcContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        cmsData.setAdminMode(false);
    }

    @Override
    public EnclosingView getEnclosingView(String thisViewName)
    {
        return EnclosingView.TOP;
    }
}
