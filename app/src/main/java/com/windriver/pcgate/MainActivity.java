package com.windriver.pcgate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.GenerativeModel;
import com.google.firebase.ai.java.GenerativeModelFutures;
import com.google.firebase.ai.type.Content;
import com.google.firebase.ai.type.GenerateContentResponse;
import com.google.firebase.ai.type.GenerativeBackend;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.windriver.pcgate.databinding.ActivityMainBinding;
import com.windriver.pcgate.ui.Cart.CartViewModel;
import com.windriver.pcgate.ui.LoginRegister.Login_activity;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity
    {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private ActivityMainBinding binding;
    private GenerativeModelFutures generativeModel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null)
        {
            Intent intent = new Intent(getApplicationContext(), Login_activity.class);
            startActivity(intent);
            finish();
            return;
        }


        try
        {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            mDatabase = FirebaseDatabase.getInstance().getReference();
        }
        catch (Exception e)
        {

            mDatabase = FirebaseDatabase.getInstance().getReference();
        }

        setupNavigation();
        initializeGeminiAI();
        }

    private void setupNavigation()
        {
        BottomNavigationView navView = binding.navView;
        CartViewModel cartViewModel = CartViewModel.getInstance();
        cartViewModel.getCartItems().observe(this, items ->
            {
                int count = 0;
                for (com.windriver.pcgate.ui.Cart.CartItem item : items)
                {
                    count += item.getQuantity();
                }
                if (count > 0)
                {
                    navView.getOrCreateBadge(R.id.navigation_cart).setNumber(count);
                    navView.getOrCreateBadge(R.id.navigation_cart).setVisible(true);
                }
                else
                {
                    navView.removeBadge(R.id.navigation_cart);
                }
            });

        if (navView == null)
        {
            Toast.makeText(this, "Error: Navigation view missing.", Toast.LENGTH_LONG).show();
            return;
        }

        View navHostFragment = findViewById(R.id.nav_host_fragment_activity_main);
        if (navHostFragment == null)
        {
            Toast.makeText(this, "Error: Navigation host missing.", Toast.LENGTH_LONG).show();
            return;
        }

        NavController navController = Navigation.findNavController(this,
                R.id.nav_host_fragment_activity_main);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_profile, R.id.navigation_cart).build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);


        navController.addOnDestinationChangedListener((controller, destination, arguments) ->
            {
                if (destination.getId() == R.id.chatFragment)
                {
                    binding.navView.setVisibility(View.GONE);
                }
                else
                {
                    binding.navView.setVisibility(View.VISIBLE);
                }
            });
        }

    private void initializeGeminiAI()
        {
        try
        {
            GenerativeModel ai = FirebaseAI.getInstance(
                    GenerativeBackend.googleAI()).generativeModel(
                    "gemini-2.5-flash");
            this.generativeModel = GenerativeModelFutures.from(ai);
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Error initializing AI features.", Toast.LENGTH_SHORT).show();
        }
        }






    public void sendMessage(View view)
        {





        if (mDatabase == null)
        {
            Toast.makeText(this, "Database not available.", Toast.LENGTH_SHORT).show();
            return;
        }

        TextView textView2 = findViewById(R.id.textView2);
        if (textView2 == null)
        {
            Toast.makeText(this, "Output view (textView2) not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        mDatabase.child("video-card").child("0").get().addOnCompleteListener(task ->
            {
                if (!task.isSuccessful())
                {
                    textView2.setText("Error fetching data.");
                }
                else
                {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot != null && dataSnapshot.exists())
                    {
                        StringBuilder builder = new StringBuilder();
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren())
                        {
                            String key = childSnapshot.getKey();
                            Object value = childSnapshot.getValue();
                            builder.append(key).append(": ").append(value).append("\n");
                        }
                        textView2.setText(builder.toString());
                    }
                    else
                    {
                        textView2.setText("No data found at location.");
                    }
                }
            });
        }

    public CompletableFuture<String> requestDataAsync(String userInput)
        {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        if (this.generativeModel == null)
        {
            Toast.makeText(this, "AI Model not initialized.", Toast.LENGTH_SHORT).show();
            completableFuture.completeExceptionally(
                    new IllegalStateException("GenerativeModel not initialized."));
            return completableFuture;
        }

        Content prompt = new Content.Builder().addText(
                "Answer in a friendly and conversational tone. Keep your answer concise—no more than 4 short lengths paragraphs. Use newlines to separate ideas where appropriate, but avoid overly long paragraphs or dense blocks of text. Do not use markdown formatting or symbols like asterisks or hashtags.\n\nUser's prompt:\n" + userInput).build();

        ListenableFuture<GenerateContentResponse> response = this.generativeModel.generateContent(
                prompt);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>()
            {
            @Override
            public void onSuccess(GenerateContentResponse result)
                {
                if (result != null && result.getText() != null)
                {
                    completableFuture.complete(result.getText());
                }
                else
                {
                    completableFuture.complete("");
                }
                }

            @Override
            public void onFailure(Throwable t)
                {
                completableFuture.completeExceptionally(t);
                }
            }, executor);

        return completableFuture;
        }

    public void requestData(View view)
        {



        EditText userPromptEditText = findViewById(R.id.userPrompt);
        TextView aiPromptTextView = findViewById(R.id.aiPrompt);

        if (userPromptEditText == null)
        {
            Toast.makeText(this, "Input field not found.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (aiPromptTextView == null)
        {
            Toast.makeText(this, "AI output view not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        String input = userPromptEditText.getText().toString();
        if (input.trim().isEmpty())
        {
            Toast.makeText(this, "Please enter a prompt.", Toast.LENGTH_SHORT).show();
            return;
        }

        aiPromptTextView.setText("Generating response...");

        requestDataAsync(input).thenAccept(text -> runOnUiThread(() ->
            {
                if (text != null && !text.isEmpty())
                {
                    aiPromptTextView.setText(text);
                }
                else
                {
                    aiPromptTextView.setText("Received empty response from AI.");
                }
            })).exceptionally(throwable ->
            {
                runOnUiThread(() ->
                    {
                        aiPromptTextView.setText("Error: " + throwable.getMessage());
                    });
                return null;
            });
        }
    }