package com.windriver.pcgate.ui.Chat;

public abstract class ChatMessage
    {
    private final String text;

    public ChatMessage(String text)
        {
        this.text = text;
        }

    public String getText()
        {
        return text;
        }

    public static class UserMessage extends ChatMessage
        {
        public UserMessage(String text)
            {
            super(text);
            }
        }

    public static class AiMessage extends ChatMessage
        {
        public AiMessage(String text)
            {
            super(text);
            }
        }
    }

