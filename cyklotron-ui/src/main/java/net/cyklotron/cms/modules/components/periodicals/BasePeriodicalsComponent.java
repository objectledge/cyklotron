/*
 * Created on Nov 6, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.modules.components.periodicals;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import net.labeo.Labeo;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.table.NameComparator;
import net.labeo.services.table.PathTreeElement;
import net.labeo.services.table.PathTreeTableModel;
import net.labeo.services.table.TableColumn;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableService;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.CmsComponentData;
import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.modules.components.SkinableCMSComponent;
import net.cyklotron.cms.periodicals.PeriodicalRenderer;
import net.cyklotron.cms.periodicals.PeriodicalResource;
import net.cyklotron.cms.periodicals.PeriodicalsException;
import net.cyklotron.cms.periodicals.PeriodicalsService;
import net.cyklotron.cms.site.SiteResource;

/**
 * @author fil
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class BasePeriodicalsComponent
    extends SkinableCMSComponent
{
    protected PeriodicalsService periodicalsService;
    
    protected FilesService cmsFilesService;
    
    protected TableService tableService;
    
    public BasePeriodicalsComponent()
    {
        periodicalsService = (PeriodicalsService)Labeo.getBroker().
            getService(PeriodicalsService.SERVICE_NAME);
        cmsFilesService = (FilesService)Labeo.getBroker().
            getService(FilesService.SERVICE_NAME);
        tableService = (TableService)Labeo.getBroker().
            getService(TableService.SERVICE_NAME);
    }
    
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        prepareState(data, context);
    }
    
    protected abstract PeriodicalResource[] getPeriodicals(SiteResource site)
        throws ProcessingException;
        
    protected abstract String getSessionKey();
    
    public void prepareDefault(RunData data, Context context)
        throws ProcessingException
    {
        List periodicals = Arrays.asList(getPeriodicals(getSite(context)));
        Collections.sort(periodicals, new NameComparator(i18nContext.getLocale()()));
        templatingContext.put("periodicals", periodicals);
    }

    public void prepareDetails(RunData data, Context context)
        throws ProcessingException
    {
        PeriodicalResource periodical = getPeriodical(data);
        templatingContext.put("periodical", periodical);
        Resource[] children = coralSession.getStore().getResource(periodical.getStorePlace());
        PeriodicalRenderer renderer = periodicalsService.getRenderer(periodical.getRenderer());
        String suffix = renderer.getFilenameSuffix();
        periodicalsService.releaseRenderer(renderer);
        try
        {
            TableColumn column = new TableColumn("element", PathTreeElement.getComparator("name", i18nContext.getLocale()()));
            PathTreeTableModel model = new PathTreeTableModel(new TableColumn[] { column });
            model.bind("/", new PathTreeElement("archive","label"));
            Resource latest = null;
            for(int i = 0; i < children.length; i++)
            {
                Resource res = children[i];
                if(res instanceof FileResource && res.getName().endsWith(suffix))
                {
                    if(latest == null || latest.getCreationTime().compareTo(res.getCreationTime()) < 0)
                    {
                        latest = res;
                    }
                    bindIssue(model, (FileResource)res);
                }
            }
            if(latest != null)
            {
                templatingContext.put("latest", latest);
            }
            
            CmsComponentData componentData = getCmsData().getComponent();
            String key = "table:"+getSessionKey()+":"+periodical.getIdString()+
                ":"+componentData.getInstanceName();
            TableState state = tableService.getGlobalState(data, key);
            if(state.isNew())
            {
                state.setViewType(TableConstants.VIEW_AS_TREE);
                state.setRootId("0");
                state.setShowRoot(true);
                state.setExpanded("0");
                state.setPageSize(0);
                state.setMultiSelect(false);
                state.setSortColumnName("element");
            }
            templatingContext.put("table", new TableTool(state, model, null));
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to gather data", e);
        }
    }
    
    protected void bindIssue(PathTreeTableModel model, FileResource file)
    {
        Calendar cal = new GregorianCalendar();
        cal.setTime(file.getCreationTime());
        String name = Integer.toString(cal.get(Calendar.YEAR));
        String path = "/"+name;
        if(model.getObjectByPath(path) == null)
        {
            model.bind(path, new PathTreeElement(name, "year"));
        }
        name = Integer.toString(cal.get(Calendar.MONTH));
        path = path+"/"+name;
        if(model.getObjectByPath(path) == null)
        {
            model.bind(path, new PathTreeElement(name, "month"));
        }
        name = file.getName();
        path = path+"/"+name;
        PathTreeElement elm = new PathTreeElement(name, "item");
        elm.set("file", file);
        elm.set("date", file.getCreationTime());
        model.bind(path, elm);
    }
    
    protected PeriodicalResource getPeriodical(RunData data)
        throws ProcessingException
    {
    	CmsData cmsData = cmsDataFactory.getCmsData(context);
    	
        CmsComponentData componentData = cmsData.getComponent();
        String key = getSessionKey()+":"+getNode().getIdString()+
            ":"+componentData.getInstanceName();
        String ci = parameters.get("ci","");
        long periodicalId = -1;
        if(ci.equals(componentData.getInstanceName()))
        {
            periodicalId = parameters.getLong("periodical", -1);
            if(periodicalId > 0)
            {
                httpContext.setSessionAttribute(key, new Long(periodicalId));
            }
            else
            {
                httpContext.removeSessionAttribute(key);
            }
        }
        else
        {
            Long stored = (Long)data.getGlobalContext().getAttribute(key);
            if(stored != null)
            {
                periodicalId = stored.longValue();
            }
        }
        if(periodicalId > 0)
        {
            try
            {
                return (PeriodicalResource)coralSession.getStore().getResource(periodicalId);
            }
            catch(EntityDoesNotExistException e)
            {
                throw new ProcessingException("invalid peridical id", e);
            }
        }
        else
        {
			try
			{
	            String periodicalName = getConfiguration().get("periodical","");
	            if(periodicalName.length() > 0)
	            {
	                Resource[] res = coralSession.getStore().getResource(
	                	getPeriodicalRoot(cmsData.getSite()), periodicalName);
	                if(res.length > 0)
	                {
	                    return (PeriodicalResource)res[0];
	                }
	                else
	                {
	                    return null;
	                }
	            }
	            else
	            {
	                return null;
	            }
			}
			catch (PeriodicalsException e)
			{
				throw new ProcessingException("cannot get periodcals root", e);
			}
        }
    }
    
    public String getState(RunData data)
        throws ProcessingException
    {
        return getPeriodical(data) == null ? "Default" : "Details";
    }

	protected abstract Resource getPeriodicalRoot(SiteResource site)
	throws PeriodicalsException;
}
