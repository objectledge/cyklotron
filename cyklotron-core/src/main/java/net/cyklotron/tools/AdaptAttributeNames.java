package net.cyklotron.tools;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/*
 * Created on 2005-01-12
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author pablo
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AdaptAttributeNames
{
    public static void main(String[] argv)
        throws Exception
    {
        boolean initData = false;
        String encoding = "UTF-8";
        if(argv.length < 1)
        {
            throw new Exception("Not enough attributes");
        }
        String path = "src/main/rml-sources.list";
        HashMap changed = new HashMap();
        InputStream is = new FileInputStream(new File(path));
        LineNumberReader reader = new LineNumberReader(new InputStreamReader(is,encoding));
        List fileList = new ArrayList();
        while(reader.ready())
        {
            String line = reader.readLine();
            if(line.contains("#init data"))
            {
                initData = true;
            }
            if(line.contains(".rml"))
            {
                fileList.add(line);
                if(!initData)
                {
                    processDefinitionFile(line, encoding, changed);
                }
                else
                {
                    processDataFile(line, encoding, changed);
                }
            }
        }
        is.close();
        for(int i = 0; i < fileList.size(); i++)
        {
            path = (String)fileList.get(i);
            File newFile = new File(path+".new");
            File oldFile = new File(path);
            oldFile.delete();
            newFile.renameTo(oldFile);
        }
    }
    
    private static void processDataFile(String path, String encoding, Map changed)
        throws Exception
    {
        int counter = 0;
        InputStream is = new FileInputStream(new File(path));
        LineNumberReader reader = new LineNumberReader(new InputStreamReader(is,encoding));
        OutputStream os = new FileOutputStream(new File(path+".new"));
        while(reader.ready())
        {
            String line = reader.readLine();
            line = convertDataLine(line, changed);  
            String toWrite = line+"\n";
            os.write(toWrite.getBytes(encoding));
            counter++;
        }
        System.out.println("Definition file: '"+path+"' parsed "+counter+" lines");
        os.close();
        is.close();
    }
    
    private static void processDefinitionFile(String path, String encoding, Map changed)
        throws Exception
    {
        int counter = 0;
        boolean attributes = false;
        InputStream is = new FileInputStream(new File(path));
        LineNumberReader reader = new LineNumberReader(new InputStreamReader(is,encoding));
        OutputStream os = new FileOutputStream(new File(path+".new"));
        while(reader.ready())
        {
            String line = reader.readLine();
            if(line.contains("ATTRIBUTES"))
            {
                attributes = true;
            }
            if(line.contains(");"))
            {
                attributes = false;
            }
            if(attributes)
            {
                line = convertDefinitionLine(line, changed);
            }
            String toWrite = line+"\n";
            os.write(toWrite.getBytes(encoding));
            counter++;
        }
        System.out.println("Data file: '"+path+"' parsed "+counter+" lines");
        os.close();
        is.close();
    }

    private static String convertDefinitionLine(String line, Map changed)
        throws Exception
    {
        StringTokenizer st = new StringTokenizer(line," ");
        String newLine = "";
        while(st.hasMoreTokens())
        {
            String part = st.nextToken();
            String newPart = "";
            if(part.indexOf("_") > 0)
            {   
                if(part.contains("resource_list") ||
                   part.contains("cross_reference") ||
                   part.contains("resource_class") ||
                   part.contains("resource_class"))
                {
                    newPart = part;
                }
                else
                {
                    part = part.trim();
                    boolean coma = false;
                    if(part.endsWith(","))
                    {
                        part = part.substring(0, part.length()-1).trim();
                        coma = true;
                    }
                    StringTokenizer st2 = new StringTokenizer(part,"_");
                    String result = st2.nextToken();
                    while(st2.hasMoreTokens())
                    {
                        String temp = st2.nextToken();
                        result = result+temp.substring(0,1).toUpperCase();
                        result = result+temp.substring(1,temp.length());
                    }
                    changed.put(part, result);
                    if(coma)
                    {
                        result = result + ","; 
                    }
                    newPart = result;
                }
            }
            else
            {
                newPart = part; 
            }
            newLine = newLine + newPart;
            if(st.hasMoreTokens())
            {
                newLine = newLine + " ";
            }
        }
        return newLine;
    }

    private static String convertDataLine(String line, Map changed)
    throws Exception
    {
        Iterator it = changed.keySet().iterator();
        while(it.hasNext())
        {
            String key = (String)it.next();
            if(line.contains(key))
            {
                line = line.replace(key, (String)changed.get(key));
            }
        }
        return line;
    }
}
