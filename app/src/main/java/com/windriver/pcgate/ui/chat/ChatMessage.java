package com.windriver.pcgate.ui.chat;

import lombok.Getter;

@Getter
public abstract class ChatMessage
    {
    private final String text;

    public ChatMessage(String text)
        {
        this.text = text;
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

