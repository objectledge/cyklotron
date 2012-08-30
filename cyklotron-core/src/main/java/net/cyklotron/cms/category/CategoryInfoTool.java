package net.cyklotron.cms.category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.comparator.NameComparator;
import org.objectledge.i18n.I18nContext;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.integration.ApplicationResource;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.ResourceClassResource;

/**
 * Tool for getting information on category's resource classes support.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryInfoTool.java,v 1.3 2005-02-09 22:19:55 rafal Exp $
 */
public class CategoryInfoTool
{
    /** Integration service for information on resource classes */
    private IntegrationService integrationService;
    /** Category service for category manipulation */
    private CategoryService categoryService;

    private Context context;
    public CategoryInfoTool(Context context, IntegrationService integrationService, CategoryService
        categoryService)
    {
        this.context = context;
        this.integrationService = integrationService;
        this.categoryService = categoryService;
        I18nContext i18nContext = I18nContext.getI18nContext(context); 
        nameComp = new NameComparator(i18nContext.getLocale());
    }
    
    // resource categorisation support ////////////////////////////////////////

    /** Used keep categories mapped by bound resources. */
    private HashMap ownedCategories;

    /** Used keep categories mapped by categorised (bound and bound by category inheritance)
      * resources. */
    private HashMap supportedCategories;

	public List getCategoriesAsList(Resource resource, boolean useImplied)
	{
		Set set = getCategories(resource,useImplied);
		List list = new ArrayList(set);
		Collections.sort(list, nameComp);
		return list;
	}

	public Set getCategories(Resource resource, boolean useImplied)
	{
		if(useImplied)
		{
			supportedCategories = makeResourceCategoryMap(supportedCategories, resource, true);
			return (Set)(supportedCategories.get(resource));
		}
		else
		{
			ownedCategories = makeResourceCategoryMap(ownedCategories, resource, false);
			return (Set)(ownedCategories.get(resource));
		}
	}

    public boolean hasCategory(Resource resource, CategoryResource category)
    {
        ownedCategories = makeResourceCategoryMap(ownedCategories, resource, false);
        HashSet resCategories = (HashSet)(ownedCategories.get(resource));
        return resCategories.contains(category);
    }
    
     

    public boolean supportsCategory(Resource resource, CategoryResource category)
    {
        supportedCategories = makeResourceCategoryMap(supportedCategories, resource, true);
        HashSet resCategories = (HashSet)(supportedCategories.get(resource));
        return resCategories.contains(category);
    }

    private HashMap makeResourceCategoryMap(HashMap map, Resource resource, boolean useImplied)
    {
        if(map == null)
        {
            map = new HashMap();
        }

        if(!map.containsKey(resource))
        {
            CategoryResource[] categories = categoryService.getCategories(getCoralSession(context),resource, useImplied);
            HashSet resCategories = new HashSet(categories.length);
            for(int i=0; i<categories.length; i++)
            {
                resCategories.add(categories[i]);
            }
            map.put(resource, resCategories);
        }

        return map;
    }

    // resource classes support ///////////////////////////////////////////////

    /** Used to sort apllications and resource classes by name. */
    private NameComparator nameComp;

    /** Temporary storage for registry information */
    private ArrayList registry;

    private void resourceClassesInit()
    {
        if(registry == null)
        {
            registry = new ArrayList();
            // 1. get apps
            List apps = Arrays.asList(integrationService.getApplications(getCoralSession(context)));
            // sort for deterministic output
            Collections.sort(apps, nameComp);

            for(Iterator i = apps.iterator(); i.hasNext();)
            {
                ArrayList resClassesOut = new ArrayList();
                ApplicationResource app = (ApplicationResource)(i.next());

                // 2. get res classes for app
                List resClasses = Arrays.asList(integrationService.getResourceClasses(getCoralSession(context),app));
                // sort for deterministic output
                Collections.sort(resClasses, nameComp);

                // filter resClasses by categorization possbility
                for(Iterator j=resClasses.iterator(); j.hasNext();)
                {
                    ResourceClassResource resClass = (ResourceClassResource)(j.next());
                    if(resClass.getCategorizable())
                    {
                        resClassesOut.add(resClass);
                    }
                }

                // show app only if it has categorizable res classes
                if(resClassesOut.size() > 0)
                {
                    HashMap appInfo = new HashMap();
                    appInfo.put("app", app);
                    appInfo.put("resClasses", resClassesOut);
                    registry.add(appInfo);
                }
            }
        }
    }


    private ResourceClassResource getResourceClass(Resource resource)
        throws ProcessingException
    {
        ResourceClassResource resClass =
            integrationService.getResourceClass(getCoralSession(context),resource.getResourceClass());
        if(resClass == null)
        {
            throw new ProcessingException("Cannot find resource class for resource id="
                +resource.getIdString());
        }
        if(!resClass.getCategorizable())
        {
            throw new ProcessingException("Cannot categorize non categorizable resources,"+
            " resource id="+resource.getIdString()+", resource class id="+resClass.getIdString());
        }
        return resClass;
    }

    /** Used to keep resource classes mapped by resources. */
    private HashMap resourceClasses;

    /** Used to keep categories mapped by resource classes. */
    private HashMap categorizableBy;

    public boolean supportsResourceClass(CategoryResource category, Resource resource)
        throws ProcessingException
    {
        if(resourceClasses == null)
        {
            resourceClasses = new HashMap();
        }
        if(!resourceClasses.containsKey(resource))
        {
            resourceClasses.put(resource, getResourceClass(resource));
        }
        ResourceClassResource resClass = (ResourceClassResource)resourceClasses.get(resource);

        if(category == null || resClass == null)
        {
            return false;
        }

        if(categorizableBy == null)
        {
            categorizableBy = new HashMap();
        }
        if(!categorizableBy.containsKey(resClass))
        {
            categorizableBy.put(resClass, new HashMap());
        }

        HashMap categories = (HashMap)(categorizableBy.get(resClass));

        if(!categories.containsKey(category))
        {
            categories.put(category,
                           new Boolean(categoryService.supportsResourceClass(getCoralSession(context),category, resClass)));
        }

        return ((Boolean)(categories.get(category))).booleanValue();
    }
    
    
    public boolean supportsResourceClass(CategoryResource category, ResourceClassResource resClass)
        throws ProcessingException
    {

        if(category == null || resClass == null)
        {
            return false;
        }

        if(categorizableBy == null)
        {
            categorizableBy = new HashMap();
        }
        if(!categorizableBy.containsKey(resClass))
        {
            categorizableBy.put(resClass, new HashMap());
        }

        HashMap categories = (HashMap)(categorizableBy.get(resClass));

        if(!categories.containsKey(category))
        {
            categories.put(
                category,
                new Boolean(categoryService.supportsResourceClass(getCoralSession(context),
                    category, resClass)));
        }

        return ((Boolean)(categories.get(category))).booleanValue();
    }
        

    public List getSupportedResourceClasses(CategoryResource category)
    {
        resourceClassesInit();

        ArrayList resourceClassesInfos = new ArrayList();
        if(category == null)
        {
            return resourceClassesInfos;
        }

        for(Iterator i = registry.iterator(); i.hasNext();)
        {
            HashMap appInfo = (HashMap)(i.next());
            ArrayList resClassesInfo = new ArrayList();

            List resClasses = (List)(appInfo.get("resClasses"));

            for(Iterator j=resClasses.iterator(); j.hasNext();)
            {
                ResourceClassResource resClass = (ResourceClassResource)(j.next());

                if(categoryService.supportsResourceClass(getCoralSession(context),category, resClass))
                {
                    resClassesInfo.add(new ResourceClassInfo(resClass,
                        categoryService.hasResourceClass(getCoralSession(context),category, resClass),
                        true
                        ));
                }
            }

            if(resClassesInfo.size() > 0)
            {
                resourceClassesInfos.add(new ResourceClassesInfo(
                    (ApplicationResource)appInfo.get("app"), resClassesInfo));
            }
        }
        return resourceClassesInfos;
    }

    public List getOwnedResourceClasses(CategoryResource category)
    {
        resourceClassesInit();

        ArrayList resourceClassesInfos = new ArrayList();
        if(category == null)
        {
            return resourceClassesInfos;
        }

        for(Iterator i = registry.iterator(); i.hasNext();)
        {
            HashMap appInfo = (HashMap)(i.next());
            ArrayList resClassesInfo = new ArrayList();

            List resClasses = (List)(appInfo.get("resClasses"));

            for(Iterator j=resClasses.iterator(); j.hasNext();)
            {
                ResourceClassResource resClass = (ResourceClassResource)(j.next());

                if(categoryService.hasResourceClass(getCoralSession(context),category, resClass))
                {
                    resClassesInfo.add(new ResourceClassInfo(resClass, true, true));
                }
            }

            if(resClassesInfo.size() > 0)
            {
                resourceClassesInfos.add(new ResourceClassesInfo(
                    (ApplicationResource)appInfo.get("app"), resClassesInfo));
            }
        }
        return resourceClassesInfos;
    }

    public List getResourceClassesInfo()
    {
        return getResourceClassesInfo((CategoryResource)null);
    }

    public List getResourceClassesInfo(CategoryResource category)
    {
        resourceClassesInit();

        ArrayList resourceClassesInfos = new ArrayList();

        for(Iterator i = registry.iterator(); i.hasNext();)
        {
            HashMap appInfo = (HashMap)(i.next());
            ArrayList resClassesInfo = new ArrayList();

            List resClasses = (List)(appInfo.get("resClasses"));

            for(Iterator j=resClasses.iterator(); j.hasNext();)
            {
                ResourceClassResource resClass = (ResourceClassResource)(j.next());
                if(category != null)
                {
                    resClassesInfo.add(new ResourceClassInfo(resClass,
                        categoryService.hasResourceClass(getCoralSession(context),category, resClass),
                        categoryService.supportsResourceClass(getCoralSession(context),category, resClass)
                        ));
                }
                else
                {
                    resClassesInfo.add(new ResourceClassInfo(resClass, false, false));
                }
            }

            if(resClassesInfo.size() > 0)
            {
                resourceClassesInfos.add(new ResourceClassesInfo(
                    (ApplicationResource)appInfo.get("app"), resClassesInfo));
            }
        }
        return resourceClassesInfos;
    }

    public class ResourceClassesInfo
    {
        private ApplicationResource app;
        private ArrayList resourceClassesInfos;

        public ResourceClassesInfo(ApplicationResource app, ArrayList resourceClassesInfos)
        {
            this.app = app;
            this.resourceClassesInfos = resourceClassesInfos;
        }

        public ArrayList getResourceClassesInfos()
        {
            return resourceClassesInfos;
        }

        public ApplicationResource getApplication()
        {
            return app;
        }
    }

    public class ResourceClassInfo
    {
        private ResourceClassResource resClass;
        private boolean supported;
        private boolean owned;
        private boolean inherited;

        public ResourceClassInfo(ResourceClassResource resClass, boolean owned, boolean supported)
        {
            this.resClass = resClass;
            this.supported = supported;
            this.owned = owned;
            this.inherited = supported && !owned;
        }

        public ResourceClassResource getResourceClass()
        {
            return resClass;
        }

        public boolean getSupported()
        {
            return supported;
        }

        public boolean getOwned()
        {
            return owned;
        }

        public boolean getInherited()
        {
            return inherited;
        }
    }
    
    private CoralSession getCoralSession(Context context)
    {   
        return (CoralSession)context.getAttribute(CoralSession.class);
    }

}
