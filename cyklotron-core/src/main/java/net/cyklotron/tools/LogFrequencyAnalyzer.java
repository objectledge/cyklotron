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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fil
 *
 */
public class LogFrequencyAnalyzer
{
    public static void main(String[] argv)
        throws Exception
    {
        LogFrequencyAnalyzer analyzer = new LogFrequencyAnalyzer();
        analyzer.process(new File(argv[0]), new File(argv[1]));
    }

    
    private void process(File inFile, File outFile)
        throws IOException
    {        
        System.out.println("reading");
        LineNumberReader reader = new LineNumberReader(new InputStreamReader(
            new BufferedInputStream(new FileInputStream(inFile)), "UTF-8"));
        Map<String,Item> map = new HashMap<String,Item>();
        int total = 0;
        while(reader.ready())
        {
            String line = reader.readLine();
            String[] tokens = line.split(" ");
            String method = tokens[3];
            if(!method.equals("GET") && !method.equals("HEAD"))
            {
                continue;
            }
            total++;
            String uri = tokens[4];
            Item i = map.get(uri);
            if(i != null)
            {
                i.incFreq();
            }
            else
            {
                map.put(uri, new Item(uri));
            }
        }

        System.out.println("sorting");
        List<Item> l = new ArrayList<Item>(map.values());
        Collections.sort(l);
        System.out.println("reversing");
        Collections.reverse(l);
        
        System.out.println("writing");
        PrintWriter out = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(
            new FileOutputStream(outFile)), "UTF-8"));
        for(Item i : l)
        {
            out.print(i.getFreq());
            out.print(" ");
            out.format("%.2f%% ", (double)i.getFreq() * 100 / total);
            out.println(i.getUri());
        }
    }
    
    private static class Item 
        implements Comparable<Item>
    {
        private final String uri;
        
        private int freq = 1;
        
        public Item(String uri)
        {
            this.uri = uri;
        }
        
        public String getUri()
        {
            return uri;
        }
        
        public int getFreq()
        {
            return freq;
        }
        
        public void incFreq()
        {
            freq++;
        }

        /**
         * {@inheritDoc}
         */
        public int compareTo(Item o)
        {
            return freq - o.freq;
        }
    }
}
