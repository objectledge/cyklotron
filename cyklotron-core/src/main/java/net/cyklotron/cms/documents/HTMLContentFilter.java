/*
 * Created on 2003-11-12
 */
package net.cyklotron.cms.documents;

import org.dom4j.Document;

/**
 * An interfeace defining HTML DOM filters. 
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: HTMLContentFilter.java,v 1.1 2005-01-12 20:44:39 pablo Exp $
 */
public interface HTMLContentFilter
{
	public Document filter(Document dom);
}
