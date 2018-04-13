package com.hbtl.ui.menu.activity;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by archie on 15/5/27.
 */

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainMenuActivityTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {

    public MainMenuActivityTest() {
        super(MainMenuActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        // Injecting the Instrumentation instance is required
        // for your test to run with AndroidJUnitRunner.
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        getActivity();
    }

    @Rule
    public ActivityTestRule<MainMenuActivity> activityRule = new ActivityTestRule<>(MainMenuActivity.class);

    @Before
    public void checkInitialCount() {
//        onView(withId(R.id.count))
//                .check(matches(withText("0")));
    }

    @Test
    public void helloWorldOnView() {
        onView(withText("个人中心")).check(matches(isDisplayed()));
    }

//    @Test
//    public void greetingMessageWithNameDisplayed() {
//        //onView(withId(R.id.name_edittext)).perform(typeText(USER_NAME));
//        onView(withId(R.id.commonAccountCenter)).perform(click());
//        //onView(withId(R.id.greeting_message)).check(matches(withText(GREETING_MESSAGE)));
//    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

}