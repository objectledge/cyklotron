/**
 * 
 */
package net.cyklotron.tools;

import static java.util.concurrent.TimeUnit.SECONDS;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.httpclient.methods.TraceMethod;

/**
 * @author fil
 *
 */
public class PerformanceTester2
{
    private static int TESTS_PER_PAGE = 20;
    private static int CONCURRENCY = 5;

    public static void main(String[] argv)
        throws Exception
    {
        PerformanceTester2 tester = new PerformanceTester2();
        tester.process(new File(argv[0]), new File(argv[1]), argv[2]);
    }
    
    private void process(File inFile, File outFile, String baseUrl)
        throws IOException
    {
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENCY);
        HttpClientPool httpClientPool = new HttpClientPool(CONCURRENCY);
        try
        {
            LineNumberReader reader = new LineNumberReader(new InputStreamReader(
                new BufferedInputStream(new FileInputStream(inFile)), "UTF-8"));

            PrintWriter out = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(
                new FileOutputStream(outFile)), "UTF-8"));

            int counter = 0;
            while(reader.ready())
            {
                Request request;
                Date date;
                try
                {
                    String line = reader.readLine();
                    String[] tokens = line.split(" ");
                    String uri = tokens[2];
                    double t = test(baseUrl + uri, executor, httpClientPool);
                    double rate = 1000 / t;
                    out.format("%s %s %.2fms %.2f/s %s\n", tokens[0], tokens[1], t, rate, uri);
                    out.flush();
                    System.out.print(".");
                    if(++counter % 50 == 0)
                    {
                        System.out.println(" "+counter);
                    }
                }
                catch(Exception e)
                {
                    throw (IOException)new IOException("problem parsing line "
                        + reader.getLineNumber() + ": " + e.getMessage()).initCause(e);
                }
            }
            out.close();
        }
        finally
        {
            executor.shutdown();
            System.out.println("\ndone.");
        }
    }  
    
    private double test(String url, ExecutorService executor, HttpClientPool httpClientPool)
    {
        // heatup
        try
        {
            new Request(new GetMethod(url), httpClientPool).call();
        }
        catch(Exception e)
        {
            // heatup failed - don't bother measuring.
            return Double.NaN;
        }
        List<Callable<Long>> batch = new ArrayList<Callable<Long>>(TESTS_PER_PAGE);
        for(int i = 0; i < TESTS_PER_PAGE; i++)
        {
            batch.add(new Request(new GetMethod(url), httpClientPool));
        }
        try
        {
            List<Future<Long>> results = executor.invokeAll(batch);
            long sum = 0;
            int completed = 0;
            for(Future<Long> result : results)
            {
                try
                {
                    sum += result.get();
                    completed++;
                }
                catch(ExecutionException e)
                {
                    // failed request
                }
            }
            return (double)sum / completed;
        }
        catch(InterruptedException e)
        {
            return Double.NaN;
        }
    }
    
    private HttpMethod getHttpMethod(String method, String url)
    {
        if(method.equals("GET"))
        {
            return new GetMethod(url);
        }
        else if(method.equals("HEAD"))
        {
            return new HeadMethod(url);
        }
        else if(method.equals("OPTIONS"))
        {
            return new OptionsMethod(url);
        }
        else if(method.equals("TRACE"))
        {
            return new TraceMethod(url);
        }
        else
        {
            return null;
        }    
    }

    private static class Request implements Callable<Long>
    {
        private final HttpMethod httpMethod;
        private final HttpClientPool clientPool;

        public Request(HttpMethod httpMethod, HttpClientPool clientPool)
        {
            this.httpMethod = httpMethod;
            this.clientPool = clientPool;            
        }
        
        /**
         * {@inheritDoc}
         */
        public Long call()
            throws Exception
        {
            HttpClient client = clientPool.getClient();
            try
            {
                long t = System.currentTimeMillis();
                client.executeMethod(httpMethod);                 
                return System.currentTimeMillis() - t;
            }
            finally
            {
                clientPool.releaseClient(client);
            }
        }        
    }
    
    private static class HttpClientPool
    {
        private LinkedList<HttpClient> pool = new LinkedList<HttpClient>();
        
        public HttpClientPool(int initial)
        {
            for(int i = 0; i < initial; i++)
            {
                pool.add(new HttpClient());
            }
        }
        
        public synchronized HttpClient getClient()
        {
            if(pool.size() > 0)
            {
                return pool.remove(0);
            }
            else
            {
                return new HttpClient();
            }
        }
        
        public synchronized void releaseClient(HttpClient client)
        {
            if(client != null)
            {
                pool.add(client);
            }
        }
    }
}
