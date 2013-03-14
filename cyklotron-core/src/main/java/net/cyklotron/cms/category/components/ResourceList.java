package net.cyklotron.cms.category.components;

import java.util.ArrayList;
import java.util.List;

import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.CmsComponentData;
import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.query.CategoryQueryException;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureException;
import net.cyklotron.cms.structure.StructureService;

import bak.pcj.set.LongSet;

/**
 * This class contains logic of component which displays lists of resources assigned
 * to queried categories.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: ResourceList.java,v 1.6 2005-05-17 06:19:58 zwierzem Exp $
 */
public class ResourceList
    extends BaseResourceList
{
    protected final CategoryQueryService categoryQueryService;
    
    /** site service */
    protected final SiteService siteService;

    protected final StructureService structureService;
	
    public ResourceList(Context context, IntegrationService integrationService,
        CmsDataFactory cmsDataFactory,  CategoryQueryService categoryQueryService,
        SiteService siteService, StructureService structureService)
	{
        super(context, integrationService, cmsDataFactory);
		this.categoryQueryService = categoryQueryService;
        this.siteService = siteService;
        this.structureService = structureService;
	}

    public BaseResourceListConfiguration createConfig()
        throws ProcessingException
    {
        return new ResourceListConfiguration();
    }
    
    private CategoryQueryResource categoryQuery;
    private boolean categoryQuerySought = false;

    public String getQuery(CoralSession coralSession, BaseResourceListConfiguration config)
        throws ProcessingException
    {
		String query = null;
        categoryQuery = getCategoryQueryRes(coralSession, config);
		if (categoryQuery != null)
		{
			query = categoryQuery.getQuery();
		} 
		return query;
    }

    protected String[] getResourceClasses(CoralSession coralSession, BaseResourceListConfiguration config)
	throws ProcessingException
    {
		String[] resClassNames = null;
		categoryQuery = getCategoryQueryRes(coralSession, config);
    	if (categoryQuery != null)
        {
			resClassNames = categoryQuery.getAcceptedResourceClassNames();
        } 
		if(resClassNames == null)
		{
			resClassNames = new String[0];
		}
		return resClassNames;
    }
    
    @Override
    public LongSet getIdSet(CoralSession coralSession, BaseResourceListConfiguration config)
        throws ProcessingException
    {
        LongSet idSet = super.getIdSet(coralSession, config);

        CategoryQueryResource categoryQuery = getCategoryQueryRes(coralSession, config);
        List<SiteResource> acceptedSites = new ArrayList<>();
        if(categoryQuery != null)
        {
            try
            {
                for(String siteName : categoryQuery.getAcceptedSiteNames())
                {
                    acceptedSites.add(siteService.getSite(coralSession, siteName));
                }
            }
            catch(SiteException e)
            {
                throw new ProcessingException("illegal site name", e);
            }
            try
            {
                idSet = structureService.restrictNodeIdSet(acceptedSites, idSet, coralSession);
            }
            catch(StructureException e)
            {
                throw new ProcessingException(e);
            }
        }
        return idSet;
    }

    // implementation /////////////////////////////////////////////////////////////////////////////
    
    protected CategoryQueryResource getCategoryQueryRes(
    	CoralSession coralSession, BaseResourceListConfiguration config)
    	throws ProcessingException
	{
		CmsData cmsData = cmsDataFactory.getCmsData(context);
		
		ResourceListConfiguration config2 = (ResourceListConfiguration)config;
		String name = config2.getCategoryQueryName();

        if (categoryQuerySought)
        {
            return categoryQuery;
        }

        // guard from multiple error reporting
        categoryQuerySought = true;

		CmsComponentData compData = cmsData.getComponent();
		if(name.length() == 0 && compData != null)
		{
            compData.error("category query not configured", null);
            return null;
		}

		try
		{
			Resource[] res = coralSession.getStore().getResource(
							   categoryQueryService.getCategoryQueryRoot(coralSession, cmsData.getSite()),
							   name);
			
			if(res.length == 1)
			{
				categoryQuery = (CategoryQueryResource) res[0];
			}
			else if(res.length == 0 && compData != null)
			{
                compData.error("no category query with name '"+name+"'", null);
			}
			else if(compData != null)
			{
                compData.error("too many category query objects", null);
			}
		}
		catch (CategoryQueryException e)
		{
			if(compData != null)
			{
                compData.error("cannot get category query object", e);
			}
			return null;
		}    

		return categoryQuery;
	}
}
