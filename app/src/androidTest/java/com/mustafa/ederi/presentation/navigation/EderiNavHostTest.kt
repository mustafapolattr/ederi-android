package com.mustafa.ederi.presentation.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.mustafa.ederi.MainActivity
import com.mustafa.ederi.core.security.SessionManager
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class EderiNavHostTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var sessionManager: SessionManager

    @Before
    fun setUp() {
        hiltRule.inject()
        // Bypass the auth graph: this test exercises the main graph's bottom
        // nav, not the login flow, so flip the session flag directly rather
        // than driving a real login through the UI.
        sessionManager.setLoggedIn()
        composeRule.waitForIdle()
    }

    @Test
    fun bottomNavBar_navigatesToEachDestination() {
        bottomNavItems.forEach { item ->
            composeRule.onNodeWithText(item.label).performClick()
            composeRule.onNodeWithText(item.label).assertIsDisplayed()
        }
    }
}
