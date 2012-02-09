package net.cyklotron.cms.modules.views.syndication;

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

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.syndication.SyndicationService;

/**
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: OutgoingFeedList.java,v 1.3 2007-11-18 21:24:50 rafal Exp $
 */
public class OutgoingFeedList extends BaseSyndicationScreen
{
   public OutgoingFeedList(Context context, Logger logger, PreferencesService preferencesService,
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
                    syndicationService.getOutgoingFeedsManager().getFeedsParent(coralSession, site);
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
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        if(!cmsData.isApplicationEnabled("syndication"))
        {
            logger.debug("Application 'syndication' not enabled in site");
            return false;
        }
        SiteResource site = getSite();
        return getCoralSession(context).getUserSubject().hasRole(site.getTeamMember());
    }
}
