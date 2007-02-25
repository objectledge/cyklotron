package net.cyklotron.cms.modules.views.related;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.related.RelatedService;

/**
 * The base screen class for related application screens
 */
public abstract class BaseRelatedScreen
    extends BaseCMSScreen
{
    protected RelatedService relatedService;

    public BaseRelatedScreen(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        RelatedService relatedService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.relatedService = relatedService;
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        if(!cmsData.isApplicationEnabled("related"))
        {
            logger.debug("Application 'related' not enabled in site");
            return false;
        }
        return true;
    }

}
