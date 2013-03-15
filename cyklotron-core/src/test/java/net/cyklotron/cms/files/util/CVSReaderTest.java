package net.cyklotron.cms.files.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.SimpleLayout;
import org.jcontainer.dna.Logger;
import org.jcontainer.dna.impl.Log4JLogger;

import net.cyklotron.files.util.CSVReader;

public class CVSReaderTest
    extends TestCase
{
  
    private Logger log;

    public void setUp()
    {
        final org.apache.log4j.Logger log4jl = org.apache.log4j.Logger.getLogger(CSVReader.class);
        log4jl.setLevel(Level.DEBUG);
        Appender console = new ConsoleAppender(new SimpleLayout(), "System.out");
        log4jl.addAppender(console);
        log = new Log4JLogger(log4jl);
    }
	
	private Reader input(String name) throws IOException {
		log.debug("reading " + name);
		File f = new File("src/test/resources/csv/" + name);
		return new InputStreamReader(new BufferedInputStream(
				new FileInputStream(f)), "UTF-8");
	}

	/**
	 * Simple, correct.
	 */
	public void test1() throws IOException {
        CSVReader reader = new CSVReader(input("input1.csv"), ';');
        reader.setLog(log);
        List<List<String>> data = reader.readData();
		Assert.assertEquals(2, data.size());
		Assert.assertEquals(2, data.get(0).size());
		Assert.assertEquals(2, data.get(1).size());
		Assert.assertEquals("field1", data.get(0).get(0));
		Assert.assertEquals("field4", data.get(1).get(1));
	}

	/**
	 * Non terminated field at EOF.
	 */
	public void test2() throws IOException {
        CSVReader reader = new CSVReader(input("input2.csv"), ';');
        reader.setLog(log);
		List<List<String>> data = reader.readData();
		Assert.assertEquals(1, data.size());
		Assert.assertEquals(2, data.get(0).size());
		Assert.assertEquals("field1", data.get(0).get(0));
		Assert.assertEquals("field2", data.get(0).get(1));
	}

	/**
	 * Missing newline at EOF.
	 */
	public void test3() throws IOException {
        CSVReader reader = new CSVReader(input("input3.csv"), ';');
        reader.setLog(log);
		List<List<String>> data = reader.readData();
		Assert.assertEquals(2, data.size());
		Assert.assertEquals(2, data.get(0).size());
		Assert.assertEquals(2, data.get(1).size());
		Assert.assertEquals("field1", data.get(0).get(0));
		Assert.assertEquals("field4", data.get(1).get(1));
	}	

	/**
	 * Quoted, correct.
	 */
	public void test4() throws IOException {
        CSVReader reader = new CSVReader(input("input4.csv"), ';');
        reader.setLog(log);
		List<List<String>> data = reader.readData();
		Assert.assertEquals(1, data.size());
		Assert.assertEquals(3, data.get(0).size());
		Assert.assertEquals("field1", data.get(0).get(0));
		Assert.assertEquals("field2", data.get(0).get(1));
		Assert.assertEquals("field3", data.get(0).get(2));
	}
	
	/**
	 * Quoted, non-matched quote.
	 */
	public void test5() throws IOException {
        CSVReader reader = new CSVReader(input("input5.csv"), ';');
        reader.setLog(log);
		try {
			reader.readData();
			Assert.fail("should" +
					" throw");
		} catch(AssertionFailedError e) {
			throw e;
		} catch(Error e) {
			log.debug("caught", e);
			Assert.assertTrue("wrong exception message", e.getMessage().startsWith(("non matched quote")));
		}
	}
	
	/**
	 * Quoted, escaped quote.
	 */
	public void test6() throws IOException {
        CSVReader reader = new CSVReader(input("input6.csv"), ';');
        reader.setLog(log);
		List<List<String>> data = reader.readData();
		Assert.assertEquals(1, data.size());
		Assert.assertEquals(3, data.get(0).size());
		Assert.assertEquals("field1", data.get(0).get(0));
		Assert.assertEquals("field\"2", data.get(0).get(1));
		Assert.assertEquals("field3", data.get(0).get(2));
	}	

	/**
	 * Quoted, quoted newline.
	 */
	public void test7() throws IOException {
        CSVReader reader = new CSVReader(input("input7.csv"), ';');
        reader.setLog(log);
		List<List<String>> data = reader.readData();
		Assert.assertEquals(1, data.size());
		Assert.assertEquals(3, data.get(0).size());
		Assert.assertEquals("field1", data.get(0).get(0));
		Assert.assertEquals("field\n2", data.get(0).get(1));
		Assert.assertEquals("field3", data.get(0).get(2));
	}	
	
	
	/**
	 * Alternative separator, correct.
	 */
	public void test8() throws IOException {
        CSVReader reader = new CSVReader(input("input8.csv"), ',');
        reader.setLog(log);
		List<List<String>> data = reader.readData();
		Assert.assertEquals(2, data.size());
		Assert.assertEquals(2, data.get(0).size());
		Assert.assertEquals(2, data.get(1).size());
		Assert.assertEquals("field1", data.get(0).get(0));
		Assert.assertEquals("field4", data.get(1).get(1));
	}
	
	/**
	 * Alternative separator, separator mixing.
	 */
	public void test9() throws IOException {
        CSVReader reader = new CSVReader(input("input9.csv"), ',');
        reader.setLog(log);
		List<List<String>> data = reader.readData();
		Assert.assertEquals(2, data.size());
		Assert.assertEquals(2, data.get(0).size());
		Assert.assertEquals(2, data.get(1).size());
		Assert.assertEquals("field;1", data.get(0).get(0));
	}
	
	/**
	 * Alternative separator, separator mixing.
	 */
	public void test10() throws IOException {
        CSVReader reader = new CSVReader(input("input10.csv"), ',');
        reader.setLog(log);
		List<List<String>> data = reader.readData();
		Assert.assertEquals(2, data.size());
		Assert.assertEquals(2, data.get(0).size());
		Assert.assertEquals(2, data.get(1).size());
		Assert.assertEquals("field;1", data.get(0).get(0));
		Assert.assertEquals("field;2", data.get(0).get(1));
		Assert.assertEquals("field,3", data.get(1).get(0));
	}

	/**
	 * Alternative separator, separator mixing.
	 */
	public void test11() throws IOException {
        CSVReader reader = new CSVReader(input("input11.csv"), ';');
        reader.setLog(log);
		List<List<String>> data = reader.readData();
		Assert.assertEquals(2, data.size());
		Assert.assertEquals(2, data.get(0).size());
		Assert.assertEquals(2, data.get(1).size());
		Assert.assertEquals("field,1", data.get(0).get(0));
		Assert.assertEquals("field,2", data.get(0).get(1));
		Assert.assertEquals("field;3", data.get(1).get(0));
	}
	
	/**
	 * Tab terminated.
	 */
	public void test12() throws IOException {
        CSVReader reader = new CSVReader(input("input12.csv"), '\t');
        reader.setLog(log);
		List<List<String>> data = reader.readData();
		Assert.assertEquals(2, data.size());
		Assert.assertEquals(2, data.get(0).size());
		Assert.assertEquals(2, data.get(1).size());
		Assert.assertEquals("field1", data.get(0).get(0));
		Assert.assertEquals("field4", data.get(1).get(1));
	}
	
	/**
	 * Tab separated.
	 */
	public void test13() throws IOException {
        CSVReader reader = new CSVReader(input("input13.csv"), '\t');
        reader.setLog(log);
		List<List<String>> data = reader.readData();
		Assert.assertEquals(2, data.size());
		Assert.assertEquals(2, data.get(0).size());
		Assert.assertEquals(2, data.get(1).size());
		Assert.assertEquals("field1", data.get(0).get(0));
		Assert.assertEquals("field4", data.get(1).get(1));
	}
}
