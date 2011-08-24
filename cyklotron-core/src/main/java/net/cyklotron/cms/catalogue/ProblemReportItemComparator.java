package net.cyklotron.cms.catalogue;

import java.util.Locale;

import org.objectledge.table.comparator.BaseStringComparator;

public class ProblemReportItemComparator
    extends BaseStringComparator<ProblemReportItem>
{
    public ProblemReportItemComparator(Locale locale)
    {
        super(locale);
    }

    @Override
    public int compare(ProblemReportItem o1, ProblemReportItem o2)
    {
        return compareStrings(o1.getResource().getPath(), o2.getResource().getPath());
    }
}