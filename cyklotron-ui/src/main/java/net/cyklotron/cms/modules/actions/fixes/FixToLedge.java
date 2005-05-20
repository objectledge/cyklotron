package net.cyklotron.cms.modules.actions.fixes;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.query.MalformedQueryException;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: FixToLedge.java,v 1.3 2005-05-20 02:56:31 pablo Exp $
 */
public class FixToLedge
    extends BaseCMSAction
{
	SiteService siteService;
	
	SkinService skinService;
	
    public FixToLedge(Logger logger, StructureService structureService, CmsDataFactory cmsDataFactory,
			SiteService siteService, SkinService skinService)
    {
        super(logger, structureService, cmsDataFactory);
		this.siteService = siteService;
		this.skinService = skinService;
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
		fixSkinEntries(coralSession);
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
			coralSession.getStore().setName(node, name.replace(",","."));
		}
		Resource[] nodes = coralSession.getStore().getResource(node);
		for(Resource child: nodes)
		{
			fixSkinNode(coralSession, child);
		}
	}
}
