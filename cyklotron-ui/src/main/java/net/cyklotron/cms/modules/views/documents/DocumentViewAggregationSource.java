package net.cyklotron.cms.modules.views.documents;

import net.cyklotron.cms.modules.views.aggregation.BaseAggregationSource;

/**
 * The aggregation source screen for documents.
 *
 * @author <a href="mailto:zwierzem@caltha.pl">Damian Gajda</a>
 * @version $Id: DocumentViewAggregationSource.java,v 1.1 2005-01-24 04:34:59 pablo Exp $
 */
public class DocumentViewAggregationSource
extends BaseAggregationSource
{
    protected String getComponentClass(RunData data)
    {
        return "documents,DocumentView";
    }
}
