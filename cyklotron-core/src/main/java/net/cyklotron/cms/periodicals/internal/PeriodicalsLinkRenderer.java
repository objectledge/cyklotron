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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;

import net.cyklotron.cms.documents.LinkRenderer;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.RootDirectoryResource;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;

public class PeriodicalsLinkRenderer
    implements LinkRenderer
{
    /** default server name used when no site alias is selected. */
    private final String serverName;
    
    /** server port to use. "80" by default. */
    private final int port;
    
    /** context name. "/" by default. */
    private final String context;
    
    /** servlet name and application bit. "ledge/" by default. */
    private final String servletAndApp;
    
    /** site service. */
    private final SiteService siteService;
    
    /** log */
    private final Logger log;        
    
    public PeriodicalsLinkRenderer(String serverName, int port, String context,
        String servletAndApp, SiteService siteService, Logger log)
    {
        this.serverName = serverName;
        this.port = port;
        this.context = context;
        this.servletAndApp = servletAndApp;
        this.siteService = siteService;
        this.log = log;
    }
    
    public String getFileURL(CoralSession coralSession, FileResource file)
    {
        Resource parent = file.getParent();
        while(parent != null && !(parent instanceof RootDirectoryResource))
        {
            parent = parent.getParent();
        }
        if(parent == null)
        {
            throw new IllegalStateException("cannot determine root directory");
        }
        RootDirectoryResource rootDirectory = ((RootDirectoryResource)parent);

        while(parent != null && !(parent instanceof SiteResource))
        {
            parent = parent.getParent();
        }
        if(parent == null)
        {
            throw new IllegalStateException("cannot determine site");
        }
        SiteResource site = (SiteResource)parent;
        
        if(rootDirectory.getExternal())
        {
            String path = "";
            for(parent = file; parent != null; parent = parent.getParent())
            {
                if(parent instanceof RootDirectoryResource)
                {
                    break;
                }
                else
                {
                    try
                    {
                        path = "/" + URLEncoder.encode(parent.getName(), "UTF-8") + path;
                    }
                    catch(UnsupportedEncodingException e)
                    {
                        throw new RuntimeException("really should not happen", e);
                    }
                }
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append(getContextURL(coralSession, site));
            sb.append("files/");
            sb.append(site.getName());
            sb.append("/");
            sb.append(rootDirectory.getName());
            sb.append(path);
            return sb.toString();
        }
        else
        {
            String path = "";
            for(parent = file; parent != null; parent = parent.getParent())
            {
                if(parent instanceof RootDirectoryResource)
                {
                    break;
                }
                else
                {
                    path = ","+parent.getName()+path;
                }
            }
            path = "/"+rootDirectory.getName()+path;

            StringBuilder sb = new StringBuilder();
            sb.append(getApplicationURL(coralSession, site));
            sb.append("view/files.Download?");
            sb.append("path=").append(path).append('&');
            sb.append("file_id=").append(file.getIdString());
            return sb.toString();
        }
    }
    
    public String getAbsoluteURL(CoralSession coralSession, SiteResource site, String path) 
    {
        return getContextURL(coralSession, site) + path;
    }
    
    public String getCommonResourceURL(CoralSession coralSession, SiteResource site, String path)
    {
        return getContextURL(coralSession, site) + "content/default/" + path;
    }
    
    public String getNodeURL(CoralSession coralSession, NavigationNodeResource node)
    {
        return getApplicationURL(coralSession, node.getSite())+"x/"+node.getIdString();
    }

    protected String getContextURL(CoralSession coralSession, SiteResource site)
    {
        StringBuilder buff = new StringBuilder();
        buff.append("http://")
            .append(getServer(coralSession, site));
        if(port != 80)
        {
            buff.append(':')
                .append(port);
        }
        buff.append(context);
        return buff.toString();
    }
    
    protected String getApplicationURL(CoralSession coralSession, SiteResource site)
    {
        StringBuilder buff = new StringBuilder();
        buff.append("http://")
            .append(getServer(coralSession, site));
        if(port != 80)
        {
            buff.append(':')
                .append(port);
        }
        buff.append(context)
            .append(servletAndApp);
        return buff.toString();
    }    

    protected String getServer(CoralSession coralSession, SiteResource site)
    {
        String server = null;
        try
        {
            server = siteService.getPrimaryMapping(coralSession, site);
        }
        catch(Exception e)
        {
            log.error("failed to deteremine site domain address", e);
        }
        if(server == null)
        {
            server = serverName;        
        }
        return server;
    }
}