package net.cyklotron.tools;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
                if((sites || !children[i].getName().equals("layouts"))
                  && !children[i].getName().equals("messages")
                  && !children[i].getName().equals("sites")
                  && !children[i].getName().equals("default")
                  && !children[i].getName().equals("pages"))
                {
                    processDirectory(children[i]);
                }
            }
            else
            {
                processFile(children[i]);
            }
        }
    }
    
    private final String IMMUTABLE_MARKER = "ConvertTemplates:IMMUTABLE";
    
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
            String content = Utils.readFile(file, INPUT_ENCODING);
            Iterator<String> it = patternList.iterator();
            while(it.hasNext())
            {
                String next = it.next();
                content = content.replaceAll(next, targetMap.get(next));
            }
            Utils.writeFileIfDifferent(new File(outPath), content, OUTPUT_ENCODING, 
                IMMUTABLE_MARKER);
        }
    }
    

    private String getSitesPath(String in)
    {
        return baseOutDir.getPath()+in.substring(baseInDir.getPath().length());
    }
    
    private String getOutPath(String in)
    {
        if(in.indexOf("/cms/sites/") > 0)
        {
            return getSitesPath(in.replace("/cms/sites/", "/sites/"));
        }
        String source = Utils.camelCaseFileName(in);
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
}
