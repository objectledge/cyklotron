/**
 * 
 */
package net.cyklotron.tools;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.httpclient.methods.TraceMethod;
import org.objectledge.utils.StringUtils;

/**
 * @author fil
 */
public class PerformanceTester
{
    public static void main(String[] argv)
        throws Exception
    {
        PerformanceTester tester = new PerformanceTester(180, SECONDS, 1000, 100);
        tester.process(new File(argv[0]), new File(argv[1]), argv[2]);
    }

    private final ThreadPoolExecutor workerExecutor;

    private final long sessionTimeout;

    private final int lazyExpireCounterLimit;

    private int lazyExpireCounter;

    private final Map<Integer, Session> sessionStore = new HashMap<Integer, Session>();

    public PerformanceTester(long sessionTimeout, TimeUnit unit, int lazyExpireCounterLimit,
        int maxThreads)
    {
        this.sessionTimeout = unit.toMillis(sessionTimeout);
        this.lazyExpireCounterLimit = lazyExpireCounterLimit;
        workerExecutor = new ThreadPoolExecutor(maxThreads, maxThreads, 60, SECONDS,
            new LinkedBlockingQueue<Runnable>());
    }

    private void process(File inFile, File logFile, String baseUrl)
        throws IOException
    {
        try
        {
            LineNumberReader reader = new LineNumberReader(new InputStreamReader(
                new BufferedInputStream(new FileInputStream(inFile)), "UTF-8"));

            Monitor monitor = new Monitor(logFile, 1);

            while(reader.ready())
            {
                Request request;
                Date date;
                try
                {
                    String line = reader.readLine();
                    String[] tokens = line.split(" ");
                    int requestId = Integer.parseInt(tokens[0]);
                    int sessionId = Integer.parseInt(tokens[1]);
                    date = new Date(Long.parseLong(tokens[2]));
                    String method = tokens[3];
                    String url = baseUrl + tokens[4];
                    HttpMethod httpMethod;
                    if(method.equals("GET"))
                    {
                        httpMethod = new GetMethod(url);
                    }
                    else if(method.equals("HEAD"))
                    {
                        httpMethod = new HeadMethod(url);
                    }
                    else if(method.equals("OPTIONS"))
                    {
                        httpMethod = new OptionsMethod(url);
                    }
                    else if(method.equals("TRACE"))
                    {
                        httpMethod = new TraceMethod(url);
                    }
                    else
                    {
                        monitor.log(requestId, "unsuported method "+method);
                        continue;
                    }

                    request = new Request(requestId, getSession(sessionId), date, httpMethod,
                        monitor);
                }
                catch(Exception e)
                {
                    throw (IOException)new IOException("problem parsing line "
                        + reader.getLineNumber() + ": " + e.getMessage()).initCause(e);
                }

                workerExecutor.execute(request);
            }
        }
        finally
        {
            workerExecutor.shutdown();
            try
            {
                workerExecutor.awaitTermination(Long.MAX_VALUE, SECONDS);
            }
            catch(InterruptedException e)
            {
                // oh really?
            }
            System.out.println("\ndone.");
        }
    }

    private Session getSession(int id)
    {
        Session s = sessionStore.get(id);
        if(s == null)
        {
            s = new Session(id);
            sessionStore.put(id, s);
        }
        return s;
    }

    private void expireSessions(Date date)
    {
        Date limit = new Date(date.getTime() - sessionTimeout);
        Iterator<Map.Entry<Integer, Session>> i = sessionStore.entrySet().iterator();
        while(i.hasNext())
        {
            Date lastAccess = i.next().getValue().getLastRequest();
            if(lastAccess != null && lastAccess.before(limit))
            {
                i.remove();
            }
        }
    }

    private static class Session
    {
        private final int id;

        private final HttpClient httpClient;

        private Date lastRequest;

        public Session(int id)
        {
            this.id = id;
            this.httpClient = new HttpClient();
        }

        public int getId()
        {
            return id;
        }

        public Date getLastRequest()
        {
            return lastRequest;
        }

        public void doRequest(Request req, Monitor monitor)
        {
            HttpMethod method = req.getMethod();
            try
            {
                httpClient.executeMethod(method);
                if(method.getStatusCode() != 200)
                {
                    monitor.log(req, method.getStatusCode() + " " + method.getStatusText());
                }
                //monitor.log(req, Integer.toString(method.getStatusCode()));
            }
            catch(Exception e)
            {
                monitor.log(req, "failed request", e);
            }
            finally
            {
                method.releaseConnection();
                this.lastRequest = req.getDate();
            }
        }
    }

    private static class Request
        implements Runnable
    {
        private final int requestId;

        private final Session session;

        private final Date date;

        private final HttpMethod method;

        private final Monitor monitor;

        public Request(int requestId, Session session, Date date, HttpMethod method, Monitor monitor)
        {
            this.requestId = requestId;
            this.session = session;
            this.date = date;
            this.method = method;
            this.monitor = monitor;
        }

        /**
         * {@inheritDoc}
         */
        public void run()
        {
            synchronized(session)
            {
                session.doRequest(this, monitor);
                monitor.tick();
            }
        }

        public Date getDate()
        {
            return date;
        }

        public HttpMethod getMethod()
        {
            return method;
        }

        public int getRequestId()
        {
            return requestId;
        }
    }

    private static class Monitor
    {
        private final PrintWriter log;
        private final int ticksPerDot;
        private final long started;
        private int counter = 0;
        private int dotCounter = 0;
        private int recentCounter;
        private double recentTime;

        public Monitor(File logFile, int ticksPerDot) 
            throws IOException
        {
            this.log = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(logFile), "UTF-8"));
            this.ticksPerDot = ticksPerDot;
            this.started = System.currentTimeMillis();
            System.out.println(ticksPerDot + " entries / dot");
        }

        public void tick()
        {
            if(++counter % ticksPerDot == 0)
            {
                System.out.print(".");
                if(++dotCounter % 50 == 0)
                {
                    double now = System.currentTimeMillis();
                    double elapsed = (now - started) / 1000;
                    int counterDelta = counter - recentCounter;
                    double timeDelta = (now - recentTime) / 1000;
                    
                    System.out.println(" "
                        + counter
                        + " "
                        + StringUtils.formatRate(counter, elapsed, "request")
                        + (recentCounter !=  0 ? (", recently " + StringUtils.formatRate(
                            counterDelta, timeDelta, "request")) : ""));

                    recentCounter = counter;
                    recentTime = now;
                }
            }
        }

        public void log(int i, String s)
        {
            synchronized(log)
            {
                log.println(i+": "+s);
            }
        }
        
        public void log(Request r, String s)
        {
            synchronized(log)
            {
                log.println(r.getRequestId()+": "+s);
            }
        }

        public void log(Request r, String s, Throwable t)
        {
            synchronized(log)
            {
                log.println(r.getRequestId()+": "+s);
                t.printStackTrace(log);
            }
        }
    }
}
