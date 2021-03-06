/**
 * 
 */
package me.finiteloop.demo.bpm;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

/**
 * @author klimaye
 *
 */
@Path("/mail")
public class MailClientService {

	Properties properties = null;

	public MailClientService() throws IOException{
//		loadConfigurationData();
	}
	
	private void loadConfigurationData() throws IOException {
		properties.load(MailClientService.class.getClassLoader()
						.getResource("configuration.properties")
						.openStream());
		return;
	}

	private HttpClient getHttpClient(){
		CredentialsProvider credentialProvider = new BasicCredentialsProvider();
		UsernamePasswordCredentials usernamePassword = new UsernamePasswordCredentials(
				//TODO: remove the hardcoding
				"api",
				"key-896ca146389f7ca7551bf2fec697fe51"
//					this.properties.getProperty("mail.server.username"),
//					this.properties.getProperty("mail.server.passwd")
				);
		credentialProvider.setCredentials(AuthScope.ANY, usernamePassword);
		
		return HttpClientBuilder.create().setDefaultCredentialsProvider(credentialProvider).build();
	}
	
	@GET
	@Path("/send/{sendTo}/{emailSubject}/{emailBody}")
	public Response sendMail(@PathParam("sendTo") String sendTo,
			@PathParam("emailSubject") String emailSubject,
			@PathParam("emailBody") String emailBody) {

		ResponseBuilder builder = null;
		try {
			HttpClient client = getHttpClient();

			HttpPost httpPost = new HttpPost(
//TODO: remove the hardcoding					
//					properties.getProperty("mail.server.url")
					"https://api.mailgun.net/v3/sandboxd19e1d3ae87f4f89a24ef653c1f034aa.mailgun.org/messages"
					);
			List<NameValuePair> map = new ArrayList<NameValuePair>();
			map.add(new BasicNameValuePair("from",
					"Automated BPM User <mailgun@sandboxd19e1d3ae87f4f89a24ef653c1f034aa.mailgun.org>"));
			map.add(new BasicNameValuePair("to", sendTo));
			map.add(new BasicNameValuePair("subject", emailSubject));
			map.add(new BasicNameValuePair("text", emailBody));
			httpPost.setEntity(new UrlEncodedFormEntity(map));
			HttpResponse response = client.execute(httpPost);
			builder = Response.ok();
			builder.entity(response.getEntity().getContent());

		} catch (IOException ioException) {
			builder = Response.serverError();
			builder.entity(ioException.getMessage());
		}

		return builder.build();
	}

}
