package za.ac.uct.cs.videodatausageapp;

import ua.naiksoftware.stomp.dto.StompMessage;

public interface SubscriptionCallbackInterface {
    void onSubscriptionResult(StompMessage result);
}
