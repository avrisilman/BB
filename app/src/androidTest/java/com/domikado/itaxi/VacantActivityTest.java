package com.domikado.itaxi;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.domikado.itaxi.ui.ads.VacantActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class VacantActivityTest {

    @Rule
    public IntentsTestRule<VacantActivity> rule = new IntentsTestRule<>(VacantActivity.class);

    @Test
    public void checkVacantModeIsDisplayed() {
        onView(withText("VACANT")).check(matches(isDisplayed()));
    }

//    @Test
//    public void clickVacantMode() {
//        onView(withText("VACANT")).perform(click());
//        intended(hasComponent(SplashActivity.class.getName()));
//    }
}
