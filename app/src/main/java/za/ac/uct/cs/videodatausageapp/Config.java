package za.ac.uct.cs.videodatausageapp;

import java.util.ArrayList;
import java.util.List;

public interface Config {

    int SERVER_PORT=7800;
    String SERVER_HOST_ADDRESS = "jchavula-1.cs.uct.ac.za";
    String RAFFLE_CLAIM_EMAIL = "mf.giggs@gmail.com";
    String PREF_KEY_UNIQUE_ID = "PREF_KEY_UNIQUE_ID";
    String PREF_KEY_RAFFLE_STATUS = "PREF_KEY_RAFFLE_STATUS";
    String PREF_KEY_USER_INSTITUTION = "PREF_KEY_USER_INSTITUTION";
    String PREF_KEY_USER_CONSENT = "PREF_KEY_USER_INSTITUTION";

    String STOMP_SERVER_CONNECT_ENDPOINT = "/uctDataMon";
    String STOMP_SERVER_SUMMARY_REPORT_ENDPOINT = "/device/usage-summary";
    String STOMP_SERVER_RAFFLE_ENDPOINT = "/user/%s/raffle/notification";
    String STOMP_SERVER_CONTROL_ENDPOINT = "/queue/control";

    List<String> APPS_LIST = new ArrayList<String>(){{
        add("Zoom");
        add("Meet");
        add("Teams");
        add("WhatsApp");
        add("Vula App");
    }};
    long SPLASH_SCREEN_DURATION_MSEC = 3000;
}
