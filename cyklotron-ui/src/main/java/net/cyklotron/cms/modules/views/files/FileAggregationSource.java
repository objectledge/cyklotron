package net.cyklotron.cms.modules.views.files;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.table.TableStateManager;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.views.aggregation.BaseAggregationSource;
import net.cyklotron.cms.preferences.PreferencesService;

/**
 * File import source screen.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: FileAggregationSource.java,v 1.2 2005-01-26 09:00:34 pablo Exp $
 */
public class FileAggregationSource
    extends BaseAggregationSource
{
    public FileAggregationSource(Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory, MVCFinder mvcFinder,
        TableStateManager tableStateManager)
    {
        super(context, logger, preferencesService, cmsDataFactory, mvcFinder, tableStateManager);
        // TODO Auto-generated constructor stub
    }
    protected String getComponentClass()
    {
        return "files,Files";
    }
}


