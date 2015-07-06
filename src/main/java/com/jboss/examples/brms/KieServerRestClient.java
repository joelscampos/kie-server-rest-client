package com.jboss.examples.brms;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.ws.rs.core.MediaType;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;

public class KieServerRestClient {

	public static void main(String[] args) {

		System.out
				.println(runRequest(
						args[0],
						//"http://localhost:8080/kie-server/services/rest/server/containers/container1",
						new MediaType("application", "xml"),
						args[1],
						args[2]));
	}

	private static String runRequest(String url, MediaType mediaType, String username, String password) {
		String result = null;

		System.out.println("===============================================");
		System.out.println("URL: " + url);
		System.out.println("MediaType: " + mediaType.toString());

		try {

			DefaultHttpClient client = new DefaultHttpClient();
			client.getCredentialsProvider().setCredentials(
					new AuthScope(new HttpHost("localhost")),
					new UsernamePasswordCredentials(username, password));
			ApacheHttpClient4Executor executer = new ApacheHttpClient4Executor(
					client);

			// Using the RESTEasy libraries, initiate a client request
			// using the url as a parameter
			ClientRequest request = new ClientRequest(url, executer);

			// Be sure to set the mediatype of the request
			request.accept(mediaType);

			// Set body
			String xml = readFile("src/main/resources/input.xml");
			request.body(mediaType, xml);

			// Request has been made, now let's get the response
			ClientResponse<String> response = request.post(String.class);

			// Check the HTTP status of the request
			// HTTP 200 indicates the request is OK
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed request with HTTP status: "
						+ response.getStatus());
			}

			// We have a good response, let's now read it
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new ByteArrayInputStream(response.getEntity().getBytes())));

			// Loop over the br in order to print out the contents
			System.out.println("\n*** Response from Server ***\n");
			String output = null;
			while ((output = br.readLine()) != null) {
				System.out.println(output);
				result = output;
			}

		} catch (ClientProtocolException cpe) {
			System.err.println(cpe);
		} catch (IOException ioe) {
			System.err.println(ioe);
		} catch (Exception e) {
			System.err.println(e);
		}

		System.out.println("\n===============================================");

		return result;
	}

	private static String readFile(String path) throws IOException {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				sb.append(sCurrentLine);
			}

		}

		return sb.toString();
	}

}
