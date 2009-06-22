package net.cyklotron.cms.modules.views.documents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.comparator.NameComparator;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.modules.views.BaseSkinableScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: BaseSkinableDocumentScreen.java,v 1.4 2008-10-30 17:54:28 rafal Exp $
 */
public class BaseSkinableDocumentScreen
    extends BaseSkinableScreen
{
    public BaseSkinableDocumentScreen(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        StructureService structureService, StyleService styleService, SkinService skinService,
        MVCFinder mvcFinder, TableStateManager tableStateManager)
    {
        super(context, logger, preferencesService, cmsDataFactory, structureService, styleService,
                        skinService, mvcFinder, tableStateManager);
    }
    
	public void prepareCategories(Context context, boolean checkClassifyPermission)
		throws Exception
	{
        CoralSession coralSession = context.getAttribute(CoralSession.class);
        Permission classifyPermission = coralSession.getSecurity().getUniquePermission("cms.category.classify");
        I18nContext i18nContext = I18nContext.getI18nContext(context);
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
		Parameters screenConfig = getScreenConfig();
		NameComparator<CategoryResource> comparator = new NameComparator<CategoryResource>(i18nContext.getLocale());
		long root1 = screenConfig.getLong("category_id_1", -1);
        long root2 = screenConfig.getLong("category_id_2", -1);
        templatingContext.put("categories_1", getCategoryList(root1,
            checkClassifyPermission, classifyPermission, comparator, coralSession));
        templatingContext.put("categories_2", getCategoryList(root2,
            checkClassifyPermission, classifyPermission, comparator, coralSession));
	}

    private List<CategoryResource> getCategoryList(long rootCategoryId,
        boolean checkClassifyPermission, Permission classifyPermission,
        NameComparator<CategoryResource> comparator, CoralSession coralSession)
        throws EntityDoesNotExistException
    {
        List<CategoryResource> categoryList = new ArrayList<CategoryResource>();
        if(rootCategoryId != -1)
        {
            Resource rootCategory = coralSession.getStore().getResource(rootCategoryId);
            for(Resource childCategory : coralSession.getStore().getResource(rootCategory))
            {
                if(childCategory instanceof CategoryResource)
                {
                    if(!checkClassifyPermission
                        || coralSession.getUserSubject().hasPermission(childCategory,
                            classifyPermission))
                    {
                        categoryList.add((CategoryResource)childCategory);
                    }
                }
            }
            Collections.sort(categoryList, comparator);
        }
        return categoryList;
    }
}
