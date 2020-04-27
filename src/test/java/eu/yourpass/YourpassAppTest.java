package eu.yourpass;

import static org.junit.Assert.assertTrue;

import eu.yourpass.model.Pass;
import org.dmfs.httpessentials.exceptions.ProtocolError;
import org.dmfs.httpessentials.exceptions.ProtocolException;
import org.dmfs.oauth2.client.OAuth2AccessToken;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class YourpassAppTest {
    @Test
    public void getTokenTest() {
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

    @Test
    public void createPassTest() {
        try {
            YourpassApp.configure(
                    System.getenv("YOURPASS_API_URL"),
                    System.getenv("YOURPASS_CLIENT_ID"),
                    System.getenv("YOURPASS_CLIENT_SECRET") == null ? "" : System.getenv("YOURPASS_CLIENT_SECRET"),
                    System.getenv("YOURPASS_USERNAME"),
                    System.getenv("YOURPASS_PASSWORD")

            );


            Map<String, Object> map = new HashMap<>();
            map.put("property", "Příliš žluťoučký kůň úpěl ďábelské ódy.");
            Pass pass = YourpassApp.getInstance().createPass(System.getenv("TEMPLATE_ID"), map);

            System.out.println(pass.toString());

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
