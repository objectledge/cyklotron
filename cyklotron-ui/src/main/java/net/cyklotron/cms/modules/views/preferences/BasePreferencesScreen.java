package net.cyklotron.cms.modules.views.preferences;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.table.TableStateManager;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: BasePreferencesScreen.java,v 1.2 2005-01-26 09:00:41 pablo Exp $
 */
public abstract class BasePreferencesScreen 
    extends BaseCMSScreen
{
    
    public BasePreferencesScreen(Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        // TODO Auto-generated constructor stub
    }
}
