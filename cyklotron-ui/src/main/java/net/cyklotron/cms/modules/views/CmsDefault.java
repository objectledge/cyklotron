package net.cyklotron.cms.modules.views;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.table.TableStateManager;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 * The main screen of the CMS application that displays page contents.
 */
public class CmsDefault extends BaseSkinableScreen
{

    public CmsDefault(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, StructureService structureService,
        StyleService styleService, SkinService skinService, MVCFinder mvcFinder
        , TableStateManager tableStateManager)
    {
        super(context, logger, preferencesService, cmsDataFactory, structureService, styleService,
                        skinService, mvcFinder, tableStateManager);
        // TODO Auto-generated constructor stub
    }
}
