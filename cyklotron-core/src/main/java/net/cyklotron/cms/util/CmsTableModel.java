package net.cyklotron.cms.util;

import java.util.Locale;

/**
 * Implementation of Table model for CMS resources
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CmsTableModel.java,v 1.2 2005-01-13 11:46:38 pablo Exp $
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
