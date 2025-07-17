package com.windriver.pcgate.ui.Chat;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.GenerativeModel;
import com.google.firebase.ai.java.ChatFutures;
import com.google.firebase.ai.java.GenerativeModelFutures;
import com.google.firebase.ai.type.Content;
import com.google.firebase.ai.type.GenerateContentResponse;
import com.google.firebase.ai.type.GenerativeBackend;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.windriver.pcgate.R;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChatFragment extends Fragment implements MenuProvider
    {
    private ChatAdapter chatAdapter;
    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    private ImageButton micButton;
    private View bottomSpace;
    private ChatFutures chat;
    private final List<Content> history = new ArrayList<>();
    private final Executor executor = Executors.newSingleThreadExecutor();
    // Add category-specific keywords
    private static final String[] KEYWORDS = {"case", "cpu", "laptop", "memory", "motherboard", "power-supply", "video-card"};
    private static final String[] CPU_KEYWORDS = {"Ryzen", "Intel"};
    private boolean keywordsAddedToContext = false;
    // Use a list of strings for chat history
    private final List<String> chatHistory = new ArrayList<>();
    private ChatHistoryManager chatHistoryManager;
    private List<ChatMessage> currentMessages = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
        {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        messageInput = view.findViewById(R.id.messageInput);
        sendButton = view.findViewById(R.id.sendButton);
        micButton = view.findViewById(R.id.micButton);
        bottomSpace = view.findViewById(R.id.bottomSpace);
        // setHasOptionsMenu(true); // Removed deprecated call
        // Enable back button in the action bar
        if (getActivity() instanceof AppCompatActivity)
        {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null)
            {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
        chatHistoryManager = new ChatHistoryManager(requireContext());
        return view;
        }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
        {
        super.onViewCreated(view, savedInstanceState);

        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        chatAdapter = new ChatAdapter();
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        chatRecyclerView.setAdapter(chatAdapter);

        // Restore current chat if available
        List<ChatMessage> current = chatHistoryManager.loadCurrentChat();
        chatAdapter.clearMessages();
        currentMessages.clear();
        if (!current.isEmpty()) {
            currentMessages.addAll(current);
            for (ChatMessage msg : current)
                chatAdapter.addMessage(msg);
        }
        // If no current chat, start with an empty chat (do not load from history)

        // Add keywords as context to Gemini (not shown to user)
        if (!keywordsAddedToContext)
        {
            String keywordsContext = "Store keywords: case, cpu, laptop, memory, motherboard, power-supply, video-card.";
            Content keywordsContent = new Content.Builder().setRole("user").addText(
                    keywordsContext).build();
            history.add(keywordsContent);
            keywordsAddedToContext = true;
        }

        // Remove clearChatButton logic (moved to menu)
        view.findViewById(R.id.clearChatButton).setVisibility(View.GONE);

        // Initialize Gemini AI chat
        GenerativeModel ai = FirebaseAI.getInstance(GenerativeBackend.googleAI()).generativeModel(
                "gemini-2.5-flash");
        GenerativeModelFutures model = GenerativeModelFutures.from(ai);
        if (!keywordsAddedToContext)
        {
            String keywordsContext = "Store keywords: case, cpu, laptop, memory, motherboard, power-supply, video-card.";
            Content keywordsContent = new Content.Builder().setRole("user").addText(
                    keywordsContext).build();
            history.add(keywordsContent);
            keywordsAddedToContext = true;
        }
        chat = model.startChat(history);

        sendButton.setOnClickListener(v ->
            {
                String text = messageInput.getText().toString().trim();
                if (!text.isEmpty())
                {
                    handleUserMessage(text);
                }
            });
        messageInput.setOnEditorActionListener(
                (TextView v, int actionId, android.view.KeyEvent event) ->
                    {
                        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == android.view.KeyEvent.KEYCODE_ENTER && event.getAction() == android.view.KeyEvent.ACTION_DOWN))
                        {
                            String text = messageInput.getText().toString().trim();
                            if (!text.isEmpty())
                            {
                                handleUserMessage(text);
                            }
                            return true;
                        }
                        return false;
                    });
        // TODO: Add micButton logic if needed

        // Dynamically adjust bottomSpace height to match navigation bar insets
        Space bottomSpaceView = view.findViewById(R.id.bottomSpace);
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) ->
            {
                if (bottomSpaceView != null)
                {
                    int navBarHeight = insets.getInsets(
                            WindowInsetsCompat.Type.navigationBars()).bottom;
                    ViewGroup.LayoutParams params = bottomSpaceView.getLayoutParams();
                    params.height = navBarHeight;
                    bottomSpaceView.setLayoutParams(params);
                }
                return insets;
            });
        }

    @Override
    public void onStop() {
        super.onStop();
        // Save current chat to temp storage (not history)
        if (chatHistoryManager != null) {
            chatHistoryManager.saveCurrentChat(currentMessages);
        }
    }

    // Remove saving chat to history from onDestroy. Only keep temp save in onStop.
    @Override
    public void onDestroy() {
        super.onDestroy();
        // No chat history save here!
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_chat, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home)
        {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        if (menuItem.getItemId() == R.id.action_new_chat)
        {
            // Save current chat before clearing
            if (!currentMessages.isEmpty())
            {
                chatHistoryManager.saveChat(currentMessages);
            }
            chatAdapter.clearMessages();
            currentMessages.clear();
            if (getView() != null)
            {
                getView().findViewById(R.id.clearChatButton).performClick();
            }
            return true;
        }
        if (menuItem.getItemId() == R.id.action_load_chat)
        {
            showChatHistoryDialog();
            return true;
        }
        return false;
    }

    private void showChatHistoryDialog()
        {
        List<ChatHistoryManager.ChatHistoryItem> history = chatHistoryManager.getChatHistory();
        if (history.isEmpty())
        {
            new AlertDialog.Builder(requireContext()).setTitle("Chat History").setMessage(
                    "No previous chats found.").setPositiveButton("OK", null).show();
            return;
        }
        String[] items = new String[history.size()];
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        for (int i = 0; i < history.size(); i++)
        {
            String uuid = history.get(i).id;
            String shortUuid = uuid.length() > 8 ? uuid.substring(0, 8) : uuid;
            items[i] = "Chat at " + sdf.format(history.get(i).timestamp) + "\nID: " + shortUuid;
        }
        new AlertDialog.Builder(requireContext()).setTitle("Select a chat to load").setItems(items,
                (dialog, which) ->
                    {
                        List<ChatMessage> chat = chatHistoryManager.loadChat(
                                history.get(which).index);
                        chatAdapter.clearMessages();
                        currentMessages.clear();
                        currentMessages.addAll(chat);
                        for (ChatMessage msg : chat)
                            chatAdapter.addMessage(msg);
                    }).setNegativeButton("Cancel", null).show();
        }

    private void handleUserMessage(String text)
        {
        ChatMessage userMsg = new ChatMessage.UserMessage(text);
        chatAdapter.addMessage(userMsg);
        currentMessages.add(userMsg);
        messageInput.setText("");
        chatHistory.add("User: " + text);
        // Detect keyword
        String foundKeyword = null;
        for (String keyword : KEYWORDS)
        {
            if (text.toLowerCase().contains(keyword))
            {
                foundKeyword = keyword;
                break;
            }
        }
        // Check for CPU sub-keywords if cpu is found
        List<String> contextToSend = new ArrayList<>();
        if (foundKeyword != null && foundKeyword.equals("cpu"))
        {
            for (String cpuKeyword : CPU_KEYWORDS)
            {
                if (text.toLowerCase().contains(cpuKeyword.toLowerCase()))
                {
                    contextToSend.add("CPU keyword: " + cpuKeyword);
                }
            }
        }
        if (foundKeyword != null)
        {
            fetchItemsAndSend(foundKeyword, text, contextToSend);
        }
        else
        {
            sendMessageToGemini(text, contextToSend);
        }
        }

    private void fetchItemsAndSend(String keyword, String userInput, List<String> extraContext)
        {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(keyword);
        ref.addListenerForSingleValueEvent(new ValueEventListener()
            {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                List<String> contextToSend = new ArrayList<>(extraContext);
                StringBuilder itemsContext = new StringBuilder();
                for (DataSnapshot itemSnap : snapshot.getChildren())
                {
                    itemsContext.append(itemSnap.getValue()).append("\n");
                }
                if (!TextUtils.isEmpty(itemsContext))
                {
                    contextToSend.add("Store items for " + keyword + ":\n" + itemsContext);
                }
                sendMessageToGemini(userInput, contextToSend);
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
                {
                sendMessageToGemini(userInput, extraContext);
                }
            });
        }

    private void sendMessageToGemini(String userInput, List<String> extraContext)
        {
        chatHistory.add("User: " + userInput);
        StringBuilder contextBuilder = new StringBuilder();
        for (String msg : chatHistory)
        {
            contextBuilder.append(msg).append("\n\n");
        }
        for (String ctx : extraContext)
        {
            contextBuilder.append(ctx).append("\n\n");
        }
        contextBuilder.append(
                "Answer in a friendly and conversational tone. Keep your answer concise—no more than 4 to 5 short lengths paragraphs. Use newlines to separate ideas where appropriate, but avoid overly long paragraphs or dense blocks of text. Do not use markdown formatting or symbols like asterisks or hashtags.\n\nUser's prompt:\n").append(
                userInput);
        Content prompt = new Content.Builder().setRole("user").addText(
                contextBuilder.toString()).build();
        Publisher<GenerateContentResponse> streamingResponse = chat.sendMessageStream(prompt);
        final StringBuilder fullResponse = new StringBuilder();
        final boolean[] aiMessageStarted = {false};
        streamingResponse.subscribe(new Subscriber<GenerateContentResponse>()
            {
            @Override
            public void onSubscribe(Subscription s)
                {
                s.request(Long.MAX_VALUE);
                }

            @Override
            public void onNext(GenerateContentResponse response)
                {
                String chunk = response.getText();
                fullResponse.append(chunk);
                requireActivity().runOnUiThread(() ->
                    {
                        if (!aiMessageStarted[0])
                        {
                            ChatMessage aiMsg = new ChatMessage.AiMessage(fullResponse.toString());
                            chatAdapter.addMessage(aiMsg);
                            currentMessages.add(aiMsg);
                            aiMessageStarted[0] = true;
                        }
                        else
                        {
                            chatAdapter.updateLastAiMessage(fullResponse.toString());
                            // Update last AI message in currentMessages
                            for (int i = currentMessages.size() - 1; i >= 0; i--)
                            {
                                if (currentMessages.get(i) instanceof ChatMessage.AiMessage)
                                {
                                    currentMessages.set(i,
                                            new ChatMessage.AiMessage(fullResponse.toString()));
                                    break;
                                }
                            }
                        }
                    });
                }

            @Override
            public void onError(Throwable t)
                {
                requireActivity().runOnUiThread(() -> chatAdapter.addMessage(
                        new ChatMessage.AiMessage("[Error: " + t.getMessage() + "]")));
                }

            @Override
            public void onComplete()
                {
                String aiText = fullResponse.toString();
                if (!aiText.isEmpty())
                {
                    requireActivity().runOnUiThread(() ->
                        {
                            chatHistory.add("AI: " + aiText);
                        });
                }
                }
            });
        }
    }
