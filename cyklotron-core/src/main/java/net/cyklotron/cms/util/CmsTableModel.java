package net.cyklotron.cms.util;

import java.util.Locale;

import net.labeo.services.resource.table.ARLTableModel;

/**
 * Implementation of Table model for CMS resources
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CmsTableModel.java,v 1.1 2005-01-12 20:44:32 pablo Exp $
 */
public class CmsTableModel extends ARLTableModel
{
    public CmsTableModel(Locale locale)
    {
        super(locale);

        // add NavigationResource columns
        comparatorByColumnName.put("index.title", new IndexTitleComparator(locale));
    }
}
