package com.shineofeidos.mockapiproject.ui.screens

import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.shineofeidos.mockapiproject.event.AppEvent
import com.shineofeidos.mockapiproject.event.Bus
import com.shineofeidos.mockapiproject.event.IBusListener

class EventBusLearningScreen : IBusListener {
    private val TAG = "EventBusLearningScreen"
    private var eventReceived by mutableStateOf("")

    override fun onBusEvent(event: com.shineofeidos.mockapiproject.event.BusEvent) {
        if (event is AppEvent) {
            val message = "Received event: ${event.type?.name ?: "Unknown"}, success: ${event.isSuccess}"
            Log.d(TAG, message)
            eventReceived = message
        }
    }

    @Composable
    fun Content() {
        val context = LocalContext.current

        // Register listener when composable is created
        DisposableEffect(Unit) {
            Bus.getInstance().addListener(this@EventBusLearningScreen)
            onDispose {
                Bus.getInstance().removeListener(this@EventBusLearningScreen)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Event Bus Learning",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Surface(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = eventReceived.ifEmpty { "No events received yet" },
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    val event = AppEvent(true, AppEvent.EventTypeEnum.DEMO_LOGIN)
                    event.send()
                }) {
                    Text("Send Login Event")
                }

                Button(onClick = {
                    val event = AppEvent(false, AppEvent.EventTypeEnum.DEMO_LOGOUT)
                    event.send()
                }) {
                    Text("Send Logout Event")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    val event = AppEvent(AppEvent.EventTypeEnum.DEMO_UPDATE_USER)
                    event.sendOnThread()
                }) {
                    Text("Send Update Event (Thread)")
                }

                Button(onClick = {
                    val event = AppEvent(AppEvent.EventTypeEnum.DEMO_LOAD_DATA)
                    event.send(2000)
                }) {
                    Text("Send Load Data Event (Delayed)")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Event Bus Demo Instructions:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "1. Click buttons to send different events\n" +
                        "2. Events will be received and displayed here\n" +
                        "3. Some events are sent on main thread, others on background thread\n" +
                        "4. One event is sent with a 2-second delay",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun EventBusLearningScreenContent() {
    val screen = remember { EventBusLearningScreen() }
    screen.Content()
}