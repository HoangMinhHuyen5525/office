package com.huyenhm.data;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.BasicCredentialsProvider;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CallApi {

	public static List<String> callApi(String ip, String port, String apiEndpoint, String method, String username,
			String password, String payload) throws Exception {
		List<String> responseBody = new ArrayList<>();
		String apiUrl = "";

		if (port.equals("80")) {
			apiUrl += "http://" + ip + apiEndpoint;
		} else if (port.equals("443")) {
			apiUrl += "https://" + ip + apiEndpoint;
		} else {
			apiUrl += "http://" + ip + ":" + port + apiEndpoint;
		}

		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope(ip, AuthScope.ANY_PORT),
				new UsernamePasswordCredentials(username, password));
		try (CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider)
				.build()) {
			HttpUriRequest httpRequest = null;

			if (method.equals("GET")) {
				httpRequest = new HttpGet(apiUrl);
			} else if (method.equals("POST")) {
				HttpPost httpPost = new HttpPost(apiUrl);
				StringEntity entity = new StringEntity(payload);
				entity.setContentType("application/*");
				httpPost.setEntity(entity);
				httpRequest = httpPost;
			}  else if (method.equals("PUT")) {
				HttpPut httpPost = new HttpPut(apiUrl);
				StringEntity entity = new StringEntity(payload);
				entity.setContentType("application/*");
				httpPost.setEntity(entity);
				httpRequest = httpPost;
			}
			HttpResponse response = httpClient.execute(httpRequest);
			int responseCode = response.getStatusLine().getStatusCode();
			StringBuilder code = new StringBuilder();
			StringBuilder responseContent = new StringBuilder();
			if (responseCode == 200) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				String line;
				while ((line = reader.readLine()) != null) {
					responseContent.append(line);
				}
				responseBody.add(code.append(responseCode).toString());
				responseBody.add(responseContent.toString());
			} else {
				responseBody.add(code.append(responseCode).toString());
				responseBody.add("API call failed:" + responseContent.toString());
			}
		}
		return responseBody;
	}
}
