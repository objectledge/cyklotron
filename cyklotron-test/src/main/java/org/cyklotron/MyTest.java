package org.cyklotron;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.meterware.httpunit.HTMLElementPredicate;
import com.meterware.httpunit.SubmitButton;
import com.meterware.httpunit.TableCell;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebLink;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;

public class MyTest extends TestCase
{
    public static void main( String args[] ) 
    {
        junit.textui.TestRunner.run( suite() );
    }
    
    public static TestSuite suite() 
    {
        return new TestSuite( MyTest.class );
    }

    public MyTest( String s ) 
    {
        super( s );
    }

    public void testGetForm() throws Exception 
    {
        WebConversation wc = new WebConversation();
        
        
        
        
        WebResponse response = wc.getResponse( "http://localhost:8080/cykloklon/labeo" ); // read this page
        WebLink link = response.getLinkWith( "Login" );                                // find the link
        assertNotNull( "No link Login found", link );
        link.click();                                                                       // follow it
        
        response = wc.getCurrentPage();
        WebForm form = response.getFormWithName( "loginform" ); 
        assertNotNull( "No form found with name 'loginform'", form );
        assertEquals( "Form method", "post", form.getMethod() );
        form.setParameter("LOGIN","root");
        form.setParameter("PASSWORD", "00000");
        SubmitButton[] buttons = form.getSubmitButtons();
        assertEquals(1, buttons.length);
        buttons[0].click();
        
        response = wc.getCurrentPage();
        form = response.getFormWithName( "loginform" ); 
        assertNotNull( "No form found with name 'loginform'", form );
        form.setParameter("LOGIN","root");
        form.setParameter("PASSWORD", "12345");
        buttons = form.getSubmitButtons();
        assertEquals(1, buttons.length);
        buttons[0].click();
        
        
        // expected siteList view
        /*
        response = wc.getCurrentPage();
        form = response.getFormWithName( "loginform" ); 
        assertNull( "Form with name 'loginform' found - should login", form );
        link = getLinkWithString(response, "site,AddSite");
        assertNotNull( "No link to add service found", link );
        link.click();                                                                       // follow it
        
        // expected add site view
        response = wc.getCurrentPage();
        form = response.getFormWithName( "form1" );
        assertNotNull( "No form found with name 'form1'", form );
        assertTrue("Parameter name does not exist in form", form.hasParameterNamed("name"));
        link = getLinkWithString(response, "javascript:addSite()");
        assertNotNull( "No link AddSite found", link );
        link.click();
        
        // expected add site view
        response = wc.getCurrentPage();
        form = response.getFormWithName( "form1" );
        assertNotNull( "No form found with name 'form1'", form );
        assertTrue("Parameter name does not exist in form", form.hasParameterNamed("name"));
        
        form.setParameter("name", ""+(new Date()).getTime());
        form.setParameter("owner", "root");
        form.setParameter("description", "new test site");
        String[] options = form.getOptionValues("template_id");
        assertTrue("No templates found", options.length > 0);
        form.setParameter("template_id", options[0]);
        link = getLinkWithString(response, "javascript:addSite()");
        assertNotNull( "No link AddSite found", link );
        link.click();                  
        
        // expected site list
        response = wc.getCurrentPage();
        form = response.getFormWithName( "form1" );
        assertNull( "Form with name 'form1' found", form );
        */
        
        
        // site list
        response = wc.getCurrentPage();
        WebTable table = response.getFirstMatchingTable(new TableClassPredicate("genericItemList"), null);
        assertNotNull("Table not found", table);
        TableCell cell = table.getTableCell(1, 0);
        WebLink[] links = cell.getLinks();
        assertEquals(1, links.length);
        links[0].click();
        
        // edit site
        response = wc.getCurrentPage();
        link = getLinkWithString(response, "poll,PoolList");
        assertNotNull("Poll application link not found", link);
        link.click();

        // pool list
        response = wc.getCurrentPage();
        form = response.getFormWithName( "form1" );
        assertNotNull( "No form found with name 'form1'", form );
        table = response.getFirstMatchingTable(new TableClassPredicate("genericItemList"), null);
        assertNotNull("Table not found", table);
        int tableRows = table.getRowCount();
        
        // if no pools defined
        if(table.getTableCell(1,0).getLinks().length == 0)
        {
            tableRows--;
        }
        
        
        form.setParameter("title", "pool1");
        form.setParameter("description", "new test pool");
        link = getLinkWithString(response, "javascript:sendmkdir()");
        assertNotNull( "No link to add pool found", link );
        link.click();                  
        
        // poll list
        response = wc.getCurrentPage();
        form = response.getFormWithName( "form1" );
        assertNotNull( "No form found with name 'form1'", form );
        table = response.getFirstMatchingTable(new TableClassPredicate("genericItemList"), null);
        assertNotNull("Table not found", table);
        //assertEquals(tableRows + 1, table.getRowCount());
        links = table.getTableCell(1,0).getLinks();
        assertEquals(1, links.length);
        links[0].click();
        
        // edit pool
        response = wc.getCurrentPage();
        table = response.getFirstMatchingTable(new TableClassSizePredicate(4,2,"genericScreen"), null);
        
        cell = table.getTableCell(2, 1);
        int pollCount = Integer.parseInt(cell.getText().trim());
        link = getLinkWithString(response, "poll,AddPoll");
        assertNotNull("Link to add poll to pool not found", link);
        link.click();
        
               
        // add poll
        
        response = wc.getCurrentPage();
        form = response.getFormWithName( "form1" );
        assertNotNull( "No form found with name 'form1'", form );
        form.setParameter("title", "poll1");
        form.setParameter("description", "new test poll");
        form.setParameter("question_0_title", "q1");
        form.setParameter("question_0_answer_0_title", "a1");
        form.setParameter("question_0_answer_1_title", "a2");
        
        link = getLinkWithString(response, "poll,AddAnswer");
        System.out.println(link.getText() + ":"+link.getURLString());
        link.click();
        
        
        // add poll 
        response = wc.getCurrentPage();
        form = response.getFormWithName( "form1" );
        form.setParameter("question_0_answer_2_title", "a3");
        link = getLinkWithString(response, "javascript:document.form1.submit()");
        link.click();

        // edit pool - check number of poll in pool
        response = wc.getCurrentPage();
        table = response.getFirstMatchingTable(new TableClassSizePredicate(4, 2, "genericScreen"), null);
        cell = table.getTableCell(2, 1);
        int pollCountAdded = Integer.parseInt(cell.getText().trim());
        assertEquals(pollCount+1, pollCountAdded);
    }
 
    
    /**
     * Get link that url contains the text.
     * 
     * @param response
     * @param text
     * @return
     * @throws Exception
     */
    protected WebLink getLinkWithString(WebResponse response, String text)
        throws Exception
    {
        WebLink[] links = response.getLinks();
        WebLink link = null;
        for(WebLink l: links)
        {
            if(l.getURLString().contains(text))
            {
                return l;
            }
        }
        return null;
    }
    
    
    public class CellPredicate implements HTMLElementPredicate
    {
        int column = 0;
        int row = 0;
        String text = "";
        
        public CellPredicate(int row, int column, String text)
        {
            this.column = column;
            this.row = row;
            this.text = text;
        }
        
        public boolean matchesCriteria(Object htmlElement, Object criteria)
        {
            if(!(htmlElement instanceof WebTable))
            {
                return false;
            }
            WebTable table = (WebTable)htmlElement;
            if(column >= table.getColumnCount() || row >= table.getRowCount())
            {
                return false;
            }
            String value = table.getCellAsText(row, column);
            if(value != null && value.contains(text))
            {
                return true;
            }
            return false;
        }
    }
    
    public class TableClassPredicate implements HTMLElementPredicate
    {
        String className = "";
        
        public TableClassPredicate(String className)
        {
            this.className = className;
        }
        
        public boolean matchesCriteria(Object htmlElement, Object criteria)
        {
            if(!(htmlElement instanceof WebTable))
            {
                return false;
            }
            WebTable table = (WebTable)htmlElement;
            String value = table.getClassName();
            if(value != null && value.equals(className))
            {
                return true;
            }
            return false;
        }
    }

    public class TableClassSizePredicate implements HTMLElementPredicate
    {
        String className = "";
        int column = 0;
        int row = 0;
        
        public TableClassSizePredicate(int row, int column, String className)
        {
            this.className = className;
            this.column = column;
            this.row = row;
        }
        
        public boolean matchesCriteria(Object htmlElement, Object criteria)
        {
            if(!(htmlElement instanceof WebTable))
            {
                return false;
            }
            WebTable table = (WebTable)htmlElement;
            String value = table.getClassName();
            if(value != null && value.equals(className))
            {
                return (table.getColumnCount() == column &&
                        table.getRowCount() == row);
            }
            return false;
        }
    }
}
