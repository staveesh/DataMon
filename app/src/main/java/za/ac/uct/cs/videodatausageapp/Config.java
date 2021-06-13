package za.ac.uct.cs.videodatausageapp;

import java.util.ArrayList;
import java.util.List;

public interface Config {

    int SERVER_PORT=8080;
    String SERVER_HOST_ADDRESS = "159.65.35.26";

    String PREF_KEY_UNIQUE_ID = "PREF_KEY_UNIQUE_ID";

    String STOMP_SERVER_CONNECT_ENDPOINT = "/uctHci";
    String STOMP_SERVER_SUMMARY_REPORT_ENDPOINT = "/device/usage-summary";
    String STOMP_SERVER_CONTROL_ENDPOINT = "/queue/control";

    List<String> VIDEO_CALL_APP_PACKAGES = new ArrayList<String>(){{
        add("Zoom");
        add("Meet");
        add("Teams");
        add("WhatsApp");
    }};
}
