/**
 * 
 */
package net.cyklotron.cms.ngodatabase;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

public class PNASourceParser
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

    private List<String[]> content = new ArrayList<String[]>();

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
            logger.info("loaded " + pages.size() + " pages in " + timer.getElapsedSeconds() + "s");
            PNASourceParser.PageProcessor pageProcessor = new PageProcessor(tolerance);
            int pageNum = 1;
            int analyzedPageCount = 0;
            int rowCount = 0;
            content.clear();
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
                        List<String[]> grid = pageProcessor.getContentGrid(columns, firstPage ? 4
                            : 2, 1);
                        grid = mergeContinuations(grid);
                        content.addAll(grid);
                        rowCount += grid.size();
                        if(logger.isDebugEnabled())
                        {
                            StringWriter s = new StringWriter();
                            dump(grid, s);
                            logger.debug(s.toString());
                        }
                        analyzedPageCount++;
                    }
                }
                pageNum++;
            }
            logger.info("analyzed " + analyzedPageCount + " out of " + pages.size()
                + " pages, found " + rowCount + " rows in " + timer.getElapsedSeconds() + "s");
        }
        finally
        {
            doc.close();
        }
    }

    public String[] getHeadings()
    {
        return headings;
    }

    public List<String[]> getContent()
    {
        return content;
    }

    private List<String[]> mergeContinuations(List<String[]> grid)
    {
        List<String[]> merged = new ArrayList<String[]>();
        String[] lastRow = null;
        for(String[] row : grid)
        {
            if(row[0] == null && lastRow != null)
            {
                for(int i = 0; i < row.length; i++)
                {
                    if(lastRow[i] != null && row[i] != null)
                    {
                        lastRow[i] = lastRow[i] + row[i];
                    }
                }
            }
            else
            {
                merged.add(row);
                lastRow = row;
            }
        }
        return merged;
    }

    public static String dump(List<String[]> grid, Writer out)
    {
        PrintWriter buff = new PrintWriter(out);
        for(String[] row : grid)
        {
            for(int i = 0; i < row.length; i++)
            {
                if(row[i] != null)
                {
                    buff.append(row[i]);
                }
                if(i < row.length - 1)
                {
                    buff.append(";");
                }
            }
            buff.append("\n");
        }
        String s = buff.toString();
        return s;
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

        private static final Set<String> DONT_REPLACE = new HashSet<String>();
        
        static
        {
            DONT_REPLACE.add("śląski");
            DONT_REPLACE.add("świętokrzyski");

            DONT_REPLACE.add("świeck");
            DONT_REPLACE.add("świebodzińsk");
            DONT_REPLACE.add("świdnick");
            DONT_REPLACE.add("świdwińsk");
            DONT_REPLACE.add("średzk");
            DONT_REPLACE.add("śremsk");
        }
        
        // hack, probably could be fixed properly by tweaking operator setup
        private static String fixText(String text)
        {
            text = text.replace("hyphen", "-").replace("Ŝ", "ż").replace("(ś", "(Ż");
            if(text.startsWith("ś") && !DONT_REPLACE.contains(text))
            {
                text = "Ż" + text.substring(1);
            }
            return text;
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

        public boolean isAdjecent(TextItem item)
        {
            return (item.y == this.y) && (item.x == this.x + this.w);
        }

        public void append(TextItem item)
        {
            text = text + item.text;
        }

        public static final Comparator<TextItem> HORIZONTAL_ORDER = new Comparator<TextItem>()
            {
                @Override
                public int compare(TextItem item1, TextItem item2)
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
        private final SortedMap<Float, List<TextItem>> pageContents = new TreeMap<Float, List<TextItem>>();

        private TextItem lastItem;

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
            TextItem item = new TextItem(text);
            if(lastItem != null && lastItem.isAdjecent(item))
            {
                lastItem.append(item);
            }
            else
            {
                Float rowY = findRow(item.getY());
                List<TextItem> row = pageContents.get(rowY);
                if(row == null)
                {
                    row = new ArrayList<TextItem>();
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
                List<TextItem> row = pageContents.get(y);
                Collections.sort(row, TextItem.HORIZONTAL_ORDER);
                for(TextItem item : row)
                {
                    buff.append("X: ").append(item.getX()).append(" ");
                    buff.append(item.getText());
                    buff.append(" ");
                }
                buff.append("\n");
            }
            return buff.toString();
        }

        public boolean containsTextItem(String text)
        {
            for(List<TextItem> row : pageContents.values())
            {
                for(TextItem item : row)
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
            rowLoop: for(List<TextItem> row : pageContents.values())
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

        public List<String[]> getContentGrid(float[] columns, int skipTop, int skipBottom)
        {
            List<String[]> rows = new ArrayList<String[]>();
            for(List<TextItem> pageRow : pageContents.values())
            {
                String[] row = new String[columns.length];
                for(TextItem item : pageRow)
                {
                    for(int i = 0; i < columns.length; i++)
                    {
                        if(Math.abs(item.getX() - columns[i]) < tolerance)
                        {
                            row[i] = item.getText();
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
