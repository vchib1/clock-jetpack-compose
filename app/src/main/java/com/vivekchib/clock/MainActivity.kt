package com.vivekchib.clock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vivekchib.clock.ui.theme.ClockTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val space: @Composable () -> Unit = { Spacer(modifier = Modifier.height(10.dp)) }
            val themeMode = isSystemInDarkTheme()
            var darkTheme: Boolean by remember { mutableStateOf(themeMode) }
            var selectedShape: ClockShape by remember { mutableStateOf(ClockShape.Circle) }
            var showNumber: Boolean by remember { mutableStateOf(true) }
            var showDay: Boolean by remember { mutableStateOf(false) }

            ClockTheme(darkTheme) {
                Scaffold(Modifier.fillMaxSize()) {
                    Column(
                        Modifier.padding(it),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Clock(
                            modifier = Modifier.fillMaxSize(.5f),
                            showHourNumbers = showNumber,
                            clockShape = selectedShape,
                            showDay = showDay
                        )

                        space()

                        ListItem(modifier = Modifier.height(40.dp),
                            headlineContent = { Text("Dark Theme") },
                            trailingContent = {
                                Switch(
                                    checked = darkTheme,
                                    onCheckedChange = { darkTheme = !darkTheme })
                            })


                        space()

                        ListItem(modifier = Modifier.height(40.dp),
                            headlineContent = { Text("Show Hours") },
                            trailingContent = {
                                Switch(
                                    checked = showNumber,
                                    onCheckedChange = { showNumber = !showNumber })
                            })

                        space()

                        ListItem(modifier = Modifier.height(40.dp),
                            headlineContent = { Text("Show Day") },
                            trailingContent = {
                                Switch(
                                    checked = showDay,
                                    onCheckedChange = { showDay = !showDay })
                            })

                        space()

                        ListItem(modifier = Modifier.height(70.dp),
                            headlineContent = { Text("Shape") },
                            trailingContent = {
                                SingleChoiceSegmentedButtonRow(Modifier.padding(8.dp)) {
                                    ClockShape.entries.forEachIndexed { index, item ->
                                        SegmentedButton(
                                            selected = selectedShape == item,
                                            onClick = {
                                                selectedShape = item
                                            },
                                            shape = SegmentedButtonDefaults.itemShape(
                                                index = index,
                                                count = ClockShape.entries.size
                                            )
                                        ) {
                                            Text(text = (index + 1).toString())
                                        }
                                    }
                                }
                            })


                    }
                }
            }
        }
    }
}

