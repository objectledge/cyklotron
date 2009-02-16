package net.cyklotron.cms.modules.actions.fixes;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.query.MalformedQueryException;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.ComponentResource;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.ScreenResource;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: FixToLedge.java,v 1.7 2007-01-21 17:13:14 pablo Exp $
 */
public class FixToLedge
    extends BaseCMSAction
{
	SiteService siteService;
	
	SkinService skinService;
	
	IntegrationService integrationService;
	
    public FixToLedge(Logger logger, StructureService structureService, CmsDataFactory cmsDataFactory,
			SiteService siteService, SkinService skinService, IntegrationService integrationService)
    {
        super(logger, structureService, cmsDataFactory);
		this.siteService = siteService;
		this.skinService = skinService;
		this.integrationService = integrationService;
    }

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        fixResClass(coralSession, "integration.resource_class",
            new String[]
            {"view", "aggregationUpdateAction", "aggregationCopyAction", "relatedQuickAddView"});
        fixResClass(coralSession, "integration.component",
            new String[] {"componentName", "configurationView", "aggregationSourceView"});
        fixResClass(coralSession, "integration.screen",
            new String[] {"screenName", "configurationView"});
        System.out.println("Res class fix completed!");
		fixSkinEntries(coralSession);
        System.out.println("Skin entries fix completed!");
		fixIntegrationEntries(coralSession);
        System.out.println("Integration entries fix completed!");
        try
        {
            fixRelationshipsNode(coralSession);
            System.out.println("Relationship node fix completed!");
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to fix relationships node", e);
        }
    }

	
	private void fixIntegrationEntries(CoralSession coralSession)
	{
		ComponentResource comp = integrationService.getComponent(coralSession, "cms", "security.Login");
		comp.setConfigurationView(null);
		comp.update();
		ScreenResource screen = integrationService.getScreen(coralSession, "cms", "forum.Forum");
		screen.setConfigurationView(null);
		screen.update();
	}
	
    private void fixResClass(CoralSession coralSession, String resClassName,
        String[] attributeNames)
        throws ProcessingException
    {
        QueryResults results;
        try
        {
            results = coralSession.getQuery().
                executeQuery("FIND RESOURCE FROM "+resClassName);
        }
        catch(MalformedQueryException e)
        {
            throw new ProcessingException("cannot get '"+resClassName+"' resources", e);
        }
        Resource[] nodes = results.getArray(1);
        for(Resource node : nodes)
        {
            boolean update = false;
            for (String attrName : attributeNames)
            {
                AttributeDefinition attrDef = node.getResourceClass().getAttribute(attrName);
                if(node.isDefined(attrDef))
                {
                    String value = (String) node.get(attrDef);
                    if(value != null && value.indexOf(',') > 0)
                    {
                        value = value.replace(',','.');
                        try
                        {
                            node.set(attrDef, value);
                        }
                        catch(Exception e)
                        {
                            logger.error("FixToLedge: Could not update attribute '"+attrName
                                +"' for resource "+node.toString(), e);
                        }
                        update = true;
                    }
                }
            }
            
            if(update)
            {
                node.update();
            }
        }
    }
	
	private void fixSkinEntries(CoralSession coralSession)
	{
		SiteResource[] sites = siteService.getSites(coralSession);
		for(SiteResource site: sites)
		{
			Resource[] res = coralSession.getStore().getResource(site, "skins");
	        if(res.length > 0)
	        {
				res = coralSession.getStore().getResource(res[0]);
				for(Resource skin: res)
				{
					Resource[] nodes = coralSession.getStore().getResource(skin);
					for(Resource node: nodes)
					{
						fixSkinNode(coralSession, node);
					}
				}
	        }
		}
	}
	
	private void fixSkinNode(CoralSession coralSession, Resource node)
	{
		String name = node.getName(); 
		if(name.contains(","))
		{
			try
            {
                coralSession.getStore().setName(node, name.replace(",","."));
            }
            catch(InvalidResourceNameException e)
            {
                throw new RuntimeException("unexpected exception", e);
            }
		}
		Resource[] nodes = coralSession.getStore().getResource(node);
		for(Resource child: nodes)
		{
			fixSkinNode(coralSession, child);
		}
	}
    
    private void fixRelationshipsNode(CoralSession coralSession)
        throws Exception
    {
        SiteResource[] sites = siteService.getSites(coralSession);
        for(SiteResource site: sites)
        {
            deleteRelationshipsNode(coralSession, site);
        }
        sites = siteService.getTemplates(coralSession);
        for(SiteResource site: sites)
        {
            deleteRelationshipsNode(coralSession, site);
        }
    }
    
    private void deleteRelationshipsNode(CoralSession coralSession, SiteResource site)
        throws Exception
    {
        Resource[] res = coralSession.getStore().getResource(site, "relationships");
        for(Resource r: res)
        {
            coralSession.getStore().deleteResource(r);
        }
        res = coralSession.getStore().getResource(site, "related");
        for(Resource r: res)
        {
            coralSession.getStore().deleteResource(r);
        }
    }
    
    /**
     * @{inheritDoc}
     */
    public boolean checkAccessRights(Context context)
        throws Exception
    {
        return false;
    }
}
