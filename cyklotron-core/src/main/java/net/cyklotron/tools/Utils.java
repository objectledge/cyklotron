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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * 
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: Utils.java,v 1.5 2005-04-12 06:43:52 rafal Exp $
 */
public class Utils
{
    private static final Pattern CVS_ID_PATTERN = Pattern.compile("\\$Id[^$]*\\$");

    private Utils()
    {
        // non instantiantable
    }

    public static String readFile(File file, String encoding)
        throws IOException
    {
        if(file.exists())
        {
            Reader reader = new InputStreamReader(
                new BufferedInputStream(new FileInputStream(file)), encoding);
            StringBuilder out = new StringBuilder((int)file.length());
            char[] buff = new char[1024*16];
            int count = 0;
            while(count >= 0)
            {
                count = reader.read(buff, 0, buff.length);
                if(count >= 0)
                {
                    out.append(buff, 0, count);
                }
            }
            reader.close();
            return out.toString();
        }
        throw new FileNotFoundException(file.getCanonicalPath());
    }

    public static void writeFile(File file, String string, String encoding)
        throws IOException
    {
        if(!file.exists())
        {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        Writer writer = new OutputStreamWriter(
            new BufferedOutputStream(new FileOutputStream(file)), encoding);
        writer.append(string);
        writer.close();
    }

    public static void writeFileIfDifferent(File file, String newContents, 
        String encoding, String marker)
        throws IOException
    {
        String oldContents = null;
        if(file.exists())
        {
            oldContents = readFile(file, encoding);
            if(marker != null && oldContents.contains(marker))
            {
                return;
            }
            newContents = preserveCVSId(oldContents, newContents);
            if(oldContents.equals(newContents))
            {
                return;
            }
        }
        writeFile(file, newContents, encoding);
    }

    public static String preserveCVSId(String oldContents, String newContents)
    {
        Matcher oldMatch = CVS_ID_PATTERN.matcher(oldContents);
        if(!oldMatch.find())
        {
            return newContents;
        }
        String oldId = Matcher.quoteReplacement(oldMatch.group());
        Matcher newMatch = CVS_ID_PATTERN.matcher(newContents);
        StringBuffer buff = new StringBuffer(newContents.length());
        while(newMatch.find())
        {
            newMatch.appendReplacement(buff, oldId);
        }
        newMatch.appendTail(buff);
        return buff.toString();
    }

    public static String camelCaseFileName(String pathname)
    {
        int index = pathname.lastIndexOf("/");
        String dirname = pathname.substring(0, index+1);
        String filename = pathname.substring(index+1);
        filename = camelCase(filename);
        pathname = dirname + filename;
        return pathname;
    }

    /**
     * Convert string to camel case.
     * 
     * @param input the input string.
     * @return the converted string.
     */
    public static String camelCase(String input)
    {
        input = input.substring(0, 1).toUpperCase() + input.substring(1);
        if(input.indexOf("_") < 0)
        {
            return input;
        }
        StringTokenizer st2 = new StringTokenizer(input, "_");
        String result = st2.nextToken();
        while(st2.hasMoreTokens())
        {
            String temp = st2.nextToken();
            result = result + temp.substring(0, 1).toUpperCase();
            result = result + temp.substring(1, temp.length());
        }
        return result;
    }

    public static String loadUrl(URL url)
        throws IOException
    {
        URLConnection conn = url.openConnection();
        String encoding = getCharset(conn.getContentType());
        InputStream is = conn.getInputStream();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        int c = 0;
        while(c >= 0)
        {
            c = is.read();
            if(c >= 0)
            {
                os.write(c);
            }
        }
        is.close();
        os.flush();
        return new String(os.toByteArray(), encoding);
    }
    
    public static String loadUrl(URL url, HttpClient client)
        throws IOException
    {
        HttpMethod method = null;
        try
        {
            method = new GetMethod(url.toString());
            client.executeMethod(method);
            return method.getResponseBodyAsString();            
        }
        finally
        {
            if(method != null)
            {
                method.releaseConnection();
            }
        }
    }

    public static String getCharset(String contentType)
    {
        if(contentType != null)
        {
            int pos = contentType.indexOf("charset=");
            if(pos > 0)
            {
                int endPos = contentType.indexOf(';', pos);
                if(endPos < 0)
                {
                    endPos = contentType.length();
                }
                return contentType.substring(pos+8, endPos).trim();
            }
        }
        return "ISO-8859-1";
    }
    
    public static List<String> tokenize(String s, String t)
    {
        List<String> tokens = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(s, t);
        while(st.hasMoreTokens())
        {
            tokens.add(st.nextToken());
        }
        return tokens;
    }
    
    public static Properties loadProperties(File file)
        throws IOException
    {
        InputStream is = new BufferedInputStream(new FileInputStream(file));
        Properties p = new Properties();
        p.load(is);
        return p;
    }   
    
    /**
     * Returns human readable representation of interval value in days, hours etc.
     * 
     * @param interval in seconds.
     * @return human readable interval specification.
     */
    public static String formatInterval(long interval)
    {
        long days = interval / (24 * 60 * 60);
        interval -= days * 24 * 60 * 60;
        long hours = interval / (60 * 60);
        interval -= hours * 60 * 60;
        long minutes = interval / 60;
        interval -= minutes * 60;
        long seconds = interval;
        StringBuffer buff = new StringBuffer();
        if(days > 0)
        {
            buff.append(days).append(" days, ");
        }
        if(days > 0 || hours > 0)
        {
            buff.append(hours).append(" hours, ");
        }
        if(days > 0 || hours > 0 || minutes > 0)
        {
            buff.append(minutes).append(" minutes, ");
        }
        buff.append(seconds).append(" seconds");
        return buff.toString();
    }
    
    /**
     * Renders a human readable event rate esitmation.
     * 
     * @param events number of events.
     * @param time timespan in seconds.
     * @param event event name.
     * @return a human readable event rate esitmation.
     */
    public static String formatRate(double events, double time, String event)
    {
        StringBuffer buff = new StringBuffer();
        NumberFormat format = new DecimalFormat("#.##");
        if(events > time)
        {
            buff.append(format.format(events/time)+" "+event+"s / 1s on average");
        }
        else
        {
            double interval = time/events;
            int d = (int)(interval / (24 * 3600));
            interval -= d * 24 * 3600;
            int h = (int)(interval / 3600);
            interval -= h * 3600;
            int m = (int)(interval / 60);
            interval -= m * 60;
            buff.append("1 "+event+" / ");
            if(d > 0)
            {
                buff.append(d+"d ");
            }
            if(h > 0 || d > 0)
            {
                buff.append(h+"h ");
            }
            if(m > 0 || h > 0 || d > 0)
            {
                buff.append(m+"m ");
            }
            buff.append(format.format(interval)+" s on average");
        }                
        return buff.toString();
    }

    public static void keypress()
        throws IOException
    {
        System.in.read();
        while(System.in.available() > 0)
        {
            System.in.read();
        }
    }

    public static long elapsed(long start)
    {
        return System.currentTimeMillis() - start;
    }

    public static void write(String s)
    {
        System.out.println(s);
    }    
}
