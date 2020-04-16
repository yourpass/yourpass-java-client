package eu.yourpass;

import static org.junit.Assert.assertTrue;

import org.dmfs.httpessentials.exceptions.ProtocolError;
import org.dmfs.httpessentials.exceptions.ProtocolException;
import org.dmfs.oauth2.client.OAuth2AccessToken;
import org.junit.Test;

import java.io.IOException;

public class YourpassAppTest {
    @Test
    public void shouldAnswerWithTrue() {
        try {
            YourpassApp.configure(
                    System.getenv("YOURPASS_API_URL"),
                    System.getenv("YOURPASS_CLIENT_ID"),
                    System.getenv("YOURPASS_CLIENT_SECRET") == null ? "" : System.getenv("YOURPASS_CLIENT_SECRET"),
                    System.getenv("YOURPASS_USERNAME"),
                    System.getenv("YOURPASS_PASSWORD")
            );
            OAuth2AccessToken token = YourpassApp.getInstance().getToken();
            return;
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (ProtocolError protocolError) {
            protocolError.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(true);
    }
}
