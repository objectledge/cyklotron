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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.httpclient.HttpClient;
import org.objectledge.utils.StringUtils;

/**
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: ComparisonRobot.java,v 1.21 2005-06-29 04:26:49 rafal Exp $
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
    
    // command line params
    
    private int limit = 0;
        
    private boolean runNew = true;
    
    private boolean runOld = true;
    
    private String site = null;
    
    private int startPage = 0;
    
    private String page = null;
    
    //
    
    private static final int PATTERN_FLAGS = Pattern.MULTILINE;

    public static void main(String[] args)
        throws Exception
    {
        File baseDir = new File(System.getProperty("user.dir"));
        
        CommandLineParser parser = new PosixParser();
        Options options = getOptions();
        CommandLine cmd = parser.parse(options, args);
        
        if(cmd.hasOption("h"))
        {
            showHelp(options);
            return;
        }
        if(!cmd.hasOption("c"))
        {
            Utils.write("missing required option -c <config>");
        }
            
        ComparisonRobot robot = new ComparisonRobot(baseDir, cmd.getOptionValue("c"));
        if(cmd.hasOption("n") && cmd.hasOption("o"))
        {
            Utils.write("-n and -o are mutualy exclusive.");
            return;
        }
        if(cmd.hasOption("n"))
        {
            robot.runOld = false;
        }
        if(cmd.hasOption("o"))
        {
            robot.runNew = false;
        }
        robot.site = cmd.getOptionValue("s", null);
        robot.limit = Integer.parseInt(cmd.getOptionValue("l", "0"));
        robot.startPage = Integer.parseInt(cmd.getOptionValue("b", "0"));
        robot.page = cmd.getOptionValue("x", null);
        if(cmd.hasOption("t"))
        {
            robot.runTransform();
        }
        else
        {
            robot.runDownload();
        }
        System.out.println("complete.");        
    }
    
    public static Options getOptions()
    {
        Options opts = new Options();
        opts.addOption("c", "config", true, "configuration path");
        opts.addOption("s", "site", true, "selects a specific site");
        opts.addOption("l", "limit", true, "limits number of processed pages");
        opts.addOption("t", "transform", false, "perform regexp transformation only");
        opts.addOption("o", "old-only", false, "process old application only");
        opts.addOption("n", "new-only", false, "process new application only");
        opts.addOption("h", "help", false, "display help");
        opts.addOption("b", "start-offset", true, "start processing pages from given number");
        opts.addOption("x", "single-page", true, "process only one page");
        return opts;
    }
    
    public static void showHelp(Options options)
    {
        new HelpFormatter().printHelp("ComparisonRobot", options);
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////////
    
    public ComparisonRobot(File baseDir, String configPath)
        throws IOException
    {
        Properties properties = Utils.loadProperties(new File(baseDir, configPath));
        this.workDir = new File(baseDir, properties.getProperty("workdir"));

        this.oldUrl = properties.getProperty("url.old");
        this.newUrl = properties.getProperty("url.new");

        this.oldPatterns = Replacement.parse(new File(baseDir, properties
            .getProperty("patterns.old")), "UTF8", PATTERN_FLAGS);
        this.newPatterns = Replacement.parse(new File(baseDir, properties
            .getProperty("patterns.new")), "UTF8", PATTERN_FLAGS);
        
        String limitStr =  properties.getProperty("limit", "0");
        this.limit = Integer.parseInt(limitStr);
        
        oldPatterns.add(new Replacement(properties.getProperty("context.old"), "/context/"));
        newPatterns.add(new Replacement(properties.getProperty("context.new"), "/context/"));

        oldPatterns.add(new Replacement(properties.getProperty("srv.old"), properties.getProperty("srv.new")));
        this.httpClient = new HttpClient();
    }
    
    public void runDownload()
        throws Exception
    {
        if(runOld)
            runDownload(false);
        if(runNew)
            runDownload(true);
    }
    
    private void runDownload(boolean newApp)
        throws Exception
    {
        System.out.println("Start " + (newApp ? "new" : "old") + " application on " + 
            (newApp ? newUrl : oldUrl) + " and press enter when ready");
        //Utils.keypress();
        if(page == null)
        {
            System.out.println("loading listing "+(site != null ? site : ""));
            long start = Utils.elapsed(0);
            String listing = loadListing(newApp);
            List<String> ids = parseListing(listing, site);
            if(limit == 0)
            {
                limit = ids.size();
            }
            Collections.sort(ids, new SimpleComparator());
            System.out.println("got listing of " + ids.size() + " pages in "
                + StringUtils.formatInterval(Utils.elapsed(start)/1000));
            start = Utils.elapsed(0);
            int counter = 1;
            int fixedIdsSize = ids.size();
            if(startPage > 0)
            {
                counter = startPage;
                //limit = limit - (startPage-1);
                fixedIdsSize = fixedIdsSize - (startPage-1);
            }
            System.out.println("starting from page: "+counter);
            for(; counter <= limit && counter <= ids.size(); counter++)
            {
                loadPage(newApp, ids.get(counter-1));
                System.out.print(".");
                if(counter % 50 == 0)
                {
                    long t = Utils.elapsed(start)/1000;
                    int pagesProcessed = counter-(startPage-1);
                    long eta = t * fixedIdsSize / pagesProcessed;
                    System.out.println(" "+counter+" "+StringUtils.formatRate(pagesProcessed, t, "page")+" "+
                        "ETA "+StringUtils.formatInterval(eta));
                }
            }
            System.out.println();
            long t = Utils.elapsed(start)/1000;
            System.out.println("loaded "+limit+" pages in "+StringUtils.formatInterval(t)+", "+
                StringUtils.formatRate(limit, t, "page")+" on average");
        }
        else
        {
            System.out.println("loading single page: "+page);
            URL url;
            if(newApp)
            {
                url = new URL(newUrl + 
                    "/action/i18n.SetLocale/locale/pl_PL");
            }
            else
            {
                url = new URL(oldUrl + "/app/cms/view/site,SiteList");
            }
            Utils.loadUrl(url, httpClient);
            loadPage(newApp, page);
        }
    }
    
    private void loadPage(boolean newApp, String x)
        throws Exception
    {
        URL url;
        File origFile;
        File procFile;
        List<Replacement> patterns;
        int addZero = 6 - x.length();
        String fileName = "";
        for(int i = 0; i < addZero; i++)
        {
            fileName = fileName + "0";
        }
        fileName = fileName + x;
        
        if(newApp)
        {
            url = new URL(newUrl + "?x=" + x);
            origFile = new File(workDir, "/orig/new/" + fileName + ".html");
            procFile = new File(workDir, "/proc/new/" + fileName + ".html");
            patterns = newPatterns;
        }
        else
        {
            url = new URL(oldUrl + "/app/cms/x/" + x);
            origFile = new File(workDir, "/orig/old/" + fileName + ".html");
            procFile = new File(workDir, "/proc/old/" + fileName + ".html");
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
        String siteParam = "";
        if(site != null)
        {
            if(site.matches("\\d+"))
            {
                siteParam = "&site_id="+site;
            }
            else
            {
                siteParam = "&site_name="+site;
            }
        }
        if(newApp)
        {
            url = new URL(newUrl + 
                "/view/structure.PublicNodes?text&action=i18n.SetLocale&locale=pl_PL" + siteParam);
        }
        else
        {
            url = new URL(oldUrl + "/app/cms/view/structure,PublicNodes?text" + siteParam);
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
                        if(site.matches("\\d+"))
                        {
                            if(t.contains("id=" + site))
                            {
                                matched = true;
                            }
                        }
                        else
                        {
                            if(t.contains("name=" + site))
                            {
                                matched = true;
                            }
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
    
    /////////////////////////////////////////////////////////////////////////////////////////////

    public void runTransform()
        throws IOException
    {
        if(runOld)
            runTransform(false);
        if(runNew)
            runTransform(true);
    }
    
    private void runTransform(boolean newApp)
        throws IOException
    {
        Utils.write("transforming " + (newApp ? "new" : "old") + " application's output...");
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
    
    
    public class SimpleComparator implements Comparator<String>
    {
        public int compare(String o1, String o2)
        {
            if(o1 == null || o2 == null)
            {
                return 0;
            }
            try
            {
                return Integer.parseInt(o1) - Integer.parseInt(o2);
            }
            catch(NumberFormatException e)
            {
                return 0;
            }
        }
    }
}
