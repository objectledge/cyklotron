// 
// Copyright (c) 2003-2005, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
//   
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//   
// * Redistributions of source code must retain the above copyright notice,  
// this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
// this list of conditions and the following disclaimer in the documentation  
// and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
// nor the names of its contributors may be used to endorse or promote products  
// derived from this software without specific prior written permission. 
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
// IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,  
// INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  
// BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
// OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  
// WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  
// POSSIBILITY OF SUCH DAMAGE. 
//

package net.cyklotron.cms.periodicals;

import java.io.OutputStream;
import java.util.List;
import java.util.Locale;

import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.Template;
import org.objectledge.templating.TemplateNotFoundException;

import net.cyklotron.cms.site.SiteResource;

/**
 * @author <a href="rafal@caltha.pl">Rafał Krzewski</a>
 * @version $Id: PeriodicalsTemplatingService.java,v 1.2 2006-05-05 08:22:04 rafal Exp $
 */
public interface PeriodicalsTemplatingService
{
    // template variants ////////////////////////////////////////////////////

    /**
     * Returns the names of the template variants defined for a specific renderer.
     * 
     * @param site the site.
     * @param renderer the renderer.
     * @return the names of the template variants defined for a specific renderer.
     */
    public String[] getTemplateVariants(SiteResource site, String renderer)
        throws PeriodicalsException;

    /**
     * Checks if the specified variant of the renderer's template exists in the site.
     * 
     * @param site the site.
     * @param renderer the renderer.
     * @param name the varaint's name.
     * @return <code>true</code> if the specified variant of the renderer's template exists in the
     *         site.
     */
    public boolean hasTemplateVariant(SiteResource site, String renderer, String name);

    /**
     * Returns a specific renderer's template variant.
     * 
     * @param site the site.
     * @param renderer the renderer.
     * @param name the variant's name.
     * @return the variant's template.
     */
    public Template getTemplateVariant(SiteResource site, String renderer, String name)
        throws TemplateNotFoundException, PeriodicalsException;

    /**
     * Creates a new variant of the renderer's template.
     * 
     * @param site the site.
     * @param renderer the renderer.
     * @param name the variant's name.
     * @param contents the template contents.
     */
    public void createTemplateVariant(SiteResource site, String renderer, String name,
        String contents)
        throws ProcessingException;

    /**
     * Deletes a renderer's template variant.
     * 
     * @param site the site.
     * @param renderer the renderer.
     * @param name the variant's name.
     */
    public void deleteTemplateVariant(SiteResource site, String renderer, String name)
        throws ProcessingException;

    /**
     * Returns a list of Loacales in which the application provides a template for the specified
     * renderer.
     * 
     * @param renderer the renderer
     * @return a list of Locale objects.
     */
    public List<Locale> getDefaultTemplateLocales(String renderer)
        throws ProcessingException;

    /**
     * Returns the contetns of the application provided template.
     * 
     * @param renderer the renderer.
     * @param locale the locale.
     * @return the contents of the template.
     */
    public String getDefaultTemplateContents(String renderer, Locale locale)
        throws ProcessingException;

    /**
     * Returns the application provided template.
     * 
     * @param renderer the renderer.
     * @param locale the locale.
     * @return the template, or <code>null</code> if not avaialable.
     */
    public Template getDefaultTemplate(String renderer, Locale locale)
        throws ProcessingException;

    /**
     * Returns the contents of the renderer's template variant as a String.
     * 
     * @param site the site.
     * @param renderer the renderer.
     * @param name the variant's name.
     * @return the contents of the renderer's template variant as a String.
     */
    public String getTemplateVariantContents(SiteResource site, String renderer, String name)
        throws ProcessingException;

    /**
     * Writes the contnets of the renderer's template variant into a provided OutputStream.
     * 
     * @param site the site.
     * @param renderer the renderer.
     * @param name the variant's name.
     * @param out the stream to write contents to.
     */
    public void getTemplateVariantContents(SiteResource site, String renderer, String name,
        OutputStream out)
        throws ProcessingException;

    /**
     * Returns the length (byte count) of the renderer's template contents.
     * 
     * @param site the site.
     * @param renderer the renderer.
     * @param name the variant's name.
     * @return the length (byte count) of the renderer's template contents.
     */
    public long getTemplateVariantLength(SiteResource site, String renderer, String name)
        throws ProcessingException;

    /**
     * Sets the contents of a renderer's template variant.
     * 
     * @param site the site.
     * @param renderer the renderer.
     * @param name the variant's name.
     * @param contents the contents of the template.
     */
    public void setTemplateVariantContents(SiteResource site, String renderer, String name,
        String contents)
        throws ProcessingException;

    // confirmation ticket templates

    /**
     * Returns a list of Loacales in which the application provides a confirmation ticket template.
     * 
     * @return list of Locale objects.
     */
    public List<Locale> getDefaultConfirmationTicketTemplateLocales();

    /**
     * Returns a default confirmation ticket template for a given locale.
     * 
     * @param locale the locale.
     * @return the template, or null when no default template is not defined for specified locale.
     * @throws ProcessingException
     */
    public Template getDefaultConfirmationTicketTemplate(Locale locale)
        throws ProcessingException;

    /**
     * Returns the contents of default confirmation ticket template for a given locale.
     * 
     * @param locale the locale.
     * @return template contents, or null when no default template is not defined for specified
     *         locale.
     * @throws ProcessingException
     */
    public String getDefaultConfirmationTicketTemplateContents(Locale locale)
        throws ProcessingException;

    /**
     * Returns the variants of confirmation ticket template defined in the site.
     * 
     * @param site the site
     * @return List of variant names.
     * @throws PeriodicalsException
     */
    public List<String> getConfirmationTicketTemplateVariants(SiteResource site)
        throws PeriodicalsException;

    /**
     * Deletes a confirmation ticket template variant.
     * 
     * @param site
     * @param variant
     * @throws ProcessingException
     */
    public void deleteConfirmationTicketTemplateVariant(SiteResource site, String variant)
        throws ProcessingException;

    /**
     * Returns a confirmation ticket template.
     * 
     * @param site the site.
     * @param variant template variant.
     * @return the template, or null when no such variant exists.
     * @throws ProcessingException
     */
    public Template getConfirmationTicketTemplate(SiteResource site, String variant)
        throws ProcessingException;

    /**
     * Returns the contents of a confirmation ticket template.
     * 
     * @param site the site.
     * @param variant template variant.
     * @return template contents, or null when no such variant exists.
     * @throws ProcessingException
     */
    public String getConfirmationTicketTemplateContents(SiteResource site, String variant)
        throws ProcessingException;

    /**
     * Sets the contents of a confirmation ticket template.
     * <p>
     * If no variant by that name exists, new variant is created. Otherwise old contents are
     * replaced.
     * </p>
     * 
     * @param site the site.
     * @param variant template variant.
     * @param contents template contents.
     * @throws ProcessingException
     */
    public void setConfirmationTicketTemplateContents(SiteResource site, String variant,
        String contents) throws ProcessingException;
}
