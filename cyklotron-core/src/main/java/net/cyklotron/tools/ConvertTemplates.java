package net.cyklotron.tools;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertTemplates
{
    private static final String INPUT_ENCODING = "ISO-8859-2";
    
    private static final String OUTPUT_ENCODING = "UTF-8";
    
    private File baseInDir;
    
    private File baseOutDir;

    private HashMap<String, String> targetMap = new HashMap<String, String>();
    
    private List<String> patternList = new ArrayList<String>();
    
    private boolean sites = false;
    
    public ConvertTemplates(File in, File out, boolean sites)
        throws Exception
    {
        baseInDir = in;
        baseOutDir = out;
        this.sites = sites;
    }
    
    public static void main(String[] argv)
        throws Exception
    {
        if(argv.length < 3)
        {
            throw new Exception("Not enough attributes");
        }
        String regexpPath = argv[0];
        String baseInPath = argv[1];
        File baseInDir = new File(baseInPath);
        if(!baseInDir.isDirectory())
        {
            throw new Exception("Invalid input path - doesn't point to the directory");
        }
        String baseOutPath = argv[2];
        File baseOutDir = new File(baseOutPath);
        if(!baseOutDir.isDirectory())
        {
            throw new Exception("Invalid output path - doesn't point to the directory");
        }
        String mode = argv[3];
        ConvertTemplates ct = new ConvertTemplates(baseInDir, baseOutDir, mode.equals("sites"));
        ct.execute(regexpPath);
    }
    
    public void execute(String srcPath)
        throws Exception
    {
        System.out.println("started at: "+new Date());
        InputStream is = new FileInputStream(new File(srcPath));
        LineNumberReader reader = new LineNumberReader(new InputStreamReader(is,OUTPUT_ENCODING));
        List fileList = new ArrayList();
        while(reader.ready())
        {
            String source = reader.readLine();
            if(!reader.ready())
            {
                throw new Exception("Expected target pattern");
            }
            String target = reader.readLine();
            patternList.add(source);
            targetMap.put(source, target);
        }
        is.close();
        processDirectory(baseInDir);
        System.out.println("finished at: "+ new Date());
    }
    
    public void processDirectory(File dir)
        throws Exception
    {
        File[] children = dir.listFiles();
        for(int i = 0; i < children.length; i++)
        {
            if(children[i].isDirectory())
            {
                processDirectory(children[i]);
            }
            else
            {
                processFile(children[i]);
            }
        }
    }
    
    private String readFile(File file, String encoding)
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
    
    private void writeFile(File file, String string)
        throws IOException
    {
        if(!file.exists())
        {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        Writer writer = new OutputStreamWriter(
            new BufferedOutputStream(new FileOutputStream(file)), OUTPUT_ENCODING);
        writer.append(string);
        writer.close();
    }
    
    private static final String IMMUTABLE_MARKER = "ConvertTemplates:IMMUTABLE";
    
    private void writeFileIfDifferent(File file, String newContents)
        throws IOException
    {
        String oldContents = null;
        if(file.exists())
        {
            oldContents = readFile(file, OUTPUT_ENCODING);
            if(oldContents.contains(IMMUTABLE_MARKER))
            {
                return;
            }
            newContents = preserveCVSId(oldContents, newContents);
            if(oldContents.equals(newContents))
            {
                return;
            }
        }
        writeFile(file, newContents);
    }
    
    private final Pattern CVS_ID_PATTERN = Pattern.compile("\\$Id[^$]*\\$");
    
    private String preserveCVSId(String oldContents, String newContents)
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
    
    public void processFile(File file)
        throws Exception
    {
        if(!file.getPath().endsWith(".vt"))
        {
            return;
        }
        String outPath = null;
        if(sites)
        {
            outPath = getSitesPath(file.getPath());
        }
        else
        {
            outPath = getOutPath(file.getPath());
        }
        
        boolean pathTest = false;
        if(pathTest)
        {
            System.out.println(outPath);
        }
        else
        {
            String content = readFile(file, INPUT_ENCODING);
            Iterator<String> it = patternList.iterator();
            while(it.hasNext())
            {
                String next = it.next();
                content = content.replaceAll(next, targetMap.get(next));
            }
            writeFileIfDifferent(new File(outPath), content);
        }
    }
    

    private String getSitesPath(String in)
    {
        return baseOutDir.getPath()+in.substring(baseInDir.getPath().length());
    }
    
    private String getOutPath(String in)
    {
        String source = in;
        int index = source.lastIndexOf("/");
        String base = source.substring(0, index+1);
        String rest = source.substring(index+1);
        rest = camelCase(rest);
        source = base + rest;
        if(source.indexOf("/messages/") > 0)
        {
            if(source.indexOf("pl_PL_HTML/messages") > 0)
            {
                source = source.replaceAll("pl_PL_HTML/messages","messages/HTML");
                source = source.replaceAll(".vt",".pl_PL.vt");
            }
            if(source.indexOf("pl_PL_PLAIN/messages") > 0)
            {
                source = source.replaceAll("pl_PL_PLAIN/messages","messages/PLAIN");
                source = source.replaceAll(".vt",".pl_PL.vt");
            }
            if(source.indexOf("en_US_HTML/messages") > 0)
            {
                source = source.replaceAll("en_US_HTML/messages","messages/HTML");
            }
            if(source.indexOf("en_US_PLAIN/messages") > 0)
            {
                source = source.replaceAll("en_US_PLAIN/messages","messages/PLAIN");
            }
        }
        else
        {
            if(source.indexOf("pl_PL_HTML") > 0)
            {
                source = source.replaceAll("pl_PL_HTML/","");
                source = source.replaceAll(".vt",".pl_PL.vt");
            }
            if(source.indexOf("pl_PL_PLAIN") > 0)
            {
                source = source.replaceAll("pl_PL_PLAIN/","");
                source = source.replaceAll(".vt",".pl_PL.vt");
            }
            if(source.indexOf("en_US_HTML") > 0)
            {
                source = source.replaceAll("en_US_HTML/", "");
            }
            if(source.indexOf("en_US_PLAIN") > 0)
            {
                source = source.replaceAll("en_US_PLAIN/", "");
            }
            source = source.replaceAll("screens/","views/");
        }
        if(source.indexOf("/cms/") > 0)
        {
            source = source.replaceAll("/cms/", "/");
        }
        return baseOutDir.getPath()+source.substring(baseInDir.getPath().length());
    }
    
    /**
     * Convert string to camel case.
     * 
     * @param input the input string.
     * @return the converted string.
     */
    private String camelCase(String input)
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
}
