package net.cyklotron.cms.modules.views.httpfeed;

import net.labeo.services.resource.Resource;
import net.labeo.services.resource.table.ARLTableModel;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableException;
import net.labeo.services.table.TableModel;
import net.labeo.services.table.TableService;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.httpfeed.HttpFeedException;
import net.cyklotron.cms.site.SiteResource;

/**
 * A list of feeds defined fo the site.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: FeedList.java,v 1.1 2005-01-24 04:34:18 pablo Exp $
 */
public class FeedList extends BaseHttpFeedScreen
{
    /** table service for feed list display. */
    TableService tableService = null;

    public FeedList()
    {
        tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
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
            throw new ProcessingException("No site selected");
        }
        try
        {
            
            TableState state = tableService.getLocalState(data,
                                "cms.httpfeed.feedlist."+site.getName());
            if(state.isNew())
            {
                Resource feedsRoot = httpFeedService.getFeedsParent(site);
                state.setRootId(feedsRoot.getIdString());

                state.setSortColumnName("name");
                state.setViewType(TableConstants.VIEW_AS_LIST);
            }
        
            TableModel model = new ARLTableModel(i18nContext.getLocale()());
            templatingContext.put("table", new TableTool(state, model, null));
        }
        catch(HttpFeedException e)
        {
            throw new ProcessingException("cannot get feeds root", e);
        }
        catch(TableException e)
        {
            throw new ProcessingException("table tool setup failed", e);
        }
    }
}
