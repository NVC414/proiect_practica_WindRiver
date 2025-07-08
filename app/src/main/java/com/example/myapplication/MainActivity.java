package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;
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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private ActivityMainBinding binding;
    private GenerativeModelFutures generativeModel; // Store the model if needed across methods

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login_activity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Initialize Firebase Database
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            mDatabase = FirebaseDatabase.getInstance().getReference();
        } catch (Exception e) {
            // Error enabling Firebase persistence (already enabled or other issue)
            mDatabase = FirebaseDatabase.getInstance().getReference();
        }

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_profile, R.id.navigation_cart).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

// Initialize the Gemini Developer API backend service
// Create a `GenerativeModel` instance with a model that supports your use case
        GenerativeModel ai = FirebaseAI.getInstance(GenerativeBackend.googleAI())
                .generativeModel("gemini-2.5-flash");

// Use the GenerativeModelFutures Java compatibility layer which offers
// support for ListenableFuture and Publisher APIs
        GenerativeModelFutures model = GenerativeModelFutures.from(ai);


    }

        public void sendMessage(View view) {
        //retrieve from firebase database and send data to the textView2
            //retrieve a random value from 0 to 4999
//            Random r = new Random();
//            int low = 0;
//            int high = 4999;
//            int result = r.nextInt(high-low) + low;
//            System.out.println(String.valueOf(result));
            mDatabase.child("video-card").child("0").get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    DataSnapshot dataSnapshot = task.getResult();
                    TextView textView2=findViewById(R.id.textView2);
                    if (dataSnapshot.exists()) {
                        StringBuilder builder = new StringBuilder();
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            String key = childSnapshot.getKey();
                            Object value = childSnapshot.getValue();
                            builder.append(key).append(": ").append(value).append("\n");
                        }
                        textView2.setText(builder.toString());
                    } else {
                        textView2.setText("No data found");
                    }
                }
            });

        }

    public CompletableFuture<String> requestDataAsync(String userInput) {
        GenerativeModel ai = FirebaseAI.getInstance(GenerativeBackend.googleAI())
                .generativeModel("gemini-2.5-flash");

        GenerativeModelFutures model = GenerativeModelFutures.from(ai);

        Content prompt = new Content.Builder()
                .addText("Answer in a friendly and conversational tone. Keep your answer concise—no more than 4 short lengths paragraphs. Use newlines to separate ideas where appropriate, but avoid overly long paragraphs or dense blocks of text. Do not use markdown formatting or symbols like asterisks or hashtags.\n\nUser's prompt:\n" + userInput)
                .build();


        ListenableFuture<GenerateContentResponse> response = model.generateContent(prompt);

        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                if (result != null && result.getText() != null) {
                    completableFuture.complete(result.getText());
                } else {
                    completableFuture.complete(""); // Or handle as error
                }
            }

            @Override
            public void onFailure(Throwable t) {
                completableFuture.completeExceptionally(t);
            }
        }, executor);

        return completableFuture;
    }

    public void requestData(View view) {
        // This method assumes R.id.userPrompt and R.id.aiPrompt are accessible.
        // Similar to sendMessage, these should ideally be handled within the Fragment that owns them.

        EditText userPromptEditText = findViewById(R.id.userPrompt); // CAUTION
        TextView aiPromptTextView = findViewById(R.id.aiPrompt);   // CAUTION

        if (userPromptEditText == null) {
            Toast.makeText(this, "Input field not found.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (aiPromptTextView == null) {
            Toast.makeText(this, "AI output view not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        String input = userPromptEditText.getText().toString();
        if (input.trim().isEmpty()) {
            Toast.makeText(this, "Please enter a prompt.", Toast.LENGTH_SHORT).show();
            return;
        }

        aiPromptTextView.setText("Generating response...");

        requestDataAsync(input).thenAccept(text ->
                runOnUiThread(() -> {
                    if (text != null && !text.isEmpty()) {
                        aiPromptTextView.setText(text);
                    } else {
                        aiPromptTextView.setText("Received empty response from AI.");
                    }
                })
        ).exceptionally(throwable -> {
            runOnUiThread(() -> {
                aiPromptTextView.setText("Error: " + throwable.getMessage());
            });
            return null;
        });
    }
    }