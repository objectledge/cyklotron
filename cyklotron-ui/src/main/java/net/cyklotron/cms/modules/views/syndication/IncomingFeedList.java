package net.cyklotron.cms.modules.views.syndication;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.syndication.SyndicationService;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
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

/**
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: IncomingFeedList.java,v 1.1 2005-06-16 11:14:14 zwierzem Exp $
 */
public class IncomingFeedList extends BaseSyndicationScreen
{
    public IncomingFeedList(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        SyndicationService syndicationService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        syndicationService);
    }

    @Override
    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession) throws org.objectledge.pipeline.ProcessingException
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
                this.getClass().getName()+"."+site.getName());
            if(state.isNew())
            {
                Resource feedsRoot = 
                    syndicationService.getIncomingFeedsManager().getFeedsParent(coralSession, site);
                state.setRootId(feedsRoot.getIdString());

                state.setSortColumnName("name");
                state.setTreeView(false);
            }
        
            TableModel model = new CoralTableModel(coralSession, i18nContext.getLocale());
            templatingContext.put("table", new TableTool(state, null, model));
        }
        catch(TableException e)
        {
            throw new ProcessingException("table tool setup failed", e);
        }
        catch(Exception e)
        //catch(TooManySyndicationRootsException e)
        //catch(CannotCreateSyndicationRootException e)
        //catch(TooManyFeedsRootsException e)
        //catch(CannotCreateFeedsRootException e)
        {
            throw new ProcessingException("cannot get feeds root", e);
        }
    }
    
    public boolean checkAccessRights(Context context)
    throws ProcessingException
    {
        SiteResource site = getSite();
        return getCoralSession(context).getUserSubject().hasRole(site.getTeamMember());
    }
}
