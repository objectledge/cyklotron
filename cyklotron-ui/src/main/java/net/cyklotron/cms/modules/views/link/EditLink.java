package net.cyklotron.cms.modules.views.link;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.comparator.NameComparator;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsConstants;
import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.link.BaseLinkResource;
import net.cyklotron.cms.link.BaseLinkResourceImpl;
import net.cyklotron.cms.link.LinkService;
import net.cyklotron.cms.link.PoolResource;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.StructureException;
import net.cyklotron.cms.structure.StructureService;


/**
 *
 * @version $Id: EditLink.java,v 1.4 2005-02-10 17:50:15 rafal Exp $
 */
public class EditLink
    extends BaseLinkScreen
{


    public EditLink(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, LinkService linkService,
        StructureService structureService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, linkService,
                        structureService);
        // TODO Auto-generated constructor stub
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        CmsData cmsData = getCmsData();
        SiteResource site = cmsData.getSite();
        if(site == null)
        {
            site = cmsData.getGlobalComponentsDataSite();
        }
        if(site == null)
        {
            throw new ProcessingException("no site selected");
        }
        Boolean fromComponent = (Boolean)httpContext.getSessionAttribute(CmsConstants.FROM_COMPONENT);
        if(fromComponent != null && fromComponent.booleanValue())
        {
            templatingContext.put("from_component",fromComponent);
            templatingContext.put("component_node",(Long)httpContext.getSessionAttribute(CmsConstants.COMPONENT_NODE));
            templatingContext.put("component_instance",(String)httpContext.getSessionAttribute(CmsConstants.COMPONENT_INSTANCE));
        }
        templatingContext.put("data_site", site);
        try
        {
            templatingContext.put("data_site_root", structureService.getRootNode(coralSession, site));
        }
        catch(StructureException e)
        {
            throw new ProcessingException("failed to lookup site root node", e);
        }

        int lid = parameters.getInt("lid", -1);
        if(lid == -1)
        {
            throw new ProcessingException("Link id not found");
        }
        BaseLinkResource link = null;
        try
        {
            link = BaseLinkResourceImpl.getBaseLinkResource(coralSession, lid);
            templatingContext.put("linkResource",link);

            //calendar support
            Calendar startDate = Calendar.getInstance(i18nContext.getLocale());
            startDate.setTime(link.getStartDate());
            templatingContext.put("start_date",startDate);
            Calendar endDate = Calendar.getInstance(i18nContext.getLocale());
            endDate.setTime(link.getEndDate());
            templatingContext.put("end_date",endDate);
            List days = new ArrayList(31);
            for(int i = 1; i <=31; i++)
            {
                days.add(new Integer(i));
            }
            templatingContext.put("days",days);
            List months = new ArrayList(12);
            for(int i = 0; i <=11; i++)
            {
                months.add(new Integer(i));
            }
            templatingContext.put("months",months);
            List years = new ArrayList(20);
            for(int i = 2000; i <=2020; i++)
            {
                years.add(new Integer(i));
            }
            templatingContext.put("years",years);
            
            Resource[] resources = coralSession.getStore().getResource(link.getParent());
            List pools = new ArrayList();
            Map selectionMap = new HashMap();
            for(int i = 0; i < resources.length; i++)
            {
            	if(resources[i] instanceof PoolResource)
            	{
            		pools.add(resources[i]);
            		List links = ((PoolResource)resources[i]).getLinks();
            		selectionMap.put(resources[i], new Boolean(false));
            		if(links != null)
            		{
            			for(int j = 0; j < links.size(); j++)
            			{
            				if(link.equals(links.get(j)))
            				{
								selectionMap.put(resources[i], new Boolean(true));
								break;
            				}
            			}
            		}
            	}
            }
            Collections.sort(pools, new NameComparator(i18nContext.getLocale()));
            templatingContext.put("pools", pools);
            templatingContext.put("pools_map", selectionMap);
        }
        catch(Exception e)
        {
            throw new ProcessingException("Exception occurred", e);
        }
    }
}
