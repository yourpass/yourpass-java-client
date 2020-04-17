package eu.yourpass;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.yourpass.model.Pass;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dmfs.httpessentials.client.HttpRequestExecutor;
import org.dmfs.httpessentials.exceptions.ProtocolError;
import org.dmfs.httpessentials.exceptions.ProtocolException;
import org.dmfs.httpessentials.httpurlconnection.HttpUrlConnectionExecutor;
import org.dmfs.oauth2.client.*;
import org.dmfs.oauth2.client.grants.ResourceOwnerPasswordGrant;
import org.dmfs.oauth2.client.scope.BasicScope;
import org.dmfs.rfc5545.DateTime;
import org.dmfs.rfc5545.Duration;

import java.io.IOException;
import java.net.URI;
import java.util.Map;


public class YourpassApp {

    // static variable single instance of type YourpassApp
    private static YourpassApp instance = null;

    // configuration
    private String password;
    private String username;
    private String apiUrl;

    // oauth variables
    private HttpRequestExecutor executor;
    private OAuth2Client client;
    private OAuth2AccessToken token;

    // private constructor restricted to this class itself
    private YourpassApp(String apiUrl, String clientId, String clientSecret, String username, String password) {
        System.out.println("create YourpassApp instance with api_url: " + apiUrl + " client_id: " + clientId + " client_secret: " + clientSecret + " username: " + username);
        this.apiUrl = apiUrl;
        this.password = password;
        this.username = username;

        this.executor = new HttpUrlConnectionExecutor();
        BasicOAuth2AuthorizationProvider provider = new BasicOAuth2AuthorizationProvider(
                URI.create(this.apiUrl + "/oauth2/auth"),
                URI.create(this.apiUrl + "/oauth2/token"),
                new Duration(1, 0, 3600));

        BasicOAuth2ClientCredentials credentials = new BasicOAuth2ClientCredentials(
                clientId, clientSecret);

        this.client = new BasicOAuth2Client(
                provider,
                credentials, URI.create("http://localhost"));
    }

    public OAuth2AccessToken getToken() throws ProtocolException, ProtocolError, IOException {
        // check if token is not null or is not expired;
        if (this.token == null || this.token.expirationDate().after(DateTime.now())) {
            this.token = new ResourceOwnerPasswordGrant(
                    client, new BasicScope("scope"), this.username, this.password).accessToken(executor);
        }

        return token;

    }


    public Pass createPass(String templateId, Map<String, Object> data) throws IOException, ProtocolException, ProtocolError {
        Pass pass = new Pass();
        pass.setDynamicData(data);
        pass.setTemplateId(templateId);
        ObjectMapper mapper = new ObjectMapper();
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(this.apiUrl + "/v1/pass");
        StringEntity entity = new StringEntity(mapper.writeValueAsString(pass));
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        String authorization = String.format("Bearer %s", this.getToken().accessToken());
        httpPost.setHeader("Authorization", authorization);
        CloseableHttpResponse response = client.execute(httpPost);
        String jsonResponse = EntityUtils.toString(response.getEntity());
        Pass map = mapper.readValue(jsonResponse, Pass.class);
        client.close();
        return map;
    }

    public Pass readPass(String passId) throws IOException, ProtocolError, ProtocolException {
        ObjectMapper mapper = new ObjectMapper();
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpPost = new HttpGet(this.apiUrl + "/v1/pass/" + passId);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        String authorization = String.format("Bearer %s", this.getToken().accessToken());
        httpPost.setHeader("Authorization", authorization);
        CloseableHttpResponse response = client.execute(httpPost);
        String jsonResponse = EntityUtils.toString(response.getEntity());
        Pass map = mapper.readValue(jsonResponse, Pass.class);
        client.close();
        return map;
    }

    public Pass updatePass(String passId, String templateId, Map<String, Object> data) throws IOException, ProtocolError, ProtocolException {
        ObjectMapper mapper = new ObjectMapper();
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPut httpPost = new HttpPut(this.apiUrl + "/v1/pass/" + passId);
        Pass pass = new Pass();
        pass.setDynamicData(data);
        pass.setTemplateId(templateId);
        StringEntity entity = new StringEntity(mapper.writeValueAsString(pass));
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        String authorization = String.format("Bearer %s", this.getToken().accessToken());
        httpPost.setHeader("Authorization", authorization);
        CloseableHttpResponse response = client.execute(httpPost);
        String jsonResponse = EntityUtils.toString(response.getEntity());
        Pass map = mapper.readValue(jsonResponse, Pass.class);
        client.close();
        return map;
    }


    // static method to create instance of Singleton class
    public static YourpassApp getInstance() {
        if (instance == null)
            throw new RuntimeException("YourPass client was not configured");
        return instance;
    }


    public static YourpassApp configure(String apiUrl, String clientId, String clientSecret, String username, String password) {
        if (instance == null)
            instance = new YourpassApp(apiUrl, clientId, clientSecret, username, password);

        return instance;
    }
} 
  