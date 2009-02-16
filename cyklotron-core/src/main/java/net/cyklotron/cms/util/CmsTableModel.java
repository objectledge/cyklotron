package net.cyklotron.cms.util;

import java.util.Locale;

import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.table.CoralTableModel;

import net.cyklotron.cms.integration.IntegrationService;

/**
 * Implementation of Table model for CMS resources
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CmsTableModel.java,v 1.4 2005-02-09 22:20:08 rafal Exp $
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
