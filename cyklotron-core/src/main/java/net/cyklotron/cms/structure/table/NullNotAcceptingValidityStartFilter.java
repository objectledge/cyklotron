package net.cyklotron.cms.structure.table;

import java.util.Date;

/**
 * A Validity start filter that does not accept null validity start date values.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: NullNotAcceptingValidityStartFilter.java,v 1.1 2005-01-12 20:44:55 pablo Exp $
 */
public class NullNotAcceptingValidityStartFilter extends ValidityStartFilter
{
    public NullNotAcceptingValidityStartFilter(Date start, Date end)
    {
        super(start, end);
    }

	/** Does not accept resources with null validity start attribute. */ 
	protected boolean acceptNullDate()
	{
		return false;
	}
}
