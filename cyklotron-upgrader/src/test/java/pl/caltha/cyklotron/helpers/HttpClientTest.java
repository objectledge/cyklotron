package pl.caltha.cyklotron.helpers;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class HttpClientTest {

	@Test
	public void sentGetResquest() throws IOException, URISyntaxException {
		HttpClientHelper httpClient = new HttpClientHelper();
		String responseBody = httpClient.sentGetResquest(new URI(
				"http://httpbin.org/ip"));
		assertTrue(responseBody != null);
	}

	@Test
	public void sentPostResquest() throws IOException, URISyntaxException {
		HttpClientHelper httpClient = new HttpClientHelper();
		Map<String, String> post = new HashMap<String, String>();
		post.put("param", "345345^@#$@#$@#%$#%$^&%$^985");
		String responseBody = httpClient.sentPostResquest(new URI(
				"http://httpbin.org/post"), post);
		assertTrue(responseBody != null);
		assertTrue(responseBody.contains("345345^@#$@#$@#%$#%$^&%$^985"));
	}
}
