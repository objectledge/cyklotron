package net.cyklotron.cms.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.cyklotron.cms.documents.LinkRenderer;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.RootDirectoryResource;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.ComponentInitializationError;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.web.WebConfigurator;
import org.objectledge.web.mvc.tools.LinkTool;

/**
 * A service for rendering of links in offline (no HttpRequest) context.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: OfflineLinkRenderingService.java,v 1.2 2005-06-16 13:51:33 zwierzem Exp $
 */
public class OfflineLinkRenderingService 
{
    // constants ////////////////////////////////////////////////////////////
    
    /** port default value. */
    public static final int PORT_DEFAULT = 80;
    
    /** context parameter default value. */
    public static final String CONTEXT_DEFAULT = "/";
    
    /** servletAndApp defaault value. */
    public static final String SERVLET_DEFAULT = "ledge/";
    
    // instance variables ///////////////////////////////////////////////////
    
    private Logger log;
    private SiteService siteService;
    private WebConfigurator webConfigurator;
    
    /** default server name used when no site alias is selected. */
    private String serverName;

    /** server port to use. "80" by default. */
    private int port;
    
    /** context name. "/" by default. */
    private String context;
    
    /** servlet name. "ledge/" by default. */
    private String servletAndApp;

    public OfflineLinkRenderingService(Logger log, Configuration config,
        SiteService siteService, WebConfigurator webConfigurator)
    {
        this.log = log;
        this.siteService = siteService;
        this.webConfigurator = webConfigurator;

        try
        {
            serverName = config.getChild("server").getValue(); // ! no default
        }
        catch(ConfigurationException e)
        {
            throw new ComponentInitializationError("failed to configure the component", e);
        } 
        port = config.getChild("port").getValueAsInteger(PORT_DEFAULT);
        context = config.getChild("context").getValue(CONTEXT_DEFAULT);
        servletAndApp = config.getChild("servlet").getValue(SERVLET_DEFAULT);
    }

    // lame link tool ///////////////////////////////////////////////////////

    public LinkRenderer getLinkRenderer()
    {
        return new LinkRenderer()
        {
            public String getFileURL(CoralSession coralSession, FileResource file)
            {
                return OfflineLinkRenderingService.this.getFileURL(coralSession, file);
            }

			public String getCommonResourceURL(CoralSession coralSession, SiteResource site, String path)
			{
				return OfflineLinkRenderingService.this.getCommonResourceURL(coralSession, site, path);
			}

			public String getAbsoluteURL(CoralSession coralSession, SiteResource site, String path)
			{
                return OfflineLinkRenderingService.this.getAbsoluteURL(coralSession, site, path);
			}

            public String getNodeURL(CoralSession coralSession, NavigationNodeResource node)
            {
                return OfflineLinkRenderingService.this.getNodeURL(coralSession, node);
            }
        };
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
                        path = "/"+URLEncoder.encode(parent.getName(), LinkTool.PARAMETER_ENCODING)+path;
                    }
                    catch(UnsupportedEncodingException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
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
            sb.append("view/files,Download?");
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
    
    
    public String getViewURL(CoralSession coralSession, SiteResource site,
        String view,
        Parameters pathinfoParameters, Parameters queryStringParameters)
    {
        StringBuilder buff = new StringBuilder();
        String queryStringSep = "&";

        buff.append(getApplicationURL(coralSession, site));
        if(view != null && !view.equals(""))
        {
            buff.append('/').append(webConfigurator.getViewToken());
            buff.append('/').append(view);
        }

        
        List pathInfoParameterNames = new ArrayList();
        if(pathinfoParameters != null)
        {
            pathInfoParameterNames = Arrays.asList(pathinfoParameters.getParameterNames());
            Collections.sort(pathInfoParameterNames);
        }

        List queryParameterNames = new ArrayList();
        if(queryStringParameters != null)
        {
            queryParameterNames = Arrays.asList(queryStringParameters.getParameterNames());
            Collections.sort(queryParameterNames);
        }

        for(int i=0; i<pathInfoParameterNames.size(); i++)
        {
            String name = (String)pathInfoParameterNames.get(i);
            String[] values = pathinfoParameters.getStrings(name);
            for(int j=0; j<values.length; j++)
            {
                try
                {
                    buff.append('/').append(URLEncoder.encode(name, LinkTool.PARAMETER_ENCODING));
                    buff.append('/').append(URLEncoder.encode(values[j], LinkTool.PARAMETER_ENCODING));
                }
                catch (UnsupportedEncodingException e)
                {
                    throw new RuntimeException("Unknown encoding for links", e);
                }
            }
        }

        if(queryParameterNames.size() > 0)
        {
            buff.append('?');
            for(int i=0; i<queryParameterNames.size(); i++)
            {
                String name = (String)queryParameterNames.get(i);
                String[] values = queryStringParameters.getStrings(name);
                for(int j=0; j<values.length; j++)
                {
                    try
                    {
                        buff.append(URLEncoder.encode(name, LinkTool.PARAMETER_ENCODING))
                            .append('=')
                            .append(URLEncoder.encode(values[j], LinkTool.PARAMETER_ENCODING));
                    }
                    catch (UnsupportedEncodingException e)
                    {
                        throw new RuntimeException("Unknown encoding for links", e);
                    }
                    if(j<values.length-1)
                    {
                        buff.append(queryStringSep);
                    }
                }
                if(i<queryParameterNames.size()-1)
                {
                    buff.append(queryStringSep);
                }
            }
        }
        return buff.toString();
    }
}
