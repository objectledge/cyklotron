package net.cyklotron.cms.modules.views.files;

import net.cyklotron.cms.modules.views.aggregation.BaseAggregationSource;

/**
 * File import source screen.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: FileAggregationSource.java,v 1.1 2005-01-24 04:34:12 pablo Exp $
 */
public class FileAggregationSource
    extends BaseAggregationSource
{
    protected String getComponentClass(RunData data)
    {
        return "files,Files";
    }
}


