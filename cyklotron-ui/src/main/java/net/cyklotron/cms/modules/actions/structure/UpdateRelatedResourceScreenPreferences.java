package net.cyklotron.cms.modules.actions.structure;

import java.util.Iterator;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.components.RelatedResourceListConfiguration;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.ComponentDataCacheService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

public class UpdateRelatedResourceScreenPreferences
    extends UpdateScreenPreferences
{
    public UpdateRelatedResourceScreenPreferences(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService,
        PreferencesService preferencesService, SiteService siteService,
        ComponentDataCacheService componentDataCacheService)
    {
        super(logger, structureService, cmsDataFactory, styleService, preferencesService,
                        siteService, componentDataCacheService);

    }

    public void modifyNodePreferences(Context context, Parameters conf, Parameters parameters,
        CoralSession coralSession)
        throws ProcessingException
    {
        super.modifyNodePreferences(context, conf, parameters, coralSession);
        // get config
        RelatedResourceListConfiguration config = RelatedResourceListConfiguration
            .getConfig(context);
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        config.update(cmsData, parameters);
        // remove it from session
        RelatedResourceListConfiguration.removeConfig(context);

        // build paths list
        Set categories = config.getCategorySelectionState().getEntities(coralSession, "active")
            .keySet();
        String[] categoryPaths = new String[categories.size()];
        StringBuilder buf = new StringBuilder(128);
        int j = 0;
        for(Iterator i = categories.iterator(); i.hasNext(); j++)
        {
            CategoryResource cat = (CategoryResource)(i.next());
            buf.setLength(0);
            buf.append('\'').append(cat.getPath()).append('\'');
            categoryPaths[j] = buf.toString();
        }

        conf.remove(RelatedResourceListConfiguration.ACTIVE_CATEGORIES_PARAM_KEY);
        conf.add(RelatedResourceListConfiguration.ACTIVE_CATEGORIES_PARAM_KEY, categoryPaths);
        conf.set(RelatedResourceListConfiguration.SITE_FILTER_PARAM_KEY,
            parameters.getBoolean(RelatedResourceListConfiguration.SITE_FILTER_PARAM_KEY, false));
    }

}
