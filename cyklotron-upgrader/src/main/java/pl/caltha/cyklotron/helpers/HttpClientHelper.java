package pl.caltha.cyklotron.helpers;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class HttpClientHelper {

	static final Logger logger = Logger.getLogger(HttpClientHelper.class);

	static final int REQUEST_MAX_COUNT = 5;

	private RequestConfig requestConfig;

	private CredentialsProvider credsProvider;

	private BasicCookieStore cookieStore;

	public HttpClientHelper() {
		credsProvider = null;
		cookieStore = null;
		requestConfig = null;
	}

	/**
	 * Execute get resquest
	 * 
	 * @param url
	 * @return responce body
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String sentGetResquest(URI url) throws ClientProtocolException,
			IOException {

		CloseableHttpClient httpClient = getHttpClient();
		try {
			HttpGet get = new HttpGet(url);
			String response = null;
			int i = 0;
			while (response == null && i < REQUEST_MAX_COUNT) {
				response = httpClient.execute(get, responseHandler);
				i++;
			}
			httpClient.close();
			return response;
		} catch (Exception e) {
			httpClient.close();
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Execute post resquest
	 * 
	 * @param url
	 * @param qp
	 *            QueryString params map
	 * @return responce body
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String sentPostResquest(URI url, Map<String, String> qp)
			throws ClientProtocolException, IOException {

		CloseableHttpClient httpClient = getHttpClient();
		try {
			RequestBuilder rb = RequestBuilder.post().setUri(url);
			for (String key : qp.keySet()) {
				rb.addParameter(key, qp.get(key));
			}
			HttpUriRequest post = rb.build();
			String response = null;
			int i = 0;
			while (response == null && i < REQUEST_MAX_COUNT) {
				response = httpClient.execute(post, responseHandler);
				i++;
			}
			httpClient.close();
			return response;
		} catch (Exception e) {
			httpClient.close();
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	public void clearCookieStore() {
		if (cookieStore != null) {
			cookieStore.clear();
		}
	}

	/**
	 * @return HttpClient object
	 */
	private CloseableHttpClient getHttpClient() {

		HttpClientBuilder cb = HttpClients.custom();
		if (cookieStore != null) {
			cb.setDefaultCookieStore(cookieStore);
		}
		if (credsProvider != null) {
			cb.setDefaultCredentialsProvider(credsProvider);
		}
		if (requestConfig != null) {
			cb.setDefaultRequestConfig(requestConfig);
		}

		return cb.build();
	}

	/**
	 * response handler
	 */
	private ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
		public String handleResponse(final HttpResponse response)
				throws ClientProtocolException, IOException {

			int status = response.getStatusLine().getStatusCode();
			logger.debug("Repsponse status:" + status);
			if (status >= 200 && status < 300) {
				HttpEntity entity = response.getEntity();
				return entity != null ? EntityUtils.toString(entity) : "";
			} else {
				logger.warn("Repsponse error status:" + status
						+ ". Retry request.");
				return null;
			}
		}
	};

	public void setRequestConfig(RequestConfig requestConfig) {
		this.requestConfig = requestConfig;
	}

	public void setCredsProvider(CredentialsProvider credsProvider) {
		this.credsProvider = credsProvider;
	}

	public void setCookieStore(BasicCookieStore cookieStore) {
		this.cookieStore = cookieStore;
	}

	public RequestConfig getRequestConfig() {
		return requestConfig;
	}

	public CredentialsProvider getCredsProvider() {
		return credsProvider;
	}

	public BasicCookieStore getCookieStore() {
		return cookieStore;
	}
}
