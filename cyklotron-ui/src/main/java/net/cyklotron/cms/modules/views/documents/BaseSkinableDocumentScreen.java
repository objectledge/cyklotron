package net.cyklotron.cms.modules.views.documents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.comparator.NameComparator;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
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
 * @version $Id: BaseSkinableDocumentScreen.java,v 1.3 2007-11-18 21:25:26 rafal Exp $
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
        Parameters parameters = RequestParameters.getRequestParameters(context);
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Subject subject = coralSession.getUserSubject();
        Permission classifyPermission = coralSession.getSecurity().getUniquePermission("cms.category.classify");
        HttpContext httpContext = HttpContext.getHttpContext(context);
        I18nContext i18nContext = I18nContext.getI18nContext(context);
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
		Parameters screenConfig = getConfiguration(coralSession);
		NameComparator comparator = new NameComparator(i18nContext.getLocale());
		long root1 = screenConfig.getLong("category_id_1",-1);
		long root2 = screenConfig.getLong("category_id_2",-1);
		if(root1 == -1)
		{
			templatingContext.put("categories_1", new ArrayList());
		}
		else
		{
			Resource resource = coralSession.getStore().getResource(root1);
			Resource[] resources = coralSession.getStore().getResource(resource);
			List list1 = new ArrayList();
			for(int i = 0; i < resources.length; i++)
			{
				if(resources[i] instanceof CategoryResource)
				{
                    if(!checkClassifyPermission ||
                        subject.hasPermission(resources[i], classifyPermission)            
                    )
                    {
                        list1.add(resources[i]);
                    }
				}
			}
			Collections.sort(list1, comparator);
			templatingContext.put("categories_1", list1);
		}
		if(root2 == -1)
		{
			templatingContext.put("categories_2", new ArrayList());
		}
		else
		{
			Resource resource = coralSession.getStore().getResource(root2);
			Resource[] resources = coralSession.getStore().getResource(resource);
			List list2 = new ArrayList();
			for(int i = 0; i < resources.length; i++)
			{
				if(resources[i] instanceof CategoryResource)
				{
                    if(!checkClassifyPermission ||
                        subject.hasPermission(resources[i], classifyPermission))
                    {
                        list2.add(resources[i]);
                    }
				}
			}
			Collections.sort(list2, comparator);
			templatingContext.put("categories_2", list2);
		}
	}
}
