package com.trutek.looped.utils.listeners;

public interface GlobalLoginListener {

    void onCompleteQbLogin();

    void onCompleteQbChatLogin();

    void onCompleteWithError(String error);
}