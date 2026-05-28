package parth.appdev.edgeaiassistant

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ChatFlowTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun inputFieldExists() {
        composeRule
            .onNodeWithContentDescription("Command input field")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun sendButtonExists() {
        composeRule
            .onNodeWithContentDescription("Send command")
            .assertExists()
    }

    @Test
    fun typingMessageShowsInInput() {
        composeRule
            .onNodeWithContentDescription("Command input field")
            .performTextInput("calculate 2 plus 2")

        composeRule
            .onNodeWithText("calculate 2 plus 2")
            .assertExists()
    }

    @Test
    fun sendingMessageShowsUserBubble() {
        composeRule
            .onNodeWithContentDescription("Command input field")
            .performTextInput("calculate 2 plus 2")

        composeRule
            .onNodeWithContentDescription("Send command")
            .performClick()

        // User message bubble should appear
        composeRule
            .onNodeWithText("calculate 2 plus 2")
            .assertExists()
    }

    @Test
    fun sendingCalculationShowsResult() {
        composeRule
            .onNodeWithContentDescription("Command input field")
            .performTextInput("calculate 2 plus 2")

        composeRule
            .onNodeWithContentDescription("Send command")
            .performClick()

        // Wait for response
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule
                .onAllNodesWithText("Result: 4.0", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeRule
            .onNodeWithText("Result: 4.0", substring = true)
            .assertExists()
    }

    @Test
    fun bottomNavigationHasFourTabs() {
        composeRule.onNodeWithText("Chat").assertExists()
        composeRule.onNodeWithText("Notes").assertExists()
        composeRule.onNodeWithText("Analytics").assertExists()
        composeRule.onNodeWithText("Settings").assertExists()
    }

    @Test
    fun navigatingToNotesWorks() {
        composeRule.onNodeWithText("Notes").performClick()
        composeRule.onNodeWithText("My Notes").assertExists()
    }

    @Test
    fun navigatingToSettingsWorks() {
        composeRule.onNodeWithText("Settings").performClick()
        composeRule.onNodeWithText("Settings").assertExists()
    }

    @Test
    fun navigatingToAnalyticsWorks() {
        composeRule.onNodeWithText("Analytics").performClick()
        composeRule.onNodeWithText("Analytics").assertExists()
    }
}