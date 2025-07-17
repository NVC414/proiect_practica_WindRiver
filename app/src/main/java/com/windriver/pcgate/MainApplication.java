package com.windriver.pcgate;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.windriver.pcgate.ui.Chat.ChatHistoryManager;
import com.windriver.pcgate.ui.Chat.ChatMessage;

import java.util.List;

public class MainApplication extends Application {
    private int activityReferences = 0;
    private boolean isActivityChangingConfigurations = false;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}
            @Override
            public void onActivityStarted(Activity activity) {
                activityReferences++;
            }
            @Override
            public void onActivityResumed(Activity activity) {}
            @Override
            public void onActivityPaused(Activity activity) {}
            @Override
            public void onActivityStopped(Activity activity) {
                isActivityChangingConfigurations = activity.isChangingConfigurations();
                activityReferences--;
            }
            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
            @Override
            public void onActivityDestroyed(Activity activity) {
                if (activityReferences == 0 && !isActivityChangingConfigurations) {
                    // App is being killed (swiped away or finished), not just backgrounded
                    saveChatToHistory(activity);
                }
            }
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
