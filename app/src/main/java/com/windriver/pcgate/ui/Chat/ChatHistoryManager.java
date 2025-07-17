package com.windriver.pcgate.ui.Chat;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatHistoryManager
    {
    private static final String PREFS_NAME = "chat_history_prefs";
    private static final String KEY_CHATS = "chats";
    private static final String KEY_CURRENT_CHAT = "current_chat";
    private final SharedPreferences prefs;

    public ChatHistoryManager(Context context)
        {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }

    public void saveChat(List<ChatMessage> messages)
        {
        JSONArray chatsArray = getChatsArray();
        JSONArray chatMessages = new JSONArray();
        for (ChatMessage msg : messages)
        {
            JSONObject obj = new JSONObject();
            try
            {
                obj.put("text", msg.getText());
                obj.put("type", msg instanceof ChatMessage.UserMessage ? "user" : "ai");
            }
            catch (JSONException e)
            { /* ignore */ }
            chatMessages.put(obj);
        }
        JSONObject chatObj = new JSONObject();
        try
        {
            chatObj.put("timestamp", System.currentTimeMillis());
            chatObj.put("messages", chatMessages);
            chatObj.put("id", UUID.randomUUID().toString()); // Add unique ID
        }
        catch (JSONException e)
        { /* ignore */ }
        chatsArray.put(chatObj);
        prefs.edit().putString(KEY_CHATS, chatsArray.toString()).apply();
        }

    public void saveCurrentChat(List<ChatMessage> messages) {
        JSONArray chatMessages = new JSONArray();
        for (ChatMessage msg : messages) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("text", msg.getText());
                obj.put("type", msg instanceof ChatMessage.UserMessage ? "user" : "ai");
            } catch (JSONException e) { /* ignore */ }
            chatMessages.put(obj);
        }
        prefs.edit().putString(KEY_CURRENT_CHAT, chatMessages.toString()).apply();
    }

    public List<ChatMessage> loadCurrentChat() {
        List<ChatMessage> messages = new ArrayList<>();
        String json = prefs.getString(KEY_CURRENT_CHAT, null);
        if (json == null) return messages;
        try {
            JSONArray chatMessages = new JSONArray(json);
            for (int i = 0; i < chatMessages.length(); i++) {
                JSONObject obj = chatMessages.optJSONObject(i);
                if (obj != null) {
                    String text = obj.optString("text", "");
                    String type = obj.optString("type", "user");
                    if ("user".equals(type)) {
                        messages.add(new ChatMessage.UserMessage(text));
                    } else {
                        messages.add(new ChatMessage.AiMessage(text));
                    }
                }
            }
        } catch (JSONException e) { /* ignore */ }
        return messages;
    }

    public void clearCurrentChat() {
        prefs.edit().remove(KEY_CURRENT_CHAT).apply();
    }

    public List<ChatHistoryItem> getChatHistory()
        {
        List<ChatHistoryItem> history = new ArrayList<>();
        JSONArray chatsArray = getChatsArray();
        for (int i = 0; i < chatsArray.length(); i++)
        {
            JSONObject chatObj = chatsArray.optJSONObject(i);
            if (chatObj != null)
            {
                long timestamp = chatObj.optLong("timestamp", 0);
                String id = chatObj.optString("id", "");
                history.add(new ChatHistoryItem(i, timestamp, id));
            }
        }
        return history;
        }

    public List<ChatMessage> loadChat(int index)
        {
        List<ChatMessage> messages = new ArrayList<>();
        JSONArray chatsArray = getChatsArray();
        if (index < 0 || index >= chatsArray.length())
        {
            return messages;
        }
        JSONObject chatObj = chatsArray.optJSONObject(index);
        if (chatObj == null)
        {
            return messages;
        }
        JSONArray chatMessages = chatObj.optJSONArray("messages");
        if (chatMessages == null)
        {
            return messages;
        }
        for (int i = 0; i < chatMessages.length(); i++)
        {
            JSONObject obj = chatMessages.optJSONObject(i);
            if (obj != null)
            {
                String text = obj.optString("text", "");
                String type = obj.optString("type", "user");
                if ("user".equals(type))
                {
                    messages.add(new ChatMessage.UserMessage(text));
                }
                else
                {
                    messages.add(new ChatMessage.AiMessage(text));
                }
            }
        }
        return messages;
        }

    private JSONArray getChatsArray()
        {
        String json = prefs.getString(KEY_CHATS, "[]");
        try
        {
            return new JSONArray(json);
        }
        catch (JSONException e)
        {
            return new JSONArray();
        }
        }

    public static class ChatHistoryItem
        {
        public final int index;
        public final long timestamp;
        public final String id;

        public ChatHistoryItem(int index, long timestamp, String id)
            {
            this.index = index;
            this.timestamp = timestamp;
            this.id = id;
            }
        }
    }
