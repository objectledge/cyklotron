package net.cyklotron.cms.util;

import java.util.Locale;

import net.cyklotron.cms.integration.IntegrationService;

import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.table.CoralTableModel;

/**
 * Implementation of Table model for CMS resources
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CmsTableModel.java,v 1.3 2005-01-19 08:24:15 pablo Exp $
 */
public class CmsTableModel extends CoralTableModel
{
    public CmsTableModel(CoralSession coralSession, Context context, 
        Locale locale, IntegrationService integrationService)
    {
        super(coralSession, locale);

        // add NavigationResource columns
        comparatorByColumnName.put("index.title", 
            new IndexTitleComparator(context, integrationService, locale));
    }
}
