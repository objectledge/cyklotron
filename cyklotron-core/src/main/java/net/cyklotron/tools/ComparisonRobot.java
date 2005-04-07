// 
// Copyright (c) 2003-2005, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
// 
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//  
// * Redistributions of source code must retain the above copyright notice,  
//	 this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//	 this list of conditions and the following disclaimer in the documentation  
//	 and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//	 nor the names of its contributors may be used to endorse or promote products  
//	 derived from this software without specific prior written permission. 
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
package net.cyklotron.tools;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;

/**
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: ComparisonRobot.java,v 1.6 2005-04-07 12:30:13 rafal Exp $
 */
public class ComparisonRobot
{
    private File workDir;
    
    private String oldUrl;
    
    private String newUrl;
    
    private List<Replacement> oldPatterns;
    
    private List<Replacement> newPatterns;
    
    private HttpClient httpClient;
    
    private int limit;
    
    private static final int PATTERN_FLAGS = Pattern.MULTILINE;

    public static void main(String[] args)
        throws Exception
    {
        File baseDir = new File(System.getProperty("user.dir"));
        ComparisonRobot robot = new ComparisonRobot(baseDir, args[0]);
        String site = null;
        if(args.length > 1)
        {
            site = args[1];
        }
        robot.run(site);
    }
    
    public ComparisonRobot(File baseDir, String configPath)
        throws IOException
    {
        Properties properties = Utils.loadProperties(new File(baseDir, configPath));
        this.workDir = new File(baseDir, properties.getProperty("workdir"));

        this.oldUrl = properties.getProperty("url.old");
        this.newUrl = properties.getProperty("url.new");

        this.oldPatterns = Replacement.parse(new File(baseDir, properties
            .getProperty("patterns.old")), PATTERN_FLAGS);
        this.newPatterns = Replacement.parse(new File(baseDir, properties
            .getProperty("patterns.new")), PATTERN_FLAGS);
        
        String limitStr =  properties.getProperty("limit", "0");
        this.limit = Integer.parseInt(limitStr);
        
        oldPatterns.add(new Replacement(properties.getProperty("context.old"), "/context/"));
        newPatterns.add(new Replacement(properties.getProperty("context.new"), "/context/"));

        this.httpClient = new HttpClient();
    }
    
    public void run(String site)
        throws Exception
    {
        System.out.println("Start old application on "+oldUrl+" and press enter when ready");
        keypress();
        run(false, site);
        System.out.println("Start new application on "+newUrl+" and press enter when ready");
        keypress();
        run(true, site);
        System.out.println("complete.");
    }
    
    private void write(String s)
    {
        System.out.println(s);
    }
    
    private void keypress()
        throws IOException
    {
        System.in.read();
        while(System.in.available() > 0)
        {
            System.in.read();
        }
    }
    
    private void run(boolean newApp, String site)
        throws Exception
    {
        System.out.println("loading listing "+(site != null ? site : ""));
        long start = elapsed(0);
        String listing = loadListing(newApp);
        List<String> ids = parseListing(listing, site);
        if(limit == 0)
        {
            limit = ids.size();
        }
        System.out.println("got listing of " + ids.size() + " pages in "
            + Utils.formatInterval(elapsed(start)/1000));
        start = elapsed(0);
        for(int counter = 1; counter <= limit; counter++)
        {
            loadPage(newApp, ids.get(counter-1));
            System.out.print(".");
            if(counter % 50 == 0)
            {
                long t = elapsed(start)/1000;
                long eta = t * ids.size() / counter;
                System.out.println(" "+counter+" "+Utils.formatRate(counter, t, "page")+" "+
                    "ETA "+Utils.formatInterval(eta));
            }
        }
        System.out.println();
        long t = elapsed(start)/1000;
        System.out.println("loaded "+limit+" pages in "+Utils.formatInterval(t)+", "+
            Utils.formatRate(limit, t, "page")+" on average");
    }
    
    private List<String> parseListing(String listing, String site)
    {
        List<String> tokens = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(listing, "\n");
        boolean matched = false;
        // simple case - skip lines that start with site
        if(site == null)
        {
            while(st.hasMoreTokens())
            {
                String t = st.nextToken();
                if(!t.startsWith("site"))
                {
                    tokens.add(t);
                }
            }
        }
        else
        {
            loop: while(st.hasMoreTokens())
            {
                String t = st.nextToken();
                if(t.startsWith("site"))
                {
                    if(!matched)
                    {
                        // start of requested site reached
                        if(t.contains(site))
                        {
                            matched = true;
                        }
                    }
                    // next site reached
                    else
                    {
                        break loop;
                    }
                }
                // ordinary entry
                else
                {
                    if(matched)
                    {
                        tokens.add(t);
                    }
                }
            }
        }
        return tokens;        
    }
    
    private long elapsed(long start)
    {
        return System.currentTimeMillis() - start;
    }
    
    
    private String loadListing(boolean newApp)
        throws Exception
    {
        URL url;
        if(newApp)
        {
            url = new URL(newUrl + "/view/structure.PublicNodes?text&action=i18n.SetLocale&locale=pl_PL");
        }
        else
        {
            url = new URL(oldUrl + "/app/cms/view/structure,PublicNodes?text");
        }
        return Utils.loadUrl(url, httpClient);
    }
    
    private static final String OUTPUT_ENCODING = "UTF-8";
    
    private void loadPage(boolean newApp, String x)
        throws Exception
    {
        URL url;
        File outFile;
        List<Replacement> patterns;
        if(newApp)
        {
            url = new URL(newUrl + "?x=" + x);
            outFile = new File(workDir, "/new/" + x + ".html");
            patterns = newPatterns;
        }
        else
        {
            url = new URL(oldUrl + "/app/cms/x/" + x);
            outFile = new File(workDir, "/old/" + x + ".html");
            patterns = oldPatterns;
        }
        if(!outFile.getParentFile().exists())
        {
            outFile.getParentFile().mkdirs();
        }
        String content = Utils.loadUrl(url, httpClient);
        content = Replacement.apply(content, patterns);
        Utils.writeFile(outFile, content, OUTPUT_ENCODING);
    }
}
