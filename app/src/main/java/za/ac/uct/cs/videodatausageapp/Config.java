package za.ac.uct.cs.videodatausageapp;

import java.util.ArrayList;
import java.util.List;

public interface Config {

    int SERVER_PORT=7800;
    String SERVER_HOST_ADDRESS = "jchavula-1.cs.uct.ac.za";
    String PREF_KEY_UNIQUE_ID = "PREF_KEY_UNIQUE_ID";
    String PREF_KEY_USER_INSTITUTION = "PREF_KEY_USER_INSTITUTION";
    String PREF_KEY_USER_CONSENT = "PREF_KEY_USER_INSTITUTION";

    String STOMP_SERVER_CONNECT_ENDPOINT = "/uctDataMon";
    String STOMP_SERVER_SUMMARY_REPORT_ENDPOINT = "/device/usage-summary";
    String STOMP_SERVER_CONTROL_ENDPOINT = "/queue/control";

    List<String> APPS_LIST = new ArrayList<String>(){{
        add("org.easyweb.browser");
        add("com.whatsapp");
        add("org.telegram.messenger");
        add("org.thoughtcrime.securesms");
        add("com.facebook.katana");
        add("com.facebook.lite");
        add("com.google.android.apps.meetings");
        add("com.microsoft.teams");
        add("us.zoom.videomeetings");
        add("us.zoom.pwa.twa");
        add("org.jitsi.meet");
        add("com.nweave.jitsiconference");
        add("com.vulapplication.vulapackage");
        add("com.android.chrome");
        add("org.mozilla.firefox");
        add("com.sec.android.app.sbrowser");
        add("com.google.android.youtube");
        add("com.google.android.gm");
        add("com.google.android.gm.lite");
        add("com.google.android.apps.searchlite");
        add("com.google.android.apps.docs.editors.docs");
        add("com.microsoft.office.lync15");
        add("com.skype.raider");
        add("com.linkedin.android");
        add("com.linkedin.android.lite");
        add("com.google.android.apps.tachyon");
        add("com.google.android.keep");
        add("com.twitter.android");
        add("com.twitter.android.lite");
        add("com.google.android.apps.docs");
        add("com.Slack");
        add("com.google.android.talk");
        add("com.google.android.calendar");
        add("com.ombiel.campusm.uct");
        add("com.blackboard.android.bbstudent");
        add("com.blackboard.android.bbinstructor");
        add("com.dropbox.android");
        add("com.microsoft.office.onenote");
        add("com.microsoft.skydrive");
        add("com.opera.browser");
        add("com.opera.mini.native");
        add("com.microsoft.office.outlook");
        add("com.udemy.android");
        add("com.udemydownload.udemy.udemydownloader");
        add("com.linkedin.android.learning");
        add("com.microsoft.office.officehubrow");
        add("com.microsoft.office.word");
        add("com.microsoft.office.excel");
        add("com.microsoft.office.powerpoint");
        add("com.paloaltonetworks.globalprotect");
        add("com.cisco.anyconnect.vpn.android.avf");
        add("com.saber.com.ikamvabeta");
        add("za.ac.uct.cs.videodatausageapp");
    }};
    long SPLASH_SCREEN_DURATION_MSEC = 3000;
}
