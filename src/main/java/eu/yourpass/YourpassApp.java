package eu.yourpass;

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


class YourpassApp {

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
            System.out.println(token.accessToken());
            System.out.println(token.expirationDate());
        }

        return token;

    }


    public Map<String, Object> createPass(String templateId, Map<String, Object> data) {
        System.out.println(this.apiUrl);
        /* TODO - call post
            url: this.apiUrl + /v1/pass/
            body:
            {
                templateId:"{template-id}",
                data: {
                    property: "foo",
                    property2: "bar"
                }
            }
        */
        return null;
    }


    public Map<String, Object> updatePass(String passId, String templateId, Map<String, Object> data) {
        /* TODO - call put to:
            url: this.apiUrl + /v1/pass/{passId}
            body:
            {
                templateId:"{template-id}",
                data: {
                    property: "foo",
                    property2: "bar"
                }
            }
        */
        return null;
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
  