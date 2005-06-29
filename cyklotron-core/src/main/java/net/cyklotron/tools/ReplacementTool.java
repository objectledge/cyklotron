/**
 * 
 */
package net.cyklotron.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

/**
 * @author fil
 *
 */
public class ReplacementTool
{
    public static void main(String[] args)
        throws Exception
    {
        CommandLineParser parser = new PosixParser();
        Options options = new Options();
        options.addOption("e", true, "character encoding to use");
        options.addOption("f", true, "pattern flags");
        options.addOption("h", "help", false, "display help");
        CommandLine cmd = parser.parse(options, args);

        if(cmd.hasOption("h"))
        {
            showHelp(options);
            return;
        }
        String encoding =  cmd.getOptionValue("e", "UTF-8");        
        args = cmd.getArgs();
        if(args.length < 1)
        {
            showHelp(options);
            return;
        }
        File patterns = new File(args[0]);
        int flags = cmd.hasOption("f") ? Replacement.getFlags(cmd.getOptionValue("f")) : 0;
        List<Replacement> replacements = Replacement.parse(patterns, encoding, flags);
        InputStream in;
        OutputStream out;
        if(args.length >= 2)
        {
            in = new BufferedInputStream(new FileInputStream(args[1]));
        }
        else
        {
            in = System.in;
        }
        if(args.length >= 3)
        {
            out = new BufferedOutputStream(new FileOutputStream(args[2]));
        }
        else
        {
            out = System.out;
        }
        try
        {
            Replacement.apply(in, out, encoding, replacements);
        }
        finally
        {
            in.close();
            out.close();
        }
    }

    public static void showHelp(Options options)
    {
        new HelpFormatter().printHelp("ReplacementTool [-e encoding] patterns [in [out]]", 
            "", options, "if encoding is ommited, UTF-8 will be used. If in / out paths are ommited process input / output will be used instead.");
    }
}
