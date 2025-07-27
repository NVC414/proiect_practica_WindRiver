package com.windriver.pcgate.ui.chat;

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

import lombok.Getter;

public class ChatFragment extends Fragment implements MenuProvider
    {
    private ChatAdapter chatAdapter;
    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    private ChatFutures chat;
    private final List<Content> history = new ArrayList<>();
    @Getter
    private final Executor executor = Executors.newSingleThreadExecutor();

    private static final String[] KEYWORDS = {"case", "cpu", "laptop", "memory", "motherboard", "power-supply", "video-card", "processor", "ram", "psu", "gpu"};
    private static final String[] CPU_KEYWORDS = {"Ryzen", "Intel"};
    private static final java.util.Map<String, String> KEYWORD_CATEGORY_MAP = new java.util.HashMap<>()
        {{
            put("cpu", "cpu");
            put("processor", "cpu");
            put("memory", "memory");
            put("ram", "memory");
            put("power-supply", "power-supply");
            put("psu", "power-supply");
            put("video-card", "video-card");
            put("gpu", "video-card");
            put("case", "case");
            put("laptop", "laptop");
            put("motherboard", "motherboard");
        }};

    private final List<String> chatHistory = new ArrayList<>();
    private ChatHistoryManager chatHistoryManager;
    private final List<ChatMessage> currentMessages = new ArrayList<>();
    private String currentChatId = null;
    private boolean keywordsAddedToContext = false;

    // User data cache
    private String cachedUserData = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
        {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        messageInput = view.findViewById(R.id.messageInput);
        sendButton = view.findViewById(R.id.sendButton);


        if (getActivity() instanceof AppCompatActivity activity)
        {
            if (activity.getSupportActionBar() != null)
            {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
        chatHistoryManager = new ChatHistoryManager(requireContext());
        return view;
        }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
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


        List<ChatMessage> current = chatHistoryManager.loadCurrentChat();
        chatAdapter.clearMessages();
        currentMessages.clear();
        if (!current.isEmpty())
        {
            currentMessages.addAll(current);
            for (ChatMessage msg : current)
                chatAdapter.addMessage(msg);
            chatRecyclerView.post(
                    () -> chatRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1));
        }
        currentChatId = null;


        if (!keywordsAddedToContext)
        {
            String keywordsContext = getString(R.string.ai_keywords_categories);
            Content keywordsContent = new Content.Builder().setRole("user").addText(
                    keywordsContext).build();
            history.add(keywordsContent);
            keywordsAddedToContext = true;
        }


        view.findViewById(R.id.clearChatButton).setVisibility(View.GONE);


        GenerativeModel ai = FirebaseAI.getInstance(GenerativeBackend.googleAI()).generativeModel(
                "gemini-2.5-flash");
        GenerativeModelFutures model = GenerativeModelFutures.from(ai);
        if (!keywordsAddedToContext)
        {
            String keywordsContext = getString(R.string.ai_keywords_categories);
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
        fetchAndCacheUserData();
        }

    @Override
    public void onStop()
        {
        super.onStop();
        if (chatHistoryManager != null)
        {
            chatHistoryManager.saveCurrentChat(currentMessages);
        }
        }

    @Override
    public void onPause()
        {
        super.onPause();
        if (chatHistoryManager != null)
        {
            chatHistoryManager.saveCurrentChat(currentMessages);
            chatHistoryManager.saveChat(currentMessages, currentChatId);
        }
        if (isVisible() && !requireActivity().isChangingConfigurations())
        {
            androidx.navigation.NavController navController = androidx.navigation.Navigation.findNavController(
                    requireActivity(), R.id.nav_host_fragment_activity_main);
            if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() == R.id.chatFragment)
            {
                navController.navigate(R.id.navigation_home);
            }
        }
        }

    @Override
    public void onDestroy()
        {
        super.onDestroy();

        }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater)
        {
        menuInflater.inflate(R.menu.menu_chat, menu);
        }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem)
        {
        if (menuItem.getItemId() == android.R.id.home)
        {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        if (menuItem.getItemId() == R.id.action_new_chat)
        {

            if (!currentMessages.isEmpty())
            {
                chatHistoryManager.saveChat(currentMessages, currentChatId);
            }
            chatAdapter.clearMessages();
            currentMessages.clear();
            currentChatId = null;
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
            String uuid = history.get(i).id();
            String shortUuid = uuid.length() > 8 ? uuid.substring(0, 8) : uuid;
            items[i] = "Chat at " + sdf.format(history.get(i).timestamp()) + "\nID: " + shortUuid;
        }
        new AlertDialog.Builder(requireContext()).setTitle("Select a chat to load").setItems(items,
                (dialog, which) ->
                    {
                        List<ChatMessage> chat = chatHistoryManager.loadChat(
                                history.get(which).index());
                        chatAdapter.clearMessages();
                        currentMessages.clear();
                        currentMessages.addAll(chat);
                        for (ChatMessage msg : chat)
                            chatAdapter.addMessage(msg);
                        // Sort of works? Va trebui sa ma uit mai mult ig
                        currentChatId = history.get(which).id();
                    }).setNegativeButton("Cancel", null).show();
        }

    private void handleUserMessage(String text)
        {
        ChatMessage userMsg = new ChatMessage.UserMessage(text);
        requireActivity().runOnUiThread(() ->
            {
                chatAdapter.addMessage(userMsg);
                chatRecyclerView.post(
                        () -> chatRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1));
            });
        currentMessages.add(userMsg);
        messageInput.setText("");
        chatHistory.add("User: " + text);

        String mappedCategory = null;
        String lowerText = text.toLowerCase();
        boolean mentionsGpu = lowerText.contains("gpu") || lowerText.contains("video-card");
        boolean mentionsRam = lowerText.contains("ram") || lowerText.contains("memory");
        if (mentionsGpu && mentionsRam)
        {
            mappedCategory = KEYWORD_CATEGORY_MAP.get("gpu");
        }
        else
        {
            for (String keyword : KEYWORDS)
            {
                if (lowerText.contains(keyword))
                {
                    mappedCategory = KEYWORD_CATEGORY_MAP.get(keyword);
                    break;
                }
            }
        }

        List<String> contextToSend = new ArrayList<>();
        if (mappedCategory != null && mappedCategory.equals("cpu"))
        {
            for (String cpuKeyword : CPU_KEYWORDS)
            {
                if (lowerText.contains(cpuKeyword.toLowerCase()))
                {
                    contextToSend.add("CPU keyword: " + cpuKeyword);
                }
            }
        }
        if (mappedCategory != null)
        {
            fetchItemsAndSend(mappedCategory, text, contextToSend);
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
        StringBuilder contextBuilder = getStringBuilder(userInput, extraContext);
        Content prompt = new Content.Builder().setRole("user").addText(
                contextBuilder.toString()).build();
        Publisher<GenerateContentResponse> streamingResponse = chat.sendMessageStream(prompt);
        final StringBuilder fullResponse = new StringBuilder();
        final boolean[] aiMessageStarted = {false};
        streamingResponse.subscribe(new Subscriber<>()
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
                            chatRecyclerView.post(() -> chatRecyclerView.scrollToPosition(
                                    chatAdapter.getItemCount() - 1));
                        }
                        else
                        {
                            chatAdapter.updateLastAiMessage(fullResponse.toString());

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
                requireActivity().runOnUiThread(() ->
                    {
                        chatAdapter.addMessage(
                                new ChatMessage.AiMessage("[Error: " + t.getMessage() + "]"));
                        chatRecyclerView.post(() -> chatRecyclerView.scrollToPosition(
                                chatAdapter.getItemCount() - 1));
                    });
                }

            @Override
            public void onComplete()
                {
                String aiText = fullResponse.toString();
                if (!aiText.isEmpty())
                {
                    requireActivity().runOnUiThread(() -> chatHistory.add("AI: " + aiText));
                }
                }
            });
        }

    private String getUserDataString()
        {
        if (cachedUserData != null)
        {
            return cachedUserData;
        }
        com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        if (user == null)
        {
            return "";
        }
        String uid = user.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        // Try to fetch synchronously (not recommended, but for contextBuilder, we cache after first async fetch)
        // In production, you should fetch asynchronously and update the cache, then refresh the UI or retry the AI call.
        ref.addListenerForSingleValueEvent(new ValueEventListener()
            {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                if (snapshot.exists())
                {
                    String date = snapshot.child("date").getValue(String.class);
                    String name = snapshot.child("fullName").getValue(String.class);
                    String gender = snapshot.child("gender").getValue(String.class);
                    String occupation = snapshot.child("occupation").getValue(String.class);
                    StringBuilder sb = new StringBuilder();
                    if (date != null)
                    {
                        sb.append(date).append(", ");
                    }
                    if (name != null)
                    {
                        sb.append(name).append(", ");
                    }
                    if (gender != null)
                    {
                        sb.append(gender).append(", ");
                    }
                    if (occupation != null)
                    {
                        sb.append(occupation);
                    }
                    cachedUserData = sb.toString();
                }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
                {
                }
            });
        return cachedUserData != null ? cachedUserData : "";
        }

    @NonNull
    private StringBuilder getStringBuilder(String userInput, List<String> extraContext)
        {
        StringBuilder contextBuilder = new StringBuilder();
        String userData = getUserDataString();
        if (!userData.isEmpty())
        {
            contextBuilder.append("User's data: ").append(userData).append("\n");
        }
        for (String msg : chatHistory)
        {
            contextBuilder.append(msg).append("\n\n");
        }
        for (String ctx : extraContext)
        {
            contextBuilder.append(ctx).append("\n\n");
        }
        contextBuilder.append("""
                Answer in a friendly and conversational tone, and use the user's first name to address them. \
                Keep your answer concise—no more than 4 to 5 short lengths paragraphs. \
                Use newlines to separate ideas where appropriate, but avoid overly long paragraphs or dense blocks of text. \
                Do not use markdown formatting or symbols like asterisks or hashtags. \
                If the user asks anything outside the scope of the store, politely inform them that you can only help with store-related questions. \
                Adapt responses based on the user's occupation and their level of computer proficiency, ensuring explanations match their technical understanding.
                """);
        contextBuilder.append("User's prompt:\n").append(userInput);
        return contextBuilder;
        }

    private void fetchAndCacheUserData()
        {
        com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        if (user == null)
        {
            return;
        }
        String uid = user.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener()
            {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                if (snapshot.exists())
                {
                    String date = snapshot.child("date").getValue(String.class);
                    String name = snapshot.child("fullName").getValue(String.class);
                    String gender = snapshot.child("gender").getValue(String.class);
                    String occupation = snapshot.child("occupation").getValue(String.class);
                    StringBuilder sb = new StringBuilder();
                    if (date != null)
                    {
                        sb.append(date).append(", ");
                    }
                    if (name != null)
                    {
                        sb.append(name).append(", ");
                    }
                    if (gender != null)
                    {
                        sb.append(gender).append(", ");
                    }
                    if (occupation != null)
                    {
                        sb.append(occupation);
                    }
                    cachedUserData = sb.toString();
                }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
                {
                }
            });
        }
    }
