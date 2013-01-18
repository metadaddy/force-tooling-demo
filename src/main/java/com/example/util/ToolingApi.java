package com.example.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

import javax.servlet.ServletException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.ryanbrainard.richsobjects.RichSObjectsService;
import com.github.ryanbrainard.richsobjects.RichSObjectsServiceImpl;
import com.github.ryanbrainard.richsobjects.api.client.SfdcApiClientProvider;
import com.github.ryanbrainard.richsobjects.api.client.SfdcApiLoader;
import com.github.ryanbrainard.richsobjects.api.client.SfdcApiSessionProvider;

public class ToolingApi {
    private static final Logger logger = LoggerFactory.getLogger(ToolingApi.class);

    private static final ServiceLoader<SfdcApiSessionProvider> sessionLoader = loadProvider(SfdcApiSessionProvider.class);
    private static final SfdcApiSessionProvider sessionProvider = getFirstOrThrow(sessionLoader);
    
	private static final String TOOLING_API = "/services/data/v27.0/tooling/";

    public static JSONObject get(String path) throws IOException {
        String accessToken = sessionProvider.getAccessToken();
        String apiEndpoint = sessionProvider.getApiEndpoint();
        
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet get = new HttpGet(apiEndpoint + TOOLING_API + path);

        // set the token in the header
        get.setHeader("Authorization", "Bearer " + accessToken);

        logger.trace("ToolingApi.get path: "+path);

        HttpResponse response = httpclient.execute(get);
        
        logger.trace("ToolingApi.get status: "+response.getStatusLine().getStatusCode());
        
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            InputStream instream = entity.getContent();
            try {
	            	JSONObject json = (JSONObject)JSONValue.parse(new InputStreamReader(instream));
	
	            	logger.trace("ToolingApi.get response: " + json.toString());
	
	            	return json;
            } finally {
                instream.close();
            }
        }
		
		return new JSONObject();
    }
    

	public static void delete(String path) throws IOException {
        String accessToken = sessionProvider.getAccessToken();
        String apiEndpoint = sessionProvider.getApiEndpoint();
        
        HttpClient httpclient = new DefaultHttpClient();
        HttpDelete del = new HttpDelete(apiEndpoint + TOOLING_API + path);

        // set the token in the header
        del.setHeader("Authorization", "Bearer " + accessToken);
        
        logger.trace("ToolingApi.delete path: "+path);

        HttpResponse response = httpclient.execute(del);
        
        logger.trace("ToolingApi.delete status: "+response.getStatusLine().getStatusCode());
	}

	public static JSONObject post(String path, JSONObject jsonIn) throws IOException {
		return post(path, jsonIn.toString());
	}
    
    public static JSONObject post(String path, String jsonIn) throws IOException {
        String accessToken = sessionProvider.getAccessToken();
        String apiEndpoint = sessionProvider.getApiEndpoint();
        
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost post = new HttpPost(apiEndpoint + TOOLING_API + path);

        // set the token in the header
        post.setHeader("Authorization", "Bearer " + accessToken);
        
        logger.trace("ToolingApi.post path: "+path);
        logger.trace("ToolingApi.post body: "+jsonIn);
        
        // set the content
        StringEntity input = new StringEntity(jsonIn);
		input.setContentType("application/json");
        post.setEntity(input);

        HttpResponse response = httpclient.execute(post);
        
        logger.trace("ToolingApi.post status: "+response.getStatusLine().getStatusCode());
        
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            InputStream instream = entity.getContent();
            try {
            	JSONObject json = (JSONObject)JSONValue.parse(new InputStreamReader(instream));

            	logger.trace("ToolingApi.post response: " + json.toString());

            	return json;
            } finally {
                instream.close();
            }
        }
		
		return new JSONObject();
    }
 
    // TODO - put in util class
    private static <P> ServiceLoader<P> loadProvider(Class<P> providerClass) {
        final ServiceLoader<P> loader = ServiceLoader.load(providerClass, SfdcApiLoader.class.getClassLoader());

        logger.info("SfdcApiLoader loaded providers for service: " + providerClass.getName());
        int i = 1;
        for (P p : loader) {
            logger.info(providerClass.getName() + " #" + i++ + ": " + p.getClass());
        }

        return loader;
    }

    private static <S> S getFirstOrThrow(ServiceLoader<S> loader) {
        final Iterator<S> providerIterator = loader.iterator();
        if (!providerIterator.hasNext()) {
            throw new IllegalStateException(
                    "Could not load service from " + loader +
                    "\nEnsure an entry in META-INF/services has been loaded on the classpath.");
        }
        return providerIterator.next();
    }
}
