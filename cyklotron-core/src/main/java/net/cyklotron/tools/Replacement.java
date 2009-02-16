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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Replacement
{
    private final Pattern pattern;
    
    private final String replacement;
    
    public Replacement(String patternSource, String replacement, int flags)
        throws PatternSyntaxException
    {
        pattern = Pattern.compile(patternSource, flags);
        this.replacement = replacement; 
    }
    
    public Replacement(String patternSource, String replacement)
    {
        this(patternSource, replacement, 0);
    }
    
    public String apply(String s)
    {
        return pattern.matcher(s).replaceAll(replacement);
    }

    public static String apply(String s, List<Replacement> replacements)
    {
        for(Replacement r: replacements)
        {
            s = r.apply(s);
        }
        return s;
    }
    
    public static void apply(Reader r, Writer w, List<Replacement> replacements)
        throws IOException
    {
        LineNumberReader lr = new LineNumberReader(r);
        PrintWriter pw = new PrintWriter(w);
        while(lr.ready())
        {
            pw.println(apply(lr.readLine(), replacements));
        }
        pw.flush();
    }
    
    public static void apply(InputStream in, OutputStream out, String encoding,
        List<Replacement> replacements)
        throws IOException
    {
        Reader r = new InputStreamReader(in, encoding);
        Writer w = new OutputStreamWriter(out, encoding);
        apply(r, w, replacements);
    }
    
    public static void apply(File in, File out, String encoding, List<Replacement> replacements)
        throws IOException
    {
        Reader r = new InputStreamReader(new BufferedInputStream(new FileInputStream(in)), encoding);
        Writer w = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(out)), encoding);
        try
        {
            apply(r, w, replacements);
        }
        finally
        {
            r.close();
            w.close();
        }
    }
    
    public static List<Replacement> parse(File file, String encoding, int flags)
        throws IOException
    {
        List<Replacement> replacements = new ArrayList<Replacement>();
        LineNumberReader r = new LineNumberReader(new InputStreamReader(
            new BufferedInputStream(new FileInputStream(file)), encoding));
        while(r.ready())
        {
            String src = r.readLine();
            if(!r.ready())
            {
                throw new IOException("odd number of lines in " + file + " make sure that the last line is newline terminated");
            }
            String replacement = r.readLine();
            replacement = replacement.replace("\\n","\n");
            try
            {
                replacements.add(new Replacement(src, replacement, flags));
            }
            catch(PatternSyntaxException e)
            {
                throw new IOException("invalid pattern at line " + (r.getLineNumber()-1) + " " +
                    e.getMessage());
            }
        }
        return replacements;
    }
    
    /**
     * Parses a string for java.util.regex.Pattern flags.
     * 
     * <p>If the strings contains a flag letter an appropriate constant is or'ed into the return
     * value.</p>
     * 
     * <ul>
     *   <li>d, UNIX_LINES</li>
     *   <li>i, CASE_INSENSITIVE</li>
     *   <li>x, COMMENTS</li>
     *   <li>m, MULTILINE</li>
     *   <li>l, LITERAL *</li>
     *   <li>s, DOTALL</li>
     *   <li>u, UNICODE_CASE</li>
     *   <li>c, CANON_EQ *</li>
     * </li>
     *
     * <p>Flags marked with * are extensions beyound embedded options recoginzed by Pattern class.
     * See Pattern class JavaDoc for more details.</p>
     * 
     * @param flagString
     * @return
     */
    public static int getFlags(String flagString)
    {
        int flags = 0;
        if(flagString.contains("d"))
        {
            flags |= Pattern.UNIX_LINES;
        }
        if(flagString.contains("i"))
        {
            flags |= Pattern.CASE_INSENSITIVE;
        }
        if(flagString.contains("x"))
        {
            flags |= Pattern.COMMENTS;
        }
        if(flagString.contains("m"))
        {
            flags |= Pattern.MULTILINE;
        }
        if(flagString.contains("l"))
        {
            flags |= Pattern.LITERAL;
        }
        if(flagString.contains("s"))
        {
            flags |= Pattern.DOTALL;
        }
        if(flagString.contains("u"))
        {
            flags |= Pattern.UNICODE_CASE;
        }
        if(flagString.contains("c"))
        {
            flags |= Pattern.CANON_EQ;
        }
        return flags;
    }
}