package net.cyklotron.cms.modules.components.syndication;

import java.util.ArrayList;
import java.util.List;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.components.SkinableCMSComponent;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.syndication.OutgoingFeedListConfiguration;
import net.cyklotron.cms.syndication.OutgoingFeedResource;
import net.cyklotron.cms.syndication.SyndicationService;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.CoralTableModel;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableException;
import org.objectledge.table.TableFilter;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.finders.MVCFinder;

/**
 * OutgoingFeedList component displays list of outgoing feeds.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: OutgoingFeedList.java,v 1.1 2005-06-16 11:14:16 zwierzem Exp $
 */

public class OutgoingFeedList extends SkinableCMSComponent
{
    private SyndicationService syndicationService;
    private TableStateManager tableStateManager;

    public OutgoingFeedList(org.objectledge.context.Context context, Logger logger,
        Templating templating, CmsDataFactory cmsDataFactory,
        SkinService skinService, MVCFinder mvcFinder, SyndicationService syndicationService,
        TableStateManager tableService)
    {
        super(context, logger, templating, cmsDataFactory,skinService, mvcFinder);
        this.syndicationService = syndicationService;
        this.tableStateManager = tableService;
    }

    @Override
    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession)
    throws org.objectledge.pipeline.ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        
        if(cmsData.getSite() == null)
        {
            componentError(context, "No site selected");
            return;
        }

        Resource parent = null;
        try
        {
            parent = syndicationService.getOutgoingFeedsManager().getFeedsParent(
                coralSession, cmsData.getSite());
        }
        catch(Exception e)
        {
            componentError(context, "No feeds parent in site");
            return;
        }

        Parameters componentConfig = getConfiguration();
        OutgoingFeedListConfiguration config = new OutgoingFeedListConfiguration(componentConfig);
        try
        {
            TableState state = tableStateManager.getState(context,
                                this.getClass().getName()+"."+cmsData.getNode().getIdString());
            if(state.isNew())
            {
                state.setRootId(parent.getIdString());
                state.setTreeView(false);
            }
            state.setSortColumnName(config.getSortColumn());
            state.setAscSort(config.getAscSortDir());
        
            TableModel model = new CoralTableModel(coralSession, i18nContext.getLocale());
            List filters = new ArrayList();
            filters.add(new TableFilter(){
                public boolean accept(Object object)
                {
                    if(object instanceof OutgoingFeedResource)
                    {
                        OutgoingFeedResource f = (OutgoingFeedResource) object;
                        return f.getPublic();
                    }
                    return false;
                }
            });
            templatingContext.put("table", new TableTool(state, filters, model));
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
}
