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

public class MyTest2 extends TestCase
{
    public static void main( String args[] ) 
    {
        junit.textui.TestRunner.run( suite() );
    }
    
    public static TestSuite suite() 
    {
        return new TestSuite( MyTest2.class );
    }

    public MyTest2( String s ) 
    {
        super( s );
    }

    public void testGetForm() throws Exception 
    {
        WebConversation wc = new WebConversation();
        WebResponse response = wc.getResponse( "http://localhost:8080/test.html" ); // read this page
        WebLink link = response.getLinkWith("gotoFoo");
        link.click();                                                                       // follow it
        response = wc.getCurrentPage();
        System.out.println(response.getText());
    }
}
