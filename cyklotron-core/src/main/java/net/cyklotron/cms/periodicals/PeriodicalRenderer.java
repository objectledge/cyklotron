/*
 * Created on Oct 24, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.periodicals;

import java.util.Date;

import org.objectledge.coral.session.CoralSession;

import net.cyklotron.cms.files.FileResource;

/**
 * An utility class for rendering periodicals.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: PeriodicalRenderer.java,v 1.4 2005-03-08 10:49:53 pablo Exp $ 
 */
public interface PeriodicalRenderer
{
    /**
     * Prepares the renderer for rendering a periodical.
     * @param coralSession the coral session.
     * @param periodical the periodical.
     * @param time publication time.
     * 
     * @return <code>true</code> on success.
     */
    public boolean render(CoralSession coralSession, PeriodicalResource periodical, Date time, FileResource file);
    
    /**
     * Returns the suffix of the filename of the generated periodical.
     * 
     * @return the suffix.
     */
    public String getFilenameSuffix();
    
    /**
     * Returns the content type of the generated file.
     *
     * @return the content type of the generated file.
     */
    public String getMimeType();
    
    /**
     * Get the medium, for purpose of locating application provided templates.
     *
     * @return the medium, as defined by Labeo FinderService.
     */
    public String getMedium();
    
}
