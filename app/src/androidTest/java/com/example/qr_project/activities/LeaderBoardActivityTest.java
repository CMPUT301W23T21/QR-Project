package com.example.qr_project.activities;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsRule;
import static org.hamcrest.core.AllOf.allOf;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.matcher.IntentMatchers.*;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.hasFocus;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

import android.app.Activity;
import android.content.Intent;
import android.widget.ListView;

import com.example.qr_project.R;
import com.example.qr_project.utils.QR_Code;
import com.example.qr_project.utils.UserManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class LeaderBoardActivityTest {
    private UserManager userManager;

    // Rule for performing set up and tear down of Espresso-Intents API
    @Rule
    public IntentsRule intentsRule = new IntentsRule();
    ActivityScenario<Activity> activityScenario;
    private FirestoreIdlingResource firestoreIdlingResource;


    @Before
    public void setUp(){
        // Testing user ID (hardcoded, may not work if database is changed manually)
        String userID = "59b75ac7-3361-4e78-86fd-bb9d8f30f820";
        userManager = UserManager.getInstance();
        userManager.setUserID(userID);

        String key = "filter";
        String filterValue = "user";
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                LeaderboardActivity.class)
                .putExtra(key, filterValue);
        activityScenario = ActivityScenario.launch(intent);
    }

    // Tests if the activity stays the same after Leaderboard button was pressed
    @Test
    public void testLeaderBoardPressed(){
        // Check LeaderboardActivity is still active
        activityScenario.onActivity(activity -> {
            assertTrue(activity instanceof LeaderboardActivity);
        });
    }


    // Tests if 'you' filter updates the list of qrCodes
    @Test
    public void testYouFilter(){
        QR_Code qrCode1 = new QR_Code("Natus");
        QR_Code qrCode2 = new QR_Code("Vincere");

        IdlingRegistry.getInstance().register(firestoreIdlingResource);

        // Increment the operation count before making a FireStore call
        firestoreIdlingResource.increment();

        // Clear all qrCodes before testing
    }


    @After
    public void tearDown(){
        activityScenario.close();
    }






    /**
     * Idling resource for working with Firestore. It tracks firestore operations. Before using
     * any firestore operations, increments its operation count. Once a firestore operation is done
     * and onCompleteListener is called, the operation counter is decremented to signal. The
     * operation count should be 0 before Espresso will continue with testing
     */
    public class FirestoreIdlingResource implements IdlingResource {
        private ResourceCallback resourceCallback;
        private AtomicInteger operationCount = new AtomicInteger(0);

        @Override
        public String getName() {
            return FirestoreIdlingResource.class.getName();
        }

        @Override
        public boolean isIdleNow() {
            boolean isIdle = operationCount.get() == 0;
            if (isIdle && resourceCallback != null) {
                resourceCallback.onTransitionToIdle();
            }
            return isIdle;
        }

        @Override
        public void registerIdleTransitionCallback(ResourceCallback callback) {
            this.resourceCallback = callback;
        }

        public void increment() {
            operationCount.getAndIncrement();
        }

        public void decrement() {
            int count = operationCount.decrementAndGet();
            if (count < 0) {
                throw new IllegalArgumentException("Invalid operation count");
            }
            if (count == 0 && resourceCallback != null) {
                resourceCallback.onTransitionToIdle();
            }
        }
    }
}












