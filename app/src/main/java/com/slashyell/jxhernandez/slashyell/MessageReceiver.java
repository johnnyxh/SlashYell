package com.slashyell.jxhernandez.slashyell;

import android.app.Activity;

import com.example.johnny.myapplication.backend.yellMessageApi.model.YellMessage;

import java.util.List;

/**
 * Created by johnny on 4/28/15.
 */
public interface MessageReceiver {
    public void onMessagesReceived(List<YellMessage> messages);
    public void onRepliesReceived(List<YellMessage> replies);
}
