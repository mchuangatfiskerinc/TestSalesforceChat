package com.fisker.sfchat

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.fisker.myapplication.ui.theme.MyApplicationTheme
import com.salesforce.android.chat.core.ChatConfiguration
import com.salesforce.android.chat.core.model.ChatUserData
import com.salesforce.android.chat.ui.ChatUI
import com.salesforce.android.chat.ui.ChatUIClient
import com.salesforce.android.chat.ui.ChatUIConfiguration
import com.salesforce.android.chat.ui.model.PreChatTextInputField

class MainActivity : ComponentActivity() {

    private val chatContactFirstNameC = "Chat_Contact_First_Name__c"
    private val chatContactLastNameC = "Chat_Contact_Last_Name__c"
    private val chatContactEmailC = "Chat_Contact_Email__c"
    private val chatLanguageC = "Chat_Language__c"
    private val chatLanguage = "Chat Language"
    private val firstNameLabel = "First Name"
    private val lastNameLabel = "Last Name"
    private val emailAddressLabel = "Email Address"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    GreetingPreview()
                }
            }
        }
    }


    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        MyApplicationTheme {
            loadChatBot()
        }
    }

    @Composable
    private fun loadChatBot() {
        val firstName = PreChatTextInputField.Builder().required(true).inputType(EditorInfo.TYPE_TEXT_VARIATION_PERSON_NAME).mapToChatTranscriptFieldName(chatContactFirstNameC).readOnly(true).initialValue("Mike").build("Please enter your first name", firstNameLabel)
        val lastName = PreChatTextInputField.Builder().required(true).inputType(EditorInfo.TYPE_TEXT_VARIATION_PERSON_NAME).mapToChatTranscriptFieldName(chatContactLastNameC).readOnly(true).initialValue("SF Chat").build("Please enter your last name", lastNameLabel)
        val email = PreChatTextInputField.Builder().required(true).inputType(EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS).mapToChatTranscriptFieldName(chatContactEmailC).readOnly(true).initialValue("sfchat@fiskerinc.com").build("Please enter your email", emailAddressLabel)
        val language = ChatUserData(chatLanguage, "en_US", true, chatLanguageC)

        val chatConfigurationBuilder = ChatConfiguration.Builder(
            BuildConfig.salesforce_org_id,
            BuildConfig.salesforce_button_id,
            BuildConfig.salesforce_deployment_id,
            BuildConfig.salesforce_live_agent_id
        )

        chatConfigurationBuilder
            .chatUserData(firstName, lastName, email, language)

        val chatConfiguration = chatConfigurationBuilder.build()

        val uiConfig = ChatUIConfiguration.Builder()
            .chatConfiguration(chatConfiguration)
            .defaultToMinimized(false)
            .allowBackgroundNotifications(false)
            .build()

        ChatUI.configure(uiConfig)
            .createClient(this)
            .onResult { _, chatUIClient ->
                chatUIClient.apply {
                    chatUIClientAppLevel = chatUIClient
                    startChatSession(this@MainActivity)
                }
            }
    }

    fun openChatBot(chatConfiguration: ChatConfiguration) {
        val uiConfig = ChatUIConfiguration.Builder()
            .chatConfiguration(chatConfiguration)
            .defaultToMinimized(false)
            .build()
        ChatUI.configure(uiConfig)
            .createClient(this)
            .onResult { _, chatUIClient ->
                chatUIClient.apply {
                    startChatSession(this@MainActivity)
                }
            }
    }

    companion object {
        var chatUIClientAppLevel: ChatUIClient? = null
    }
}