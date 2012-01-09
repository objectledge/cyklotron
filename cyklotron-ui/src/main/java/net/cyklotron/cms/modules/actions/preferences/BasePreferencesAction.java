/*
 */
package net.cyklotron.cms.modules.actions.preferences;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.structure.StructureService;

/**
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 */
public abstract class BasePreferencesAction extends BaseCMSAction
{
    protected PreferencesService preferencesService;
    
    
    public BasePreferencesAction(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, PreferencesService preferencesService)
    {
        super(logger, structureService, cmsDataFactory);
        this.preferencesService = preferencesService;
    }
    
    public boolean checkAccessRights(Context context) throws ProcessingException
    {
        return super.checkAdministrator(context);
    }
}
