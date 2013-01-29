package com.example.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.ServiceLoader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ryanbrainard.richsobjects.api.client.SfdcApiSessionProvider;

public class ToolingApi {
	private static final Logger logger = LoggerFactory
			.getLogger(ToolingApi.class);

	private static final ServiceLoader<SfdcApiSessionProvider> sessionLoader = loadProvider(SfdcApiSessionProvider.class);
	private static final SfdcApiSessionProvider sessionProvider = getFirstOrThrow(sessionLoader);

	private static final String TOOLING_API = "/services/data/v27.0/tooling/";
	private static final String FILE_DOWNLOAD = "/servlet/servlet.FileDownload?file=";

	public static JSONObject get(String path) throws IOException {
		String accessToken = sessionProvider.getAccessToken();
		String apiEndpoint = sessionProvider.getApiEndpoint();

		HttpClient httpclient = new DefaultHttpClient();
		HttpGet get = new HttpGet(apiEndpoint + TOOLING_API + path);

		// set the token in the header
		get.setHeader("Authorization", "Bearer " + accessToken);

		logger.trace("ToolingApi.get path: " + path);

		HttpResponse response = httpclient.execute(get);

		logger.trace("ToolingApi.get status: "
				+ response.getStatusLine().getStatusCode());

		HttpEntity entity = response.getEntity();
		if (entity != null) {
			InputStream instream = entity.getContent();
			try {
				JSONObject json = (JSONObject) JSONValue
						.parse(new InputStreamReader(instream));

				logger.trace("ToolingApi.get response: " + json.toString());

				return json;
			} finally {
				instream.close();
			}
		}

		return new JSONObject();
	}
	
	// Get the content for an ApexLog from the id
	// NOTE - the file download servlet is not a documented or
	// supported interface. Use at your own risk. You may have
	// to modify your code to adapt to a future release of the
	// Tooling API!
	public static String getFile(String id) throws IOException {
		String accessToken = sessionProvider.getAccessToken();
		String apiEndpoint = sessionProvider.getApiEndpoint();

		HttpClient httpclient = new DefaultHttpClient();
		HttpGet get = new HttpGet(apiEndpoint + FILE_DOWNLOAD + id);

		// set the cookie
		HttpContext context = new BasicHttpContext();
		CookieStore cookieStore = new BasicCookieStore(); 
		BasicClientCookie cookie = new BasicClientCookie("sid", accessToken);

		try {
			cookie.setDomain((new URI(apiEndpoint)).getHost());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
		cookie.setPath("/");

		cookieStore.addCookie(cookie); 
		context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

		logger.trace("ToolingApi.getFile id: " + id);

		HttpResponse response = httpclient.execute(get, context);

		logger.trace("ToolingApi.get status: "
				+ response.getStatusLine().getStatusCode());

		HttpEntity entity = response.getEntity();
		
		return (entity != null) ? EntityUtils.toString(entity) : null;
	}	

	public static void delete(String path) throws IOException {
		String accessToken = sessionProvider.getAccessToken();
		String apiEndpoint = sessionProvider.getApiEndpoint();

		HttpClient httpclient = new DefaultHttpClient();
		HttpDelete del = new HttpDelete(apiEndpoint + TOOLING_API + path);

		// set the token in the header
		del.setHeader("Authorization", "Bearer " + accessToken);

		logger.trace("ToolingApi.delete path: " + path);

		HttpResponse response = httpclient.execute(del);

		logger.trace("ToolingApi.delete status: "
				+ response.getStatusLine().getStatusCode());
	}

	public static JSONObject post(String path, JSONObject jsonIn)
			throws IOException {
		return post(path, jsonIn.toString());
	}

	public static JSONObject post(String path, String jsonIn)
			throws IOException {
		String accessToken = sessionProvider.getAccessToken();
		String apiEndpoint = sessionProvider.getApiEndpoint();

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost post = new HttpPost(apiEndpoint + TOOLING_API + path);

		// set the token in the header
		post.setHeader("Authorization", "Bearer " + accessToken);

		logger.trace("ToolingApi.post path: " + path);
		logger.trace("ToolingApi.post body: " + jsonIn);

		// set the content
		StringEntity input = new StringEntity(jsonIn);
		input.setContentType("application/json");
		post.setEntity(input);

		HttpResponse response = httpclient.execute(post);

		logger.trace("ToolingApi.post status: "
				+ response.getStatusLine().getStatusCode());

		HttpEntity entity = response.getEntity();
		if (entity != null) {
			InputStream instream = entity.getContent();
			try {
				JSONObject json = (JSONObject) JSONValue
						.parse(new InputStreamReader(instream));

				logger.trace("ToolingApi.post response: " + json.toString());

				return json;
			} finally {
				instream.close();
			}
		}

		return new JSONObject();
	}

	// Copied from SfdcApiLoader - should really be factored out
	private static <P> ServiceLoader<P> loadProvider(Class<P> providerClass) {
		final ServiceLoader<P> loader = ServiceLoader.load(providerClass,
				ToolingApi.class.getClassLoader());

		logger.info("ToolingApi loaded providers for service: "
				+ providerClass.getName());
		int i = 1;
		for (P p : loader) {
			logger.info(providerClass.getName() + " #" + i++ + ": "
					+ p.getClass());
		}

		return loader;
	}

	private static <S> S getFirstOrThrow(ServiceLoader<S> loader) {
		final Iterator<S> providerIterator = loader.iterator();
		if (!providerIterator.hasNext()) {
			throw new IllegalStateException(
					"Could not load service from "
							+ loader
							+ "\nEnsure an entry in META-INF/services has been loaded on the classpath.");
		}
		return providerIterator.next();
	}
}
