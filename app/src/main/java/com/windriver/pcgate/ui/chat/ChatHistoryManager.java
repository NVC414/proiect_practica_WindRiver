package com.windriver.pcgate.ui.chat;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatHistoryManager
    {
    private static final String PREFS_NAME = "chat_history_prefs";
    private static final String KEY_CHATS = "chats";
    private static final String KEY_CURRENT_CHAT = "current_chat";
    private static final String KEY_LAST_CHAT_ID = "last_chat_id";
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
            catch (JSONException e) {
                throw new RuntimeException(e);
            }
            chatMessages.put(obj);
        }
        String conversationHash = generateConversationHash(chatMessages);
        String lastChatId = prefs.getString(KEY_LAST_CHAT_ID, null);
        boolean updated = false;
        for (int i = 0; i < chatsArray.length(); i++) {
            JSONObject chatObj = chatsArray.optJSONObject(i);
            if (chatObj != null && conversationHash.equals(chatObj.optString("hash"))) {
                try {
                    chatObj.put("timestamp", System.currentTimeMillis());
                    String chatId = lastChatId != null ? lastChatId : java.util.UUID.randomUUID().toString();
                    chatObj.put("id", chatId);
                    prefs.edit().putString(KEY_LAST_CHAT_ID, chatId).apply();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                try {
                    chatsArray.put(i, chatObj);
                } catch (Exception e) {
                    try {
                        chatsArray.remove(i);
                        chatsArray.put(chatObj);
                    } catch (Exception ignored) {}
                }
                prefs.edit().putString(KEY_CHATS, chatsArray.toString()).apply();
                updated = true;
                break;
            }
        }
        if (!updated) {
            JSONObject chatObj = new JSONObject();
            try {
                chatObj.put("timestamp", System.currentTimeMillis());
                chatObj.put("messages", chatMessages);
                String chatId = java.util.UUID.randomUUID().toString();
                chatObj.put("id", chatId);
                chatObj.put("hash", conversationHash);
                prefs.edit().putString(KEY_LAST_CHAT_ID, chatId).apply();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            chatsArray.put(chatObj);
            prefs.edit().putString(KEY_CHATS, chatsArray.toString()).apply();
        }
        }
    private String generateConversationHash(JSONArray chatMessages) {
        return String.valueOf(chatMessages.toString().hashCode());
    }

    public void saveCurrentChat(List<ChatMessage> messages) {
        JSONArray chatMessages = new JSONArray();
        for (ChatMessage msg : messages) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("text", msg.getText());
                obj.put("type", msg instanceof ChatMessage.UserMessage ? "user" : "ai");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
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
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
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

        public record ChatHistoryItem(int index, long timestamp, String id) {
        }

    public void saveChat(List<ChatMessage> messages, String chatIdToUpdate) {
        JSONArray chatsArray = getChatsArray();
        JSONArray chatMessages = new JSONArray();
        for (ChatMessage msg : messages) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("text", msg.getText());
                obj.put("type", msg instanceof ChatMessage.UserMessage ? "user" : "ai");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            chatMessages.put(obj);
        }
        String conversationHash = generateConversationHash(chatMessages);
        boolean updated = false;
        if (chatIdToUpdate != null) {
            // Try to update by chatId
            for (int i = 0; i < chatsArray.length(); i++) {
                JSONObject chatObj = chatsArray.optJSONObject(i);
                if (chatObj != null && chatIdToUpdate.equals(chatObj.optString("id"))) {
                    try {
                        chatObj.put("timestamp", System.currentTimeMillis());
                        chatObj.put("messages", chatMessages);
                        chatObj.put("hash", conversationHash);
                        chatObj.put("id", chatIdToUpdate);
                        prefs.edit().putString(KEY_LAST_CHAT_ID, chatIdToUpdate).apply();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        chatsArray.put(i, chatObj);
                    } catch (Exception e) {
                        try {
                            chatsArray.remove(i);
                            chatsArray.put(chatObj);
                        } catch (Exception ignored) {}
                    }
                    prefs.edit().putString(KEY_CHATS, chatsArray.toString()).apply();
                    updated = true;
                    break;
                }
            }
        }
        if (!updated) {
            saveChat(messages);
        }
    }}
