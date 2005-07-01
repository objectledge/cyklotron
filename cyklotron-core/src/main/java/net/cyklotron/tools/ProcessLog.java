/**
 * 
 */
package net.cyklotron.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Session;

/**
 * @author fil
 *
 */
public class ProcessLog
{
    
    
    public static void main(String[] argv)
        throws Exception
    {
        ProcessLog processLog = new ProcessLog();
        processLog.process(new File(argv[0]), new File(argv[1]), new File(argv[2]));
    }
    
    private void process(File hostMapFile, File inFile, File outFile)
        throws IOException
    {
        UriTranslator translator = new CykloklonUriTranslator(hostMapFile);
        LogReader log = new LogReader(inFile, translator, false);
        SessionRegistry registry = new SessionRegistry(outFile, 3*60, 100);
        PrintWriter out = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(
            new FileOutputStream(outFile)), "UTF-8"));
        
        int entriesPerDot = 200;
        int counter = 0;
        int dotCounter = 0;
        System.out.println(entriesPerDot+" entries / dot");
        while(log.next())
        {
            if(++counter % entriesPerDot == 0)
            {    
                System.out.print(".");
                if(++dotCounter % 50 == 0)
                {
                    System.out.println(" "+counter);
                    dotCounter = 0;
                }
            }
            out.print(log.getRequestId());
            out.print(" ");
            out.print(registry.getSessionId(log.getDate(), log.getClientIp(), log.getJsessionid()));
            out.print(" ");
            out.print(log.getDate().getTime());
            out.print(" ");
            out.print(log.getMethod());
            out.print(" ");
            out.println(log.getUri());
        }
        out.close();
        System.out.println("done.");
    }
    

    private static class LogReader
    {
        private static final Pattern LOG_PATTERN = Pattern
            .compile("^\\[([^]]+)\\] (\\S+) (\\S+) (\\S+) \"(\\S+) (\\S+) +HTTP/1\\..\" (\\d+) (\\S+)$");
        
        private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
            "dd/MMM/yyyy:HH:mm:ss ZZZZZ", Locale.US);
        
        private static final Pattern JSESSIONID_PARAM_PATTERN = Pattern.
            compile("(.*?)(;|%3B)jsessionid=([0-9A-Fa-z]{16,32})(.*)");
        
        private final LineNumberReader reader;
        
        private final UriTranslator translator;
        
        private final boolean reportBrokenUrl;
        
        private int requestId;
        private Date date;
        private String clientIp;
        private String jsessionid;
        private String virtualHost;
        private String method;
        private String uri;
        private int result;
        private int bytes;

        public LogReader(File file, UriTranslator translator, boolean reportBrokenUrl)
            throws IOException
        {
            reader = new LineNumberReader(new InputStreamReader(new BufferedInputStream(
                new FileInputStream(file)), "UTF-8"));            
            this.translator = translator;
            this.reportBrokenUrl = reportBrokenUrl;
        }

        public boolean next()
            throws IOException
        {
            if(!reader.ready())
            {
                reader.close();
                return false;
            }
            String line = reader.readLine();
            Matcher m = LOG_PATTERN.matcher(line);
            if(!m.matches())
            {
                throw new IOException("malfomed log entry at line "+reader.getLineNumber());
            }
            requestId = reader.getLineNumber();
            try
            {
                date = DATE_FORMAT.parse(m.group(1));
            }
            catch(ParseException e)
            {
                throw new IOException("malformed date at line " + reader.getLineNumber() + ": "
                    + e.getMessage());
            }
            clientIp = m.group(2);
            jsessionid = m.group(3);
            virtualHost = m.group(4);
            method = m.group(5);
            uri = m.group(6);
            try
            {
                result = Integer.parseInt(m.group(7));
                if(!m.group(8).equals("-"))
                {
                    bytes = Integer.parseInt(m.group(8));
                }
                else
                {
                    bytes = 0;
                }
            }
            catch(NumberFormatException e)
            {
                throw new IOException("malformed number at line "+reader.getLineNumber()+": "+
                    e.getMessage()); 
            }
            
            m = JSESSIONID_PARAM_PATTERN.matcher(uri);
            if(m.matches())
            {
                uri = m.group(1) + m.group(4);
                jsessionid = m.group(2);
            }
            if(jsessionid.equals("-"))
            {
                jsessionid = null;
            }
            
            String origUri = uri;
            uri = translator.getTransladedUri(virtualHost, origUri);
            if(uri == null)
            {
                if(reportBrokenUrl)
                {
                    System.out.println("\nunexpected uri: " + origUri + " at line "
                        + reader.getLineNumber());
                }
                uri = origUri;
            }
            return true;
        }

        public int getRequestId()
        {
            return requestId;
        }
        
        public Date getDate()
        {
            return date;
        }

        public String getClientIp()
        {
            return clientIp;
        }

        public String getJsessionid()
        {
            return jsessionid;
        }

        public String getVirtualHost()
        {
            return virtualHost;
        }
        
        public String getMethod()
        {
            return method;
        }

        public String getUri()
        {
            return uri;
        }

        public int getResult()
        {
            return result;
        }

        public int getBytes()
        {
            return bytes;
        }
    }

    public interface UriTranslator
    {
        public String getTransladedUri(String virtualHost, String uri)
            throws IllegalArgumentException;
    }
    
    private static class CykloklonUriTranslator
        implements UriTranslator
    {
        private static final Pattern HOST_MAP_PATTERN = Pattern.compile("(\\S+)\t\"([^\"]+)\"\t(\\S+)\t\"([^\"]+)\"\t(\\S+)");

        Map<String,String> hostMap = new HashMap<String,String>();
        
        public CykloklonUriTranslator(File file) 
            throws IOException
        {
            LineNumberReader reader = new LineNumberReader(new InputStreamReader(
                new BufferedInputStream(new FileInputStream(file)), "UTF-8"));
            while(reader.ready())
            {
                String line = reader.readLine();
                if(line.startsWith("#"))
                {
                    continue;
                }
                Matcher m = HOST_MAP_PATTERN.matcher(line);
                if(!m.matches())
                {
                    throw new IOException("syntax error in " + file.getCanonicalPath()
                        + " at line " + reader.getLineNumber());
                }
                hostMap.put(m.group(1), m.group(5));
            }
        }
        
        private static final Pattern STRONA_PATTERN = Pattern.compile("^/strona/(\\d+).html");
        private static final Pattern PAGE_PATTERN = Pattern.compile("^/strona/(\\d+).html");
        private static final Pattern WIADOMOSCI_PATTERN = Pattern.compile("^/wiadomosci/(\\d+).html");
        private static final Pattern WIADOMOSCI2_PATTERN = Pattern.compile("^/wiadomosci/(\\d+),(\\d+).html");
        private static final Pattern SERVLET_PATTERN = Pattern.compile("^/labeo/?$");
        private static final Pattern SERVLET_QUERY_PATTERN = Pattern.compile("^/labeo(\\?.+)"); 
        private static final Pattern ESCAPED_SLASH_PATTERN = Pattern.compile("%2F");
        
        public String getTransladedUri(String virtualHost, String uri)
            throws IllegalArgumentException
        {
            uri = ESCAPED_SLASH_PATTERN.matcher(uri).replaceAll("/");
            if(uri.contains("/app/cms/x") || uri.contains("/view/"))
            {
                return uri;
            }
            else
            {
                Matcher m = STRONA_PATTERN.matcher(uri);
                if(m.matches())
                {
                    return "/labeo/app/cms/x/" + m.group(1);
                }
                m = PAGE_PATTERN.matcher(uri);
                if(m.matches())
                {
                    return "/labeo/app/cms/x/" + m.group(1);
                }
                m = WIADOMOSCI_PATTERN.matcher(uri);
                if(m.matches())
                {
                    return "/labeo/app/cms/x/" + m.group(1);
                }       
                m = WIADOMOSCI2_PATTERN.matcher(uri);
                if(m.matches())
                {
                    return "/labeo/app/cms/x/" + m.group(1) + "?query_id=" + m.group(2);
                }       
                m = SERVLET_PATTERN.matcher(uri);
                if(m.matches())
                {
                    String home = hostMap.get(virtualHost);
                    if(home == null)
                    {
                        throw new IllegalArgumentException("unknown virtual host " + virtualHost);
                    }
                    return "/labeo/app/cms/x/" + home;
                }
                m = SERVLET_QUERY_PATTERN.matcher(uri);
                if(m.matches())
                {
                    String home = hostMap.get(virtualHost);
                    if(home == null)
                    {
                        throw new IllegalArgumentException("unknown virtual host " + virtualHost);
                    }
                    return "/labeo/app/cms/x/" + home + m.group(1);
                }
                return null;
            }
        }        
    }
    
    private static class SessionRegistry
    {
        private static class Session
        {
            private Date lastHit;
            private static int nextId = 0;
            private int id;
            
            public Session()
            {
                id = nextId++;
            }
            
            public void hit(Date date)
            {
                lastHit = date;
            }
            
            public int getId()
            {
                return id;
            }
            
            public long age(Date now)
            {
                return (now.getTime() - lastHit.getTime()) / 1000;
            }
        }
        
        private final Map<String,Session> sessionByJsessionid = new HashMap<String,Session>();
        private final Map<String,Session> sessionByClientIp = new HashMap<String,Session>();
        
        private final int timeoutSeconds;
        private final int lazyExpireCounterLimit;
        private int lazyExpireCounter;
        
        public SessionRegistry(File outFile, int timeoutSeconds, int lazyExpireCounterLimit)
            throws IOException
        {
            this.timeoutSeconds = timeoutSeconds;
            this.lazyExpireCounterLimit = lazyExpireCounterLimit;
        }
        
        public int getSessionId(Date date, String clientIp, String jsessionid)
        {
            Session s = null;
            if(jsessionid != null)
            {
                s = sessionByJsessionid.get(jsessionid);
            }
            if(s == null)
            {
                s = sessionByClientIp.get(clientIp);
                if(s != null && jsessionid != null)
                {
                    sessionByJsessionid.put(jsessionid, s);
                }
            }
            if(s != null && s.age(date) > timeoutSeconds)
            {
                sessionByClientIp.values().remove(s);
                sessionByJsessionid.values().remove(s);
                s = null;
            }
            if(s == null)
            {
                s = new Session();
                if(jsessionid != null)
                {
                    sessionByJsessionid.put(jsessionid, s);
                }
                else
                {
                    sessionByClientIp.put(clientIp, s);                    
                }
            }
            s.hit(date);
            if(lazyExpireCounter++ >= lazyExpireCounterLimit)
            {
                expireSessions(date);
                lazyExpireCounter = 0;
            }
            return s.getId();
        }
        
        private void expireSessions(Date now)
        {
            Iterator<Map.Entry<String,Session>> i = sessionByClientIp.entrySet().iterator();
            while(i.hasNext())
            {
                Map.Entry<String,Session> e = i.next();
                if(e.getValue().age(now) > timeoutSeconds)
                {
                    i.remove();
                }
            }
            sessionByJsessionid.entrySet().iterator();
            while(i.hasNext())
            {
                Map.Entry<String,Session> e = i.next();
                if(e.getValue().age(now) > timeoutSeconds)
                {
                    i.remove();
                }
            }
        }
    }
}
