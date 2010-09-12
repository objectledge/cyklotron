/**
 * 
 */
package net.cyklotron.cms.ngodatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.util.PDFStreamEngine;
import org.apache.pdfbox.util.ResourceLoader;
import org.apache.pdfbox.util.TextPosition;
import org.jcontainer.dna.Logger;
import org.objectledge.filesystem.FileSystem;

class PNASourceParser
{
    private final String startMarker = "Część 1";

    private final String stopMarker = "Część 2";

    private final String[] headings = new String[] { "PNA", "Miejscowość", "Ulica", "Numery",
    "Gmina", "Powiat", "Województwo" };

    private final float tolerance = 2.0f;

    private final Logger logger;

    private final FileSystem fileSystem;

    // when true, we are between startMarker and stopMarker in the file
    private boolean active = false;

    private boolean firstPage;

    public PNASourceParser(FileSystem fileSystem, Logger logger)
    {
        this.logger = logger;
        this.fileSystem = fileSystem;
    }

    @SuppressWarnings("unchecked")
    public void parse(String sourceLocation)
        throws IOException
    {
        PNASourceParser.Timer timer = new Timer();
        PDDocument doc = PDDocument.load(fileSystem.getInputStream(sourceLocation));
        try
        {
            if(doc.isEncrypted())
            {
                try
                {
                    doc.decrypt("");
                }
                catch(Exception e)
                {
                    throw new IOException("failed to decrypt source file", e);
                }
            }
            List<PDPage> pages = doc.getDocumentCatalog().getAllPages();
            logger.info("loaded " + pages.size() + " pages in " + timer.getElapsedSeconds()
                + "s");
            PNASourceParser.PageProcessor pageProcessor = new PageProcessor(tolerance);
            int pageNum = 1;
            int analyzedPageCount = 0;
            int rowCount = 0;
            for(PDPage page : pages)
            {
                PDStream pdStream = page.getContents();
                if(pdStream != null)
                {
                    COSStream cosStream = pdStream.getStream();
                    pageProcessor.clear();
                    pageProcessor.processStream(page, page.findResources(), cosStream);
                    if(pageProcessor.containsTextItem(startMarker))
                    {
                        active = true;
                        firstPage = true;
                    }
                    else
                    {
                        firstPage = false;
                    }
                    if(pageProcessor.containsTextItem(stopMarker))
                    {
                        active = false;
                    }                        
                    if(logger.isDebugEnabled())
                    {
                        logger.debug("page " + pageNum + "\n" + pageProcessor.dumpPage());
                    }
                    if(active)
                    {
                        float[] columns = pageProcessor.findColumns(headings);
                        if(columns == null)
                        {
                            logger.error("failed to detect headings " + headings + " in page "
                                + pageNum + "\n" + pageProcessor.dumpPage());
                            throw new IOException("failed to parse page " + pageNum
                                + "headings not found");
                        }
                        List<List<PNASourceParser.TextItem>> grid = pageProcessor.getContentGrid(columns,
                            firstPage ? 4 : 2, 1);
                        grid = PageProcessor.mergeContinuations(grid);
                        rowCount += grid.size();
                        if(logger.isDebugEnabled())
                        {
                            String s = PageProcessor.dumpGrid(grid);
                            logger.debug(s);
                        }
                        analyzedPageCount++;
                    }
                }
                pageNum++;
            }
            logger.info("analyzed " + analyzedPageCount + " out of " + pages.size()+ " pages, found " + rowCount + " rows in " + timer.getElapsedSeconds()
                + "s");
        }
        finally
        {
            doc.close();
        }
    }

    private static class Timer
    {
        private long time = System.currentTimeMillis();
    
        public long getElapsedSeconds()
        {
            long lastTime = time;
            time = System.currentTimeMillis();
            return (time - lastTime) / 1000;
        }
    }

    private static class TextItem
    {
        String text;
    
        float x, y, w;
    
        public TextItem(TextPosition textPosition)
        {
            text = fixText(textPosition.getCharacter());
            x = textPosition.getX();
            y = textPosition.getY();
            w = textPosition.getWidth();
        }
    
        // hack, probably could be fixed properly by tweaking operator setup
        private static String fixText(String text)
        {
            return text.replace("hyphen", "-").replace("Ŝ", "ż");
        }
    
        public String getText()
        {
            return text;
        }
    
        public float getX()
        {
            return x;
        }
    
        public float getY()
        {
            return y;
        }
    
        public boolean isAdjecent(PNASourceParser.TextItem item)
        {
            return (item.y == this.y) && (item.x == this.x + this.w);
        }
    
        public void append(PNASourceParser.TextItem item)
        {
            text = text + item.text;
        }
    
        public static final Comparator<PNASourceParser.TextItem> HORIZONTAL_ORDER = new Comparator<PNASourceParser.TextItem>()
            {
                @Override
                public int compare(PNASourceParser.TextItem item1, PNASourceParser.TextItem item2)
                {
                    return (int)(item1.x - item2.x);
                }
            };
    
        public String toString()
        {
            return text;
        }
    }

    private static class PageProcessor
        extends PDFStreamEngine
    {
        private final SortedMap<Float, List<PNASourceParser.TextItem>> pageContents = new TreeMap<Float, List<PNASourceParser.TextItem>>();
    
        private PNASourceParser.TextItem lastItem;
    
        private final float tolerance;
    
        public PageProcessor(float tolerance)
            throws IOException
        {
            super(ResourceLoader.loadProperties("Resources/PDFTextStripper.properties", true));
            this.tolerance = tolerance;
        }
    
        @Override
        protected void processTextPosition(TextPosition text)
        {
            PNASourceParser.TextItem item = new TextItem(text);
            if(lastItem != null && lastItem.isAdjecent(item))
            {
                lastItem.append(item);
            }
            else
            {
                Float rowY = findRow(item.getY());
                List<PNASourceParser.TextItem> row = pageContents.get(rowY);
                if(row == null)
                {
                    row = new ArrayList<PNASourceParser.TextItem>();
                    pageContents.put(rowY, row);
                }
                row.add(item);
                lastItem = item;
            }
        }
    
        private Float findRow(float y)
        {
            for(Float rowY : pageContents.keySet())
            {
                if(Math.abs(rowY - y) < tolerance)
                {
                    return rowY;
                }
            }
            return y;
        }    
    
        public String dumpPage()
        {
            StringBuilder buff = new StringBuilder();
            for(float y : pageContents.keySet())
            {
                buff.append("Y: ").append(y).append(" ");
                List<PNASourceParser.TextItem> row = pageContents.get(y);
                Collections.sort(row, TextItem.HORIZONTAL_ORDER);
                for(PNASourceParser.TextItem item : row)
                {
                    buff.append("X: ").append(item.getX()).append(" ");
                    buff.append(item.getText());
                    buff.append(" ");
                }
                buff.append("\n");
            }
            return buff.toString();
        }
    
        public static String dumpGrid(List<List<PNASourceParser.TextItem>> grid)
        {
            StringBuilder buff = new StringBuilder();
            for(List<PNASourceParser.TextItem> row : grid)
            {
                for(PNASourceParser.TextItem item : row)
                {
                    if(item != null)
                    {
                        buff.append(item.getText());
                    }
                    else
                    {
                        buff.append("null");
                    }
                    buff.append(", ");
                }
                // trim last separator
                buff.setLength(buff.length() - 2);
                buff.append("\n");
            }
            String s = buff.toString();
            return s;
        }
    
        public static List<List<PNASourceParser.TextItem>> mergeContinuations(List<List<PNASourceParser.TextItem>> grid)
        {
            List<List<PNASourceParser.TextItem>> merged = new ArrayList<List<PNASourceParser.TextItem>>();
            List<PNASourceParser.TextItem> lastRow = null;
            for(List<PNASourceParser.TextItem> row : grid)
            {
                if(row.size() > 0 && row.get(0) == null && lastRow != null && lastRow.size() >= row.size())
                {
                    for(int i = 0; i < lastRow.size() && i < row.size(); i++)
                    {
                        if(row.get(i) != null)
                        {
                            lastRow.get(i).append(row.get(i));
                        }
                    }
                }
                else
                {
                    merged.add(row);
                }
                lastRow = row;
            }
            return merged;
        }
    
        public boolean containsTextItem(String text)
        {
            for(List<PNASourceParser.TextItem> row : pageContents.values())
            {
                for(PNASourceParser.TextItem item : row)
                {
                    if(item.getText().equals(text))
                    {
                        return true;
                    }
                }
            }
            return false;
        }
    
        public float[] findColumns(String[] headings)
        {
            rowLoop: for(List<PNASourceParser.TextItem> row : pageContents.values())
            {
                if(row.size() == headings.length)
                {
                    Collections.sort(row, TextItem.HORIZONTAL_ORDER);
                    for(int i = 0; i < headings.length; i++)
                    {
                        if(!row.get(i).getText().equals(headings[i]))
                        {
                            continue rowLoop;
                        }
                    }
                    // found heading row
                    float[] columns = new float[headings.length];
                    for(int i = 0; i < headings.length; i++)
                    {
                        columns[i] = row.get(i).getX();
                    }
                    return columns;
                }
            }
            return null;
        }
    
        public List<List<PNASourceParser.TextItem>> getContentGrid(float[] columns, int skipTop, int skipBottom)
        {
            List<List<PNASourceParser.TextItem>> rows = new ArrayList<List<PNASourceParser.TextItem>>();
            for(List<PNASourceParser.TextItem> pageRow : pageContents.values())
            {
                List<PNASourceParser.TextItem> row = new ArrayList<PNASourceParser.TextItem>(columns.length);
                for(PNASourceParser.TextItem item : pageRow)
                {
                    for(int i = 0; i < columns.length; i++)
                    {
                        if(Math.abs(item.getX() - columns[i]) < tolerance)
                        {
                            while(row.size() <= i)
                            {
                                row.add(null);
                            }
                            row.set(i, item);
                        }
                    }
                }
                rows.add(row);
            }
            if(rows.size() >= skipTop + skipBottom)
            {
                return rows.subList(skipTop, rows.size() - skipBottom);
            }
            else
            {
                return rows;
            }
        }
    
        public void clear()
        {
            pageContents.clear();
            lastItem = null;
        }
    }
}