package com.example.mystoryapp.ui.login

import androidx.test.InstrumentationRegistry
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mystoryapp.R
import com.example.mystoryapp.ui.main.MainActivity
import com.example.mystoryapp.ui.welcome.WelcomeActivity
import com.example.mystoryapp.util.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {
    private val email = "yourname@gmail.com"
    private val password = "asdasdasd"

    @get:Rule
    val activity = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setUp() {
        Intents.init()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        Intents.release()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }


    @Test
    fun login() {
        Espresso.onView(ViewMatchers.withId(R.id.edtLoginEmail))
            .perform(ViewActions.typeText(email))
        Espresso.onView(ViewMatchers.withId(R.id.edtLoginPassword))
            .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.btnLogin)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(android.R.id.button1)).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(MainActivity::class.java.name))
    }

    @Test
    fun loginAndLogout() {
        login()
        Espresso.openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext())
        Espresso.onView(ViewMatchers.withText(R.string.logout)).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(WelcomeActivity::class.java.name))
    }
}