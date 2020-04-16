package eu.yourpass;

import org.dmfs.httpessentials.client.HttpRequest;
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


class YourpassApp {
    // static variable single_instance of type Singleton 
    private static YourpassApp instance = null;

    // variable of type String 
    private String apiUrl;
    private String clientId;
    private String clientSecret;
    private String password;
    private String username;

    private HttpRequestExecutor executor;
    private OAuth2AuthorizationProvider provider;
    private OAuth2ClientCredentials credentials;
    private OAuth2Client client;
    private OAuth2AccessToken token;

    // private constructor restricted to this class itself
    private YourpassApp(String apiUrl, String clientId, String clientSecret, String username, String password) {
        System.out.println("create YourpassApp instance with api_url: " + apiUrl + " client_id: " + clientId + " client_secret: " + clientSecret + " username: " + username);
        this.apiUrl = apiUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.password = password;
        this.username = username;

        executor = new HttpUrlConnectionExecutor();

// Create OAuth2 provider
        provider = new BasicOAuth2AuthorizationProvider(
                URI.create(this.apiUrl + "/oauth2/auth"),
                URI.create(this.apiUrl + "/oauth2/token"),
                new Duration(1, 0, 3600));


        credentials = new BasicOAuth2ClientCredentials(
                this.clientId, this.clientSecret);

        client = new BasicOAuth2Client(
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


    public void getPasses() throws ProtocolException, ProtocolError, IOException, InterruptedException {
        String url = this.apiUrl + "/v1/pass";
        // 'request' is a HttpRequest instance that's to be authenticated
        // result = executor.execute(url, new BearerAuthenticatedRequest(request, token));
        /* String authorization = String.format("Bearer %s", getToken().accessToken());
        java.net.http.HttpRequest r = java.net.http.HttpRequest.newBuilder().GET().header("Authorization", authorization).build();

        HttpClient client = HttpClient.newHttpClient();
        */

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
  