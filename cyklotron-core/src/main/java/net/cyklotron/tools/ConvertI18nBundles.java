package net.cyklotron.tools;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.objectledge.parameters.DefaultParameters;
import org.objectledge.parameters.Parameters;
import org.objectledge.utils.StringUtils;
import org.xml.sax.AttributeList;
import org.xml.sax.HandlerBase;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ConvertI18nBundles
{
    private static final String INPUT_ENCODING = "ISO-8859-2";
    
    private static final String OUTPUT_ENCODING = "UTF-8";
    
    private File baseInDir;
    
    private File baseOutDir;
    
    /** the parser */
    private SAXParser parser;

    /** the handler */
    private SAXEventHandler handler;
    
    private Map keys = new HashMap();
    private Map paramMap = new HashMap();
    
    private Set<String> subSet = new HashSet<String>();
    
    public ConvertI18nBundles(File in, File out)
        throws Exception
    {
        baseInDir = in;
        baseOutDir = out;
        handler = new SAXEventHandler();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        parser = factory.newSAXParser();
        subSet.add("appearance");
        subSet.add("applications");
        subSet.add("document");
        subSet.add("integration");
        subSet.add("messages");
        subSet.add("periodicals");
        subSet.add("results");
        subSet.add("security");
        subSet.add("workflow");
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
        ConvertI18nBundles ct = new ConvertI18nBundles(baseInDir, baseOutDir);
        ct.execute(regexpPath);
    }
    
    public void execute(String srcPath)
        throws Exception
    {

        System.out.println("started at: "+new Date());
        processDirectory(baseInDir);

        Iterator i = keys.keySet().iterator();
        while(i.hasNext())
        {
            Locale l = (Locale)i.next();
            Map values = (Map)keys.get(l);
            Parameters params = new DefaultParameters();
            
            ///System.out.println("locale "+l.toString()+" size:"+values.size());
            Iterator ii = values.keySet().iterator();
            while(ii.hasNext())
            {
                String key = (String)ii.next();
                String value = (String)values.get(key);
                key = key.replace(",",".");
                params.set(key, value);
            }
            Parameters scoped = params.getChild("cms.");
            //build sub sets
            for(String prefix: subSet)
            {
                dumpData(scoped.getChild(prefix+"."), prefix, l); 
            }
            Parameters global = new DefaultParameters();
            String[] keys = scoped.getParameterNames();
            outer: for(String key: keys)
            {
                boolean add = true;
                for(String prefix: subSet)
                {
                    if(key.startsWith(prefix))
                    {
                        add = false;
                        break;
                    }
                }
                if(add)
                {
                    global.set(key, scoped.get(key));
                }
            }
            dumpData(global, "", l);
        }
    }

    public void dumpData(Parameters scoped, String prefix, Locale l)
        throws Exception
    {
        String[] keys = scoped.getParameterNames();
        Node root = new Node("","");
        for(String key: keys)
        {
            addToTree(root, key, scoped.get(key));
        }
        StringBuffer sb = new StringBuffer();
        printTree(sb, root, 0);
        String fileName = baseOutDir.getPath();
        if(prefix.length()== 0)
        {
            fileName = fileName+"/cms_"+l.toString()+".xml";
        }
        else
        {
            fileName = fileName+"/cms."+prefix+"_"+l.toString()+".xml";
        }
        File outFile = new File(fileName);
        outFile.getParentFile().mkdirs();
        outFile.createNewFile();
        OutputStream os = new FileOutputStream(outFile);
        os.write(sb.toString().getBytes(OUTPUT_ENCODING));
        //System.out.println("FILE "+l.toString()+ ".xml\n"+sb.toString());
        os.close();
        // here dump data to files   cms_xx_XX.xml
    }
    
    public void printTree(StringBuffer sb, Node node, int depth)
    {
        if(depth == 1)
        {
            System.out.println(node.key);
        }
        if(depth == 0)
        {
            sb.append("<strings>\n");
        }
        else
        {
            for(int i = 0; i < depth; i++)
            {
                sb.append("  ");
            }
            sb.append("<prefix name=\"");
            sb.append(node.key);
            sb.append("\">");
            sb.append("\n");
            if(node.value != null)
            {
                for(int i = 0; i < depth+1; i++)
                {
                    sb.append("  ");
                }
                sb.append("<value>");
                sb.append(node.value);
                sb.append("</value>\n");
            }
        }
        List keys = new ArrayList(node.children.keySet());
        Collections.sort(keys);
        Iterator it = keys.iterator();
        while(it.hasNext())
        {
            printTree(sb, (Node)node.children.get(it.next()),depth+1);
        }
        if(depth == 0)
        {
            sb.append("</strings>\n");
        }
        else
        {
            for(int i = 0; i < depth; i++)
            {
                sb.append("  ");
            }
            sb.append("</prefix>\n");
        }
    }
    
    public void addToTree(Node root, String key, String value)
    {
        StringTokenizer st = new StringTokenizer(key, ".");
        int count = st.countTokens();
        String prefix = st.nextToken();
        if(st.hasMoreTokens())
        {
            Node node = createNode(root, prefix, null);
            addToTree(node, key.substring(prefix.length()+1), value);
        }
        else
        {
            createNode(root, prefix, value);
        }
    }
    
    public Node createNode(Node root, String key, String value)
    {
        Node node = (Node)root.children.get(key);
        if(node != null)
        {
            if(value != null)
            {
                node.value = value;
            }
        }
        else
        {
            node = new Node(key, value);
            root.children.put(key, node);
        }
        return node;
    }
    
    public void processDirectory(File dir)
        throws Exception
    {
        System.out.println("dir:"+dir.getPath());
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
    
    public void processFile(File file)
        throws Exception
    {
        if(!file.getPath().endsWith(".xml"))
        {
            return;
        }
        String outPath = getOutPath(file.getPath());
        
        boolean pathTest = false;
        if(pathTest)
        {
            System.out.println(outPath);
        }
        else
        {
            int counter = 0;
            InputStream is = new FileInputStream(file);
            Map map = loadBundle(is, file.getName());
            merge(map, keys);
            is.close();
        }
    }
    
    private String getOutPath(String source)
    {
        return baseOutDir.getPath()+source;
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

    /**
     * Merges two string maps together
     * 
     * @param from source map
     * @param to destination map
     */
    protected void merge(Map from, Map to)
    {
        Iterator i = from.keySet().iterator();
        while(i.hasNext())
        {
            Locale l = (Locale)i.next();
            Map src = (Map)from.get(l);
            Map dest = (Map)to.get(l);
            if(dest == null)
            {
                to.put(l, src);
            }
            else
            {
                dest.putAll(src);
            }
        }
    }

    /**
     * Load contents of a given bundle.
     * 
     * @param name the filename of hte bundle.
     * @return loaded strings (Map of Maps keyed by Locale)
     */
    protected Map loadBundle(InputStream is, String name)
        throws Exception
    {
        try
        {
            handler.init(name);
            parser.parse(is, handler);
        }
        catch(SAXException e)
        {
            if(e instanceof SAXParseException)
            {
                throw new Exception("error parsing "+name+
                                    " on line "+((SAXParseException)e).
                                    getLineNumber()+": "+e.getMessage(), e);
            }
        }
        return handler.getRepository();
    }

    protected class SAXEventHandler
    extends HandlerBase
    {
        /** current repository */
        private HashMap repository;
        /** current file name */
        private String fileName;
    
        /** the document locator */
        private Locator locator;
    
        /** prefix stack */
        private LinkedList prefix = new LinkedList();
    
        /** current locale */
        private String locale;
    
        /** current item */
        private String item;
    
        /** reusable string buffer */
        private StringBuffer buff = new StringBuffer();
    
        /** the name/locale -> inputfile:line map to detect collisions */
        private Map map = new HashMap();
    
        /**
         * Initializes the hadler before parsing a new file.
         *
         * @param name the file name, for reporting errors
         */
        public void init(String name)
        {
            fileName = name;
            item = null;
            locale = null;
            prefix.clear();
            repository = new HashMap();
            map.clear();
        }
    
        public Map getRepository()
        {
            return repository;
        }
    
        /**
         * Receive an object for locating the origin of SAX document events.
         *
         * @param locator An object that can return the location of any SAX
         * document event.
         */
        public void setDocumentLocator(Locator locator)
        {
            this.locator = locator;
        }
    
        /**
         * Receive notification of the beginning of an element. 
         *
         * @param tag The element type name.
         * @param attrs The attributes attached to the element, if any.
         */
        public void startElement(String tag, AttributeList attrs)
            throws SAXParseException
        {
            String name;
            if("strings".equals(tag))
            {
            }
            else if("prefix".equals(tag))
            {
                if(item != null)
                {
                    throw new SAXParseException("<prefix> cannot be nested inside <item>", 
                                                locator);
                }
                name = attrs.getValue("name");
                if(name == null)
                {
                    throw new SAXParseException("name attribute required", locator);
                }
                prefix.addLast(name);
            }
            else if("lang".equals(tag))
            {
                if(locale != null)
                {
                    throw new SAXParseException("<lang> can not be nested", locator);
                }
                name = attrs.getValue("name");
                if(name == null)
                {
                    throw new SAXParseException("name attribute required", locator);
                }
                locale = name;
            }
            else if("item".equals(tag))
            {
                if(item != null)
                {
                    throw new SAXParseException("<item> can not be nested", locator);
                }
                name = attrs.getValue("name");
                if(name == null)
                {
                    throw new SAXParseException("name attribute required", locator);
                }
                item = name;
            }
        }
    
        /**
         * Receive notification of the end of an element.
         *
         * @param tag The element type name.
         */
        public void endElement(String tag)
        {
            if("strings".equals(tag))
            {
            }
            else if("prefix".equals(tag))
            {
                prefix.removeLast();
            }
            else if("lang".equals(tag))
            {
                locale = null;
            }
            else if("item".equals(tag))
            {
                item = null;
            }
        }
    
        /**
         * Receive notification of character data.
         *
         * @param ch The characters from the XML document.
         * @param start The start position in the array.
         * @param length The number of characters to read from the array.
         */
        public void characters(char[] ch, int start, int length)
            throws SAXParseException
        {
            String str = new String(ch,start,length);
            str = str.trim();
            if(str.length()>0)
            {
                if(item==null || locale==null)
                {
                    throw new SAXParseException("strings need to be nested in <item> and <lang>", 
                                                locator);
                }
    
                buff.setLength(0);
                for(int i=0; i<prefix.size(); i++)
                {
                    buff.append(prefix.get(i)).append('.');
                }
                buff.append(item);
                String name = buff.toString();
    
                String key = name+"/"+locale;
                String here = fileName+":"+locator.getLineNumber();
                String loc = (String)map.get(key);
    
                Locale localeObj = StringUtils.getLocale(locale);
                Map lang = (Map)repository.get(localeObj);
                if(lang == null)
                {
                    lang = new HashMap();
                    repository.put(localeObj, lang);
                }
    
                if(loc != null)
                {
                    if(loc.equals(here))
                    {
                        // check for null
                        String sTemp1 = (String)lang.get(name);
                        if (sTemp1 != null)
                            lang.put(name, sTemp1 + str);
                        else
                            lang.put(name, str);
                    }
                    else
                    {
                        System.out.println("--"+loc+"--"+here+"--");
                        throw new SAXParseException(key+" already defined in "+loc, locator);
                    }
                }
                else
                {
                    map.put(key, here);
                    lang.put(name, str);
                }
            }
        }
    }
    
    protected class Node
    {
        String key;
        String value;
        Map children;
        public Node(String key, String value)
        {
            this.key = key;
            this.value = value;
            children = new HashMap();
        }
    }
}
