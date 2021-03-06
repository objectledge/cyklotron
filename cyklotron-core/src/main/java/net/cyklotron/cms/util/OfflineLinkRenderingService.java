package net.cyklotron.cms.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.ComponentInitializationError;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.web.WebConfigurator;
import org.objectledge.web.mvc.tools.LinkTool;

import net.cyklotron.cms.documents.LinkRenderer;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.RootDirectoryResource;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * A service for rendering of links in offline (no HttpRequest) context.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: OfflineLinkRenderingService.java,v 1.5 2007-11-18 21:23:14 rafal Exp $
 */
public class OfflineLinkRenderingService 
{
    // constants ////////////////////////////////////////////////////////////
    
    /** HTTP port default value. */
    public static final int HTTP_PORT_DEFAULT = 80;

    /** HTTPS port default value. */
    public static final int HTTPS_PORT_DEFAULT = 443;
    
    /** context parameter default value. */
    public static final String CONTEXT_DEFAULT = "/";
    
    /** servletAndApp default value. */
    public static final String SERVLET_DEFAULT = "";
    
    // instance variables ///////////////////////////////////////////////////
    
    private Logger log;
    private SiteService siteService;
    private WebConfigurator webConfigurator;
    
    /** default server name used when no site alias is selected. */
    private String serverName;

    /** server port to use. "80" by default. */
    private int httpPort;

    /** server port to use, when site requires secure channel. "443" by default. */
    private int httpsPort;
    
    /** context name. "/" by default. */
    private String context;
    
    /** servlet name. Empty by default. */
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
        httpPort = config.getChild("port").getValueAsInteger(HTTP_PORT_DEFAULT);
        httpsPort = config.getChild("securePort").getValueAsInteger(HTTPS_PORT_DEFAULT);
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
            return buildPath(getContextURL(coralSession, site), "files", site.getName(),
                rootDirectory.getName(), path);
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
                    path = "," + parent.getName() + path;
                }
            }
            path = "/" + rootDirectory.getName() + path;
            return buildPath(getApplicationURL(coralSession, site), "view/files.Download?path="
                + path + "&file_id=" + file.getIdString());
        }
    }
    
    public String getAbsoluteURL(CoralSession coralSession, SiteResource site, String path) 
    {
        return buildPath(getContextURL(coralSession, site), path);
    }
    
    public String getCommonResourceURL(CoralSession coralSession, SiteResource site, String path)
    {
        return buildPath(getContextURL(coralSession, site), "content/default", path);
    }
    
    public String getNodeURL(CoralSession coralSession, NavigationNodeResource node)
    {
        if(node.getQuickPath() == null)
        {
            return buildPath(getApplicationURL(coralSession, node.getSite()), "x", node.getIdString());
        }
        else
        {
            return buildPath(getContextURL(coralSession, node.getSite()), node.getQuickPath());
        }
    }

    protected String getContextURL(CoralSession coralSession, SiteResource site)
    {
        StringBuilder buff = new StringBuilder();
        final boolean secure = site.getRequiresSecureChannel();
        buff.append(secure ? "https" : "http").append(":");
        buff.append("//").append(getServer(coralSession, site));
        if(!secure && httpPort != HTTP_PORT_DEFAULT)
        {
            buff.append(':').append(httpPort);
        }
        if(secure && httpsPort != HTTPS_PORT_DEFAULT)
        {
            buff.append(':').append(httpsPort);
        }
        buff.append(context);
        return buff.toString();
    }
    
    public String getApplicationURL(CoralSession coralSession, SiteResource site)
    {
        return buildPath(getContextURL(coralSession, site), servletAndApp);
    }    
    
    public String getApplicationURL(CoralSession coralSession, SiteResource site, String suffix)
    {
        return buildPath(getContextURL(coralSession, site), servletAndApp, suffix);
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
    
    
    public String getViewURL(CoralSession coralSession, SiteResource site, String view,
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

        
        List<String> pathInfoParameterNames = new ArrayList<>();
        if(pathinfoParameters != null)
        {
            pathInfoParameterNames = Arrays.asList(pathinfoParameters.getParameterNames());
            Collections.sort(pathInfoParameterNames);
        }

        List<String> queryParameterNames = new ArrayList<>();
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
    
    private String buildPath(String ... elements)
    {
        StringBuilder buff = new StringBuilder();
        for(String element : elements)
        {
            if(buff.length() == 0)
            {
                buff.append(element);
            }
            else if(element.length() > 0)
            {
                if(buff.charAt(buff.length() - 1) == '/')
                {
                    if(element.charAt(0) == '/')
                    {
                        buff.append(element.substring(1));
                    }
                    else
                    {
                        buff.append(element);
                    }
                }
                else
                {
                    if(element.charAt(0) == '/')
                    {
                        buff.append(element);
                    }
                    else
                    {
                        buff.append('/').append(element);
                    }
                }
            }
        }
        return buff.toString();
    }
}
