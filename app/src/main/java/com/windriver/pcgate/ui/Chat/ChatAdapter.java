package com.windriver.pcgate.ui.Chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.windriver.pcgate.R;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
    private final List<ChatMessage> messages = new ArrayList<>();

    public void addMessage(ChatMessage message)
        {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
        }

    public void updateLastAiMessage(String text)
        {
        for (int i = messages.size() - 1; i >= 0; i--)
        {
            ChatMessage msg = messages.get(i);
            if (msg instanceof ChatMessage.AiMessage)
            {
                messages.set(i, new ChatMessage.AiMessage(text));
                notifyItemChanged(i, text);
                break;
            }
        }
        }

    public void clearMessages()
        {
        messages.clear();
        notifyDataSetChanged();
        }

    @Override
    public int getItemViewType(int position)
        {
        ChatMessage msg = messages.get(position);
        if (msg instanceof ChatMessage.UserMessage)
        {
            return 1;
        }
        else
        {
            return 2;
        }
        }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
        if (viewType == 1)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_user,
                    parent, false);
            return new UserViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_ai,
                    parent, false);
            return new AiViewHolder(view);
        }
        }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
        {
        ChatMessage message = messages.get(position);
        boolean isLastOfType = false;
        if (message instanceof ChatMessage.UserMessage)
        {

            for (int i = messages.size() - 1; i >= 0; i--)
            {
                if (messages.get(i) instanceof ChatMessage.UserMessage)
                {
                    isLastOfType = (i == position);
                    break;
                }
            }
            ((UserViewHolder) holder).bind(message.getText(), isLastOfType);
        }
        else if (message instanceof ChatMessage.AiMessage)
        {

            for (int i = messages.size() - 1; i >= 0; i--)
            {
                if (messages.get(i) instanceof ChatMessage.AiMessage)
                {
                    isLastOfType = (i == position);
                    break;
                }
            }
            ((AiViewHolder) holder).bind(message.getText(), isLastOfType);
        }
        }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position,
                                 @NonNull List<Object> payloads)
        {
        if (!payloads.isEmpty() && holder instanceof AiViewHolder)
        {

            String newText = (String) payloads.get(0);
            ((AiViewHolder) holder).animateTextUpdate(newText);
        }
        else
        {
            onBindViewHolder(holder, position);
        }
        }

    @Override
    public int getItemCount()
        {
        return messages.size();
        }

    static class UserViewHolder extends RecyclerView.ViewHolder
        {
        private final TextView textView;
        private final android.widget.ImageView iconView;

        UserViewHolder(View itemView)
            {
            super(itemView);
            textView = itemView.findViewById(R.id.userMessageText);
            iconView = itemView.findViewById(R.id.userIcon);
            }

        void bind(String text, boolean showIcon)
            {
            textView.setText(text);
            iconView.setVisibility(showIcon ? View.VISIBLE : View.GONE);
            }
        }

    static class AiViewHolder extends RecyclerView.ViewHolder
        {
        private final TextView textView;
        private final android.widget.ImageView iconView;

        AiViewHolder(View itemView)
            {
            super(itemView);
            textView = itemView.findViewById(R.id.aiMessageText);
            iconView = itemView.findViewById(R.id.aiIcon);
            }

        void bind(String text, boolean showIcon)
            {
            textView.setText(text);
            iconView.setVisibility(showIcon ? View.VISIBLE : View.GONE);
            }

        void animateTextUpdate(String text)
            {

            int oldHeight = textView.getHeight();
            textView.setText(text);
            textView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int newHeight = textView.getMeasuredHeight();
            if (oldHeight != newHeight)
            {
                android.animation.ValueAnimator animator = android.animation.ValueAnimator.ofInt(
                        oldHeight, newHeight);
                animator.setDuration(80);
                animator.addUpdateListener(animation ->
                    {
                        textView.getLayoutParams().height = (int) (Integer) animation.getAnimatedValue();
                        textView.requestLayout();
                    });
                animator.start();
            }
            }
        }
    }
