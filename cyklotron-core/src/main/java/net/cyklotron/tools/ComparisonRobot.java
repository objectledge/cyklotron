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
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;

/**
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: ComparisonRobot.java,v 1.10 2005-04-11 08:15:22 rafal Exp $
 */
public class ComparisonRobot
{
    private static final String OUTPUT_ENCODING = "UTF-8";

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
        if(args.length > 1 && args[1].equals("-t"))
        {
            robot.runTransform();
        }
        else
        {
            if(args.length > 2 && args[1].equals("-s"))
            {
                site = args[2];
            }
            robot.runDownload(site);
        }
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
    
    public void runDownload(String site)
        throws Exception
    {
        System.out.println("Start old application on "+oldUrl+" and press enter when ready");
        keypress();
        runDownload(false, site);
        System.out.println("Start new application on "+newUrl+" and press enter when ready");
        keypress();
        runDownload(true, site);
        System.out.println("complete.");
    }
    
    private void runDownload(boolean newApp, String site)
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
    
    private void loadPage(boolean newApp, String x)
        throws Exception
    {
        URL url;
        File origFile;
        File procFile;
        List<Replacement> patterns;
        if(newApp)
        {
            url = new URL(newUrl + "?x=" + x);
            origFile = new File(workDir, "/orig/new/" + x + ".html");
            procFile = new File(workDir, "/proc/new/" + x + ".html");
            patterns = newPatterns;
        }
        else
        {
            url = new URL(oldUrl + "/app/cms/x/" + x);
            origFile = new File(workDir, "/orig/old/" + x + ".html");
            procFile = new File(workDir, "/proc/old/" + x + ".html");
            patterns = oldPatterns;
        }
        if(!procFile.getParentFile().exists())
        {
            procFile.getParentFile().mkdirs();
        }
    
        String content = Utils.loadUrl(url, httpClient);
        Utils.writeFile(origFile, content, OUTPUT_ENCODING);
    
        content = Replacement.apply(content, patterns);
        Utils.writeFile(procFile, content, OUTPUT_ENCODING);
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

    public void runTransform()
        throws IOException
    {
        write("transforming old application's output...");
        runTransform(false);
        write("transforming new application's output...");
        runTransform(true);
        write("completed");
    }
    
    private void runTransform(boolean newApp)
        throws IOException
    {
        File origDir = new File(workDir, "/orig/" + (newApp ? "new" : "old"));
        File procDir = new File(workDir, "/proc/" + (newApp ? "new" : "old"));
        List<Replacement> patterns = newApp ? newPatterns : oldPatterns;
        File[] items = origDir.listFiles(
            new FilenameFilter()
            {
                public boolean accept(File dir, String name)
                {
                    return !name.startsWith(".");
                }
            }
        );
        for(File origFile : items)
        {
            runTransform(origFile, procDir, patterns);
        }
    }
    
    private void runTransform(File origFile, File procDir, List<Replacement> patterns)
        throws IOException
    {
        String content = Utils.readFile(origFile, OUTPUT_ENCODING);
        content = Replacement.apply(content, patterns);
        File procFile = new File(procDir, origFile.getName());
        Utils.writeFile(procFile, content, OUTPUT_ENCODING);
    }

    private static void keypress()
        throws IOException
    {
        System.in.read();
        while(System.in.available() > 0)
        {
            System.in.read();
        }
    }

    private static long elapsed(long start)
    {
        return System.currentTimeMillis() - start;
    }

    private static void write(String s)
    {
        System.out.println(s);
    }
}
