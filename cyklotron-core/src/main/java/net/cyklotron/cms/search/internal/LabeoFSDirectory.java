package net.cyklotron.cms.search.internal;

import java.io.IOException;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.InputStream;
import org.apache.lucene.store.Lock;
import org.apache.lucene.store.OutputStream;

import net.labeo.LabeoRuntimeException;
import net.labeo.services.file.FileService;
import net.labeo.services.file.RandomAccess;

/**
 * An implementation of lucene's directory containing an index using Labeo's file service 
 * facilities.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: LabeoFSDirectory.java,v 1.1 2005-01-12 20:44:34 pablo Exp $
 */
public class LabeoFSDirectory extends Directory
{
    /** the file service for file operations */
    private FileService fileService;

    /** name of the base dir */
    private String baseDirName;

    public LabeoFSDirectory(FileService fileService, String baseDirName)
    {
        this.fileService = fileService;
        this.baseDirName = baseDirName;
    }

    public void close()
    throws IOException
    {
        // TODO: check if this is really needed
    }

    public OutputStream createFile(String str)
    throws IOException
    {
        return new LabeoFSOutputStream(getPath(str));
    }

    public void deleteFile(String str)
    throws IOException
    {
        fileService.delete(getPath(str));
    }

    public boolean fileExists(String str)
    throws IOException
    {
        return fileService.exists(getPath(str));
    }

    public long fileLength(String str)
    throws IOException
    {
        return fileService.length(getPath(str));
    }

    public long fileModified(String str)
    throws IOException
    {
        return fileService.lastModified(getPath(str));
    }

    public String[] list()
    throws IOException
    {
        return fileService.list(baseDirName);
    }

    public Lock makeLock(String str)
    {
        return new LabeoFSLock(getPath(str));
    }

    public InputStream openFile(String str)
    throws IOException
    {
        return new LabeoFSInputStream(getPath(str));
    }

    public void renameFile(String str, String str1)
    throws IOException
    {
        fileService.rename(getPath(str), getPath(str1));
    }

    public void touchFile(String str)
    throws IOException
    {
        // TODO: wonder if it will work
        java.io.OutputStream os = fileService.getOutputStream(getPath(str), true);
        os.close();
    }

    // implementation /////////////////////////////////////////////////////////////////////////

    private final String getPath(String fileName)
    {
        return baseDirName+"/"+fileName;
    }

    private class LabeoFSOutputStream extends OutputStream
    {
        private String path;
        private RandomAccess randomAccess;
        
        public LabeoFSOutputStream(String path)
        {
            this.path = path;
            this.randomAccess = fileService.getRandomAccess(path, "rw");
        }

        protected final void flushBuffer(byte[] b, int len)
        throws IOException
        {
            randomAccess.write(b, 0, len);
        }

        public final void close()
        throws IOException
        {
            super.close();
            randomAccess.close();
        }

        /** Random-access methods */
        public final void seek(long pos) throws IOException
        {
            super.seek(pos);
            randomAccess.seek(pos);
        }
        public long length()
        throws IOException
        {
            return randomAccess.length();
        }

        protected final void finalize()
        throws IOException
        {
            randomAccess.close();   // close the file
        }
    }

    private class LabeoFSInputStream
    extends InputStream
    {
        private String path;
        private RandomAccess randomAccess;
        private boolean isClone;
        
        public LabeoFSInputStream(String path)
        throws IOException
        {
            this.path = path;
            this.randomAccess = fileService.getRandomAccess(path, "r");
            length = randomAccess.length();
            isClone = false;
        }

        public void close()
        throws IOException
        {
            if(!isClone)
            {
                randomAccess.close();
            }
        }
        
        protected void readInternal(byte[] b, int offset, int len) throws IOException
        {
            long position = getFilePointer();
            if(position != randomAccess.getFilePointer())
            {
                randomAccess.seek(position);
            }
            int total = 0;
            do
            {
                int i = randomAccess.read(b, offset+total, len-total);
                if (i == -1)
                {
                    throw new IOException("read past EOF");
                }
                total += i;
            } while (total < len);
        }
        
        protected void seekInternal(long pos) throws IOException
        {
            randomAccess.seek(pos);
        }

        protected final void finalize()
        throws IOException
        {
            close();
        }
        
        public Object clone()
        {
            LabeoFSInputStream clone = (LabeoFSInputStream)super.clone();
            clone.isClone = true;
            return clone;
        }
    }
    
    private class LabeoFSLock extends Lock
    {
        private String path;
        
        public LabeoFSLock(String path)
        {
            this.path = path;
        }
        
        public boolean obtain() throws IOException
        {
            return fileService.createNewFile(path);
        }
        
        public void release()
        {
            try
            {
                fileService.delete(path);
            }
            catch(java.io.IOException e)
            {
                throw new LabeoRuntimeException("cannot delete index lock file with path='"+
                    path+"'", e);
            }
        }
        
        public boolean isLocked()
        {
            return fileService.exists(path);
        }
    };
}
