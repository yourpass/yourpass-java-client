package eu.yourpass;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.yourpass.model.Pass;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
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
import org.dmfs.oauth2.client.grants.TokenRefreshGrant;
import org.dmfs.oauth2.client.scope.BasicScope;
import org.dmfs.rfc3986.encoding.Precoded;
import org.dmfs.rfc3986.uris.LazyUri;
import org.dmfs.rfc5545.DateTime;
import org.dmfs.rfc5545.Duration;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * YourpassApp is using "oauth2-essentials" as OAuth2 client implementation
 * (<a href="https://github.com/dmfs/oauth2-essentials">https://github.com/dmfs/oauth2-essentials</a>).
 */
public class YourpassApp {

    private static final Logger logger = LoggerFactory.getLogger(YourpassApp.class);

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
        logger.info("Creating YourpassApp instance with api_url: " + apiUrl + " client_id: " + clientId + ", username: " + username);
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
                credentials,
                new LazyUri(new Precoded("http://localhost")) /* Redirect URL */);
    }

    public OAuth2AccessToken getToken() throws ProtocolException, ProtocolError, IOException {
        logger.trace("Getting token");

        // check if token is not null or is not expired
        if (this.token == null) {
            logger.trace("Request access token using a Resource Owner Password Grant [NEW]");
            this.token = new ResourceOwnerPasswordGrant(
                    client, new BasicScope("scope"), this.username, this.password).accessToken(executor);
        } else if (this.token.expirationDate().before(DateTime.now())) {
            if (this.token.hasRefreshToken()) {
                logger.trace("Request new access token, providing the previous one");
                this.token = new TokenRefreshGrant(client, this.token).accessToken(executor);
            } else {
                logger.trace("Request access token using a Resource Owner Password Grant [RENEW]");
                this.token = new ResourceOwnerPasswordGrant(
                        client, new BasicScope("scope"), this.username, this.password).accessToken(executor);
            }
        }
        return token;
    }

    public Pass createPass(String templateId, Map<String, Object> data) throws IOException, ProtocolException, ProtocolError {
        logger.trace("Creating pass [templateId={}, data={}]", templateId, data);

        // pass create request
        Pass pass = new Pass();
        pass.setDynamicData(data);
        pass.setTemplateId(templateId);

        // yourpass api
        ObjectMapper mapper = new ObjectMapper();
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(this.apiUrl + "/v1/pass");
        StringEntity entity = new StringEntity(mapper.writeValueAsString(pass), ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);
        prepareHeaders(httpPost);
        CloseableHttpResponse response = client.execute(httpPost);
        String jsonResponse = EntityUtils.toString(response.getEntity());
        client.close();

        // reads value from json response and returns as Pass object
        Pass map = mapper.readValue(jsonResponse, Pass.class);
        if (logger.isTraceEnabled()) {
            logger.trace("{}", map);
        }
        return map;
    }

    public Pass readPass(String passId) throws IOException, ProtocolError, ProtocolException {
        logger.trace("Reading pass [passId={}]", passId);

        // yourpass api
        ObjectMapper mapper = new ObjectMapper();
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(this.apiUrl + "/v1/pass/" + passId);
        prepareHeaders(httpGet);
        CloseableHttpResponse response = client.execute(httpGet);
        String jsonResponse = EntityUtils.toString(response.getEntity());
        client.close();

        // reads value from json response and returns as Pass object
        Pass map = mapper.readValue(jsonResponse, Pass.class);
        if (logger.isTraceEnabled()) {
            logger.trace("{}", map);
        }
        return map;
    }

    public Pass updatePass(String passId, String templateId, Map<String, Object> data) throws IOException, ProtocolError, ProtocolException {
        logger.trace("Updating pass [passId={}, templateId={}, data={}]", passId, templateId, data);

        // pass update request
        Pass pass = new Pass();
        pass.setDynamicData(data);
        pass.setTemplateId(templateId);

        // yourpass api
        ObjectMapper mapper = new ObjectMapper();
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPut httpPut = new HttpPut(this.apiUrl + "/v1/pass/" + passId);
        StringEntity entity = new StringEntity(mapper.writeValueAsString(pass), ContentType.APPLICATION_JSON);
        prepareHeaders(httpPut);
        CloseableHttpResponse response = client.execute(httpPut);
        String jsonResponse = EntityUtils.toString(response.getEntity());
        client.close();

        // reads value from json response and returns as Pass object
        Pass map = mapper.readValue(jsonResponse, Pass.class);
        if (logger.isTraceEnabled()) {
            logger.trace("{}", map);
        }
        return map;
    }

    private void prepareHeaders(HttpRequestBase r) throws ProtocolException, ProtocolError, IOException {
        r.setHeader("Accept", "application/json");
        r.setHeader("Content-type", "application/json");
        String authorization = String.format("Bearer %s", this.getToken().accessToken());
        r.setHeader("Authorization", authorization);
    }


    // static method to create instance of Singleton class
    public static YourpassApp getInstance() {
        if (instance == null) {
            throw new RuntimeException("YourPass client was not configured");
        }
        return instance;
    }


    public static YourpassApp configure(String apiUrl, String clientId, String clientSecret, String username, String password) {
        if (instance == null) {
            instance = new YourpassApp(apiUrl, clientId, clientSecret, username, password);
        }
        return instance;
    }
} 
  