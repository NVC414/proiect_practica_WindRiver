package com.windriver.pcgate;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;

import com.windriver.pcgate.ui.chat.ChatHistoryManager;
import com.windriver.pcgate.ui.chat.ChatMessage;

import java.util.List;

public class MainApplication extends Application {
    private int activityReferences = 0;
    private boolean isActivityChangingConfigurations = false;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, android.os.Bundle savedInstanceState) {}
            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                activityReferences++;
                isActivityChangingConfigurations = activity.isChangingConfigurations();
            }
            @Override
            public void onActivityResumed(@NonNull Activity activity) {}
            @Override
            public void onActivityPaused(@NonNull Activity activity) {}
            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                activityReferences--;
                isActivityChangingConfigurations = activity.isChangingConfigurations();
                if (activityReferences == 0 && !isActivityChangingConfigurations) {
                    saveChatToHistory(activity);
                }
            }
            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull android.os.Bundle outState) {}
            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {}
        });
    }
    private void saveChatToHistory(Activity activity) {
        ChatHistoryManager manager = new ChatHistoryManager(activity.getApplicationContext());
        List<ChatMessage> current = manager.loadCurrentChat();
        if (current != null && !current.isEmpty()) {
            manager.saveChat(current);
            manager.clearCurrentChat();
        }
    }
}
