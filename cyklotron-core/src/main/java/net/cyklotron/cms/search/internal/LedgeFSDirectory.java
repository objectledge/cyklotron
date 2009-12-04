package net.cyklotron.cms.search.internal;

import java.io.IOException;

import org.apache.lucene.store.BufferedIndexInput;
import org.apache.lucene.store.BufferedIndexOutput;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.Lock;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.RandomAccessFile;

/**
 * An implementation of lucene's directory containing an index using file service 
 * facilities.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: LedgeFSDirectory.java,v 1.5 2005-08-10 05:31:06 rafal Exp $
 */
public class LedgeFSDirectory extends Directory
{
    /** the file service for file operations */
    private FileSystem fileSystem;

    /** name of the base dir */
    private String baseDirName;

    public LedgeFSDirectory(FileSystem fileSystem, String baseDirName)
    {
        this.fileSystem = fileSystem;
        this.baseDirName = baseDirName;
    }

    @Override
    public void close()
    throws IOException
    {
    }

    public BufferedIndexOutput createFile(String str)
    throws IOException
    {
        return new LedgeFSOutputStream(getPath(str));
    }

    @Override
    public void deleteFile(String str)
    throws IOException
    {
        fileSystem.delete(getPath(str));
    }

    @Override
    public boolean fileExists(String str)
    throws IOException
    {
        return fileSystem.exists(getPath(str));
    }

    @Override
    public long fileLength(String str)
    throws IOException
    {
        return fileSystem.length(getPath(str));
    }

    @Override
    public long fileModified(String str)
    throws IOException
    {
        return fileSystem.lastModified(getPath(str));
    }

    @Override
    public String[] list()
    throws IOException
    {
        return fileSystem.list(baseDirName);
    }

    @Override
    public Lock makeLock(String str)
    {
        return new LedgeFSLock(getPath(str));
    }

    public BufferedIndexInput openFile(String str)
    throws IOException
    {
        return new LedgeFSInputStream(getPath(str));
    }

    @Override
    public void renameFile(String str, String str1)
        throws IOException
    {
        try
        {
            fileSystem.rename(getPath(str), getPath(str1));
        }
        catch(Exception e)
        {
            IOException ee = new IOException("exception occured");
            ee.initCause(e);
            throw ee;
        }
    }

    @Override
    public void touchFile(String str)
    throws IOException
    {
        java.io.OutputStream os = fileSystem.getOutputStream(getPath(str), true);
        os.close();
    }

    // implementation /////////////////////////////////////////////////////////////////////////

    private final String getPath(String fileName)
    {
        return baseDirName+"/"+fileName;
    }

    private class LedgeFSOutputStream
        extends BufferedIndexOutput
    {
        private String path;
        private RandomAccessFile randomAccess;
        
        public LedgeFSOutputStream(String path)
        {
            this.path = path;
            this.randomAccess = fileSystem.getRandomAccess(path, "rw");
        }

        @Override
        protected final void flushBuffer(byte[] b, int pos, int len)
        throws IOException
        {
            randomAccess.write(b, pos, len);
        }

        @Override
        public final void close()
        throws IOException
        {
            super.close();
            randomAccess.close();
        }

        /** Random-access methods */
        @Override
        public final void seek(long pos) throws IOException
        {
            super.seek(pos);
            randomAccess.seek(pos);
        }
        @Override
        public long length()
        throws IOException
        {
            return randomAccess.length();
        }

        @Override
        protected final void finalize()
        throws IOException
        {
            randomAccess.close();   // close the file
        }

    }

    private class LedgeFSInputStream
        extends BufferedIndexInput
    {
        private String path;
        private RandomAccessFile randomAccess;
        private long length;
        private boolean isClone;
        
        public LedgeFSInputStream(String path)
        throws IOException
        {
            this.path = path;
            this.randomAccess = fileSystem.getRandomAccess(path, "r");
            this.length = randomAccess.length();
            isClone = false;
        }

        @Override
        public void close()
        throws IOException
        {
            if(!isClone)
            {
                randomAccess.close();
            }
        }
        
        @Override
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
        
        @Override
        protected void seekInternal(long pos) throws IOException
        {
            randomAccess.seek(pos);
        }

        @Override
        protected final void finalize()
        throws IOException
        {
            close();
        }
        
        @Override
        public Object clone()
        {
            LedgeFSInputStream clone = (LedgeFSInputStream)super.clone();
            clone.isClone = true;
            return clone;
        }

        @Override
        public long length()
        {
            long length = 0L;
            try
            {
                length = randomAccess.length();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
            return length;
        }
    }
    
    private class LedgeFSLock extends Lock
    {
        private String path;
        
        public LedgeFSLock(String path)
        {
            this.path = path;
        }
        
        @Override
        public boolean obtain() throws IOException
        {
            try
            {
                return fileSystem.createNewFile(path);
            }
            catch(Exception e)
            {
                IOException ee = new IOException("exception occured");
                ee.initCause(e);
                throw ee;
            }
        }
        
        @Override
        public void release()
        {
            try
            {
                fileSystem.delete(path);
            }
            catch(java.io.IOException e)
            {
                throw new RuntimeException("cannot delete index lock file with path='"+
                    path+"'", e);
            }
        }
        
        @Override
        public boolean isLocked()
        {
            return fileSystem.exists(path);
        }
    }

    @Override
    public IndexOutput createOutput(String str)
        throws IOException
    {
        return new LedgeFSOutputStream(getPath(str));
    }

    @Override
    public IndexInput openInput(String str)
        throws IOException
    {
        if(!fileExists(str))
        {
            throw new IOException(); // touchFile(str);
        }
        return new LedgeFSInputStream(getPath(str));
    }
}
