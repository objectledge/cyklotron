package net.cyklotron.cms.modules.views.httpfeed;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.CoralTableModel;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableException;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.httpfeed.HttpFeedException;
import net.cyklotron.cms.httpfeed.HttpFeedService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;

/**
 * A list of feeds defined fo the site.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: FeedList.java,v 1.3 2005-01-26 09:00:32 pablo Exp $
 */
public class FeedList extends BaseHttpFeedScreen
{


    public FeedList(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, HttpFeedService httpFeedService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        httpFeedService);
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
            throw new ProcessingException("No site selected");
        }
        try
        {
            
            TableState state = tableStateManager.getState(context,
                                "cms.httpfeed.feedlist."+site.getName());
            if(state.isNew())
            {
                Resource feedsRoot = httpFeedService.getFeedsParent(coralSession, site);
                state.setRootId(feedsRoot.getIdString());

                state.setSortColumnName("name");
                state.setTreeView(false);
            }
        
            TableModel model = new CoralTableModel(coralSession, i18nContext.getLocale());
            templatingContext.put("table", new TableTool(state, null, model));
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
