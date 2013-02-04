package net.cyklotron.cms.installer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class JarProcessor
{
    public static void process(File source, File overlay, File target, File workDir)
        throws IOException
    {
        if(!workDir.exists())
        {
            workDir.mkdirs();
        }
        else
        {
            if(workDir.isDirectory())
            {
                deleteContents(workDir);
            }
            else
            {
                throw new IOException(workDir + " is not a directory");
            }
        }
        if(!overlay.exists() || !overlay.isDirectory())
        {
            throw new IOException(overlay + " does not exist or is not a directory");
        }
        unpackJar(source, workDir);
        FileUtils.copyDirectory(overlay, workDir);
        packJar(workDir, target);
    }

    public static void unpackJar(File zip, File targetDirectory)
        throws IOException, FileNotFoundException
    {
        if(!zip.exists() || !zip.isFile())
        {
            throw new IOException(zip + " does not exits or is not a regular file");
        }
        if(!targetDirectory.isDirectory())
        {
            throw new IOException(targetDirectory + " is not a directory");
        }
        byte[] b = new byte[4096];
        try(ZipInputStream zis = new ZipInputStream(new BufferedInputStream(
            new FileInputStream(zip))))
        {
            ZipEntry ze;
            while((ze = zis.getNextEntry()) != null)
            {
                File p = new File(targetDirectory, ze.getName());
                if(ze.isDirectory())
                {
                    p.mkdirs();
                }
                else
                {
                    try(OutputStream os = new BufferedOutputStream(new FileOutputStream(p)))
                    {
                        int i;
                        while((i = zis.read(b)) >= 0)
                        {
                            os.write(b, 0, i);
                        }
                        os.flush();
                    }
                }
            }
        }
    }

    public static void deleteContents(final File dir)
        throws IOException
    {
        new DirectoryWalker<Void>()
            {
                @Override
                protected void handleFile(File file, int depth, Collection<Void> results)
                    throws IOException
                {
                    file.delete();
                }

                @Override
                protected void handleDirectoryEnd(File directory, int depth,
                    Collection<Void> results)
                    throws IOException
                {
                    if(depth > 0)
                    {
                        directory.delete();
                    }
                }

                public void walk()
                    throws IOException
                {
                    super.walk(dir, Collections.<Void> emptyList());
                }
            }.walk();
    }

    public static String relativePath(File inner, File outer)
        throws IOException
    {
        return outer.toPath().relativize(inner.toPath()).toString();
    }

    public static void packJar(final File sourceDir, File target)
        throws IOException
    {
        if(!sourceDir.exists() || !sourceDir.isDirectory())
        {
            throw new IOException(sourceDir + " does not exist or is not a directory");
        }
        Manifest manifest;
        try(InputStream is = new BufferedInputStream(new FileInputStream(new File(sourceDir,
            "META-INF/MANIFEST.MF"))))
        {
            manifest = new Manifest(is);
        }
        try(final JarOutputStream jos = new JarOutputStream(new BufferedOutputStream(
            new FileOutputStream(target)), manifest))
        {
            new DirectoryWalker<Void>()
                {
                    @Override
                    protected void handleFile(File file, int depth, Collection<Void> results)
                        throws IOException
                    {
                        final String rel = relativePath(file, sourceDir);
                        if(!rel.equals("META-INF/MANIFEST.MF"))
                        {
                            ZipEntry ze = new ZipEntry(rel);
                            jos.putNextEntry(ze);
                            try(InputStream is = new BufferedInputStream(new FileInputStream(file)))
                            {
                                IOUtils.copy(is, jos);
                            }
                        }
                    }

                    @Override
                    protected void handleDirectoryStart(File directory, int depth,
                        Collection<Void> results)
                        throws IOException
                    {
                        if(depth > 0)
                        {
                            ZipEntry ze = new ZipEntry(relativePath(directory, sourceDir) + "/");
                            jos.putNextEntry(ze);
                        }
                    }

                    public void walk()
                        throws IOException
                    {
                        super.walk(sourceDir, Collections.<Void> emptyList());
                    }
                }.walk();
        }
    }
}
