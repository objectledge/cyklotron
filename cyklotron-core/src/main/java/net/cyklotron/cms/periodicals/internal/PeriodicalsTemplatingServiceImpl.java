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

package net.cyklotron.cms.periodicals.internal;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.objectledge.encodings.HTMLEntityEncoder;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.i18n.I18n;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.Template;
import org.objectledge.templating.TemplateNotFoundException;
import org.objectledge.templating.Templating;
import org.objectledge.utils.StringUtils;

import net.cyklotron.cms.periodicals.PeriodicalRenderer;
import net.cyklotron.cms.periodicals.PeriodicalsException;
import net.cyklotron.cms.periodicals.PeriodicalsTemplatingService;
import net.cyklotron.cms.site.SiteResource;

/**
 *
 *
 * @author <a href="rafal@caltha.pl">Rafał Krzewski</a>
 * @version $Id: PeriodicalsTemplatingServiceImpl.java,v 1.2 2006-05-05 08:22:09 rafal Exp $
 */
public class PeriodicalsTemplatingServiceImpl implements PeriodicalsTemplatingService
{
    private final FileSystem fileSystem;
    private final Templating templating;
    private final I18n i18n;
    private final String templateEncoding;    

    public PeriodicalsTemplatingServiceImpl(FileSystem fileSystem, Templating templating, 
        I18n i18n)
    {
        this.fileSystem = fileSystem;
        this.templating = templating;
        this.i18n = i18n;        
        this.templateEncoding = templating.getTemplateEncoding();
    }
    
    // template variants ////////////////////////////////////////////////////
    
    // inherit doc
    public String[] getTemplateVariants(SiteResource site, String renderer)
        throws PeriodicalsException
    {
        try
        {
            String dir = "/templates/sites/"+site.getName()+
                "/messages/periodicals/"+renderer;
            if(!fileSystem.exists(dir))
            {
                return new String[0];
            }
            String[] items = fileSystem.list(dir);
            if(items == null || items.length == 0)
            {
                return new String[0];
            }
            ArrayList temp = new ArrayList();
            for (int i = 0; i < items.length; i++)
            {
                String path = dir+"/"+items[i];
                if(fileSystem.isFile(path) && path.endsWith(".vt"))
                {
                    temp.add(items[i].substring(0, items[i].length()-3));
                }
            }
            String[] result = new String[temp.size()];
            temp.toArray(result);
            return result;
        }
        catch(Exception e)
        {
            throw new PeriodicalsException("exception occured", e);
        }
    }

    // inherit doc
    public boolean hasTemplateVariant(SiteResource site, String renderer, String name)
    {
        String path = getTemplateVariantPath(site, renderer, name);
        return fileSystem.exists(path);
    }

    // inherit doc
    public Template getTemplateVariant(SiteResource site, String renderer, String name)
        throws TemplateNotFoundException
    {
        String path = "/sites/"+site.getName()+"/messages/periodicals/"+renderer+"/"+name;
        return templating.getTemplate(path);
    }

    // inherit doc
    public void createTemplateVariant(SiteResource site, String renderer, String name, String contents)
        throws ProcessingException
    {
        String path = getTemplateVariantPath(site, renderer, name);
        if(fileSystem.exists(path))
        {
            throw new ProcessingException("variant "+name+" of "+renderer+
                " render already exists in site "+site.getName());
        }
        
        writeTemplate(path, contents, "failed to write template contents");
        invalidateTemplate(site, renderer, name);
    }

    // inherit doc
    public void deleteTemplateVariant(SiteResource site, String renderer, String name)
        throws ProcessingException
    {
        String path = getTemplateVariantPath(site, renderer, name);
        if(!fileSystem.exists(path))
        {
            throw new ProcessingException("variant "+name+" of "+renderer+
                " render does not exist in site "+site.getName());
        }
        try
        {
            fileSystem.delete(path);
            invalidateTemplate(site, renderer, name);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to delete template", e);
        }
    }

    private static final String DEFAULT_TEMPLATE_FILE = "/templates/messages/periodicals/%s_%s.vt";
    
    private static final String DEFAULT_TEMPLATE = "/messages/periodicals/%s_%s";
    
    public List getDefaultTemplateLocales(String renderer)
        throws ProcessingException
    {
        List list = new ArrayList();
        for (Locale locale : i18n.getSupportedLocales())
        {
            if(fileSystem.exists(String.format(DEFAULT_TEMPLATE_FILE, renderer, locale.toString())))
            {
                list.add(locale);
            }
        }
        return list;
    }

    public String getDefaultTemplateContents(String renderer, Locale locale)
        throws ProcessingException
    {
        try
        {
            return fileSystem.read(String.format(DEFAULT_TEMPLATE_FILE, renderer, locale.toString()), templateEncoding);            
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to read template contents for renderer "+
                renderer+"_"+locale.toString(), e);
        }
    }

    public Template getDefaultTemplate(String renderer, Locale locale)
        throws ProcessingException
    {
        try
        {
            return templating.getTemplate(String.format(DEFAULT_TEMPLATE, renderer, locale.toString()));
        }
        catch(TemplateNotFoundException e)
        {
            return null;
        }
    }

    // inherit doc
    public String getTemplateVariantContents(SiteResource site, String renderer, String name)
        throws ProcessingException
    {
        String path = getTemplateVariantPath(site, renderer, name);
        if(!fileSystem.exists(path))
        {
            throw new ProcessingException("variant "+name+" of "+renderer+
                " render does not exist in site "+site.getName());
        }
        try
        {
            return fileSystem.read(path, templateEncoding);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to read template contents", e);
        }
    }

    // inherit doc
    public void getTemplateVariantContents(SiteResource site, String renderer, String name, OutputStream out)
        throws ProcessingException
    {
        String path = getTemplateVariantPath(site, renderer, name);
        if(!fileSystem.exists(path))
        {
            throw new ProcessingException("variant "+name+" of "+renderer+
                " render does not exist in site "+site.getName());
        }
        try
        {
            fileSystem.read(path,out);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to read template contents", e);
        }
    }

    // inherit doc
    public long getTemplateVariantLength(SiteResource site, String renderer, String name)
        throws ProcessingException
    {
        String path = getTemplateVariantPath(site, renderer, name);
        if(!fileSystem.exists(path))
        {
            throw new ProcessingException("variant "+name+" of "+renderer+
                " render does not exist in site "+site.getName());
        }
        return fileSystem.length(path);
    }

    // inherit doc
    public void setTemplateVariantContents(SiteResource site, String renderer, String name, String contents)
        throws ProcessingException
    {
        String path = getTemplateVariantPath(site, renderer, name);
        if(!fileSystem.exists(path))
        {
            throw new ProcessingException("variant "+name+" of "+renderer+
                " render does not exist in site "+site.getName());
        }
        writeTemplate(path, contents, "failed to write template contents");
        invalidateTemplate(site, renderer, name);
    }

    // inherit doc
    protected String getTemplateVariantPath(SiteResource site, String renderer, String variant)
    {
        return "/templates/sites/"+site.getName()+"/messages/periodicals/"+renderer+
            "/"+variant+".vt";    
    }

    protected void invalidateTemplate(SiteResource site, String renderer, String variant)
    {
        String name = "/sites/"+site.getName()+"/messages/periodicals/"+renderer+
            "/"+variant;
        templating.invalidateTemplate(name);
    }

    private void writeTemplate(String path, String contents, String message)
        throws ProcessingException
    {
        try
        {
            HTMLEntityEncoder encoder = new HTMLEntityEncoder();
            if(!fileSystem.exists(path))
            {
                fileSystem.mkdirs(StringUtils.directoryPath(path));
            }
            fileSystem.write(path,
                encoder.encodeHTML(contents, templateEncoding), templateEncoding);
        }
        catch(Exception e)
        {
            throw new ProcessingException(message, e);
        }
    }
}
