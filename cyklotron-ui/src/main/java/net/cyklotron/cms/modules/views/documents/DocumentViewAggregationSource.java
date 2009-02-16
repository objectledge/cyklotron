package net.cyklotron.cms.modules.views.documents;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.table.TableStateManager;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.views.aggregation.BaseAggregationSource;
import net.cyklotron.cms.preferences.PreferencesService;

/**
 * The aggregation source screen for documents.
 *
 * @author <a href="mailto:zwierzem@caltha.pl">Damian Gajda</a>
 * @version $Id: DocumentViewAggregationSource.java,v 1.3 2005-03-08 11:02:25 pablo Exp $
 */
public class DocumentViewAggregationSource
extends BaseAggregationSource
{
    
    
    public DocumentViewAggregationSource(Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory, MVCFinder mvcFinder,
        TableStateManager tableStateManager)
    {
        super(context, logger, preferencesService, cmsDataFactory, mvcFinder, tableStateManager);
        
    }
    protected String getComponentClass()
    {
        return "documents,DocumentView";
    }
}
