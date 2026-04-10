package org.example.mindmap_todo

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.formdev.flatlaf.FlatDarkLaf
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.swing.UIManager

fun main() {
    System.setProperty("apple.awt.application.appearance", "system")
    System.setProperty("flatlaf.menuBarEmbedded", "true")
    UIManager.setLookAndFeel(FlatDarkLaf())
    application {
        val savedWidth = loadFromLocalStorage("window_width")?.toIntOrNull() ?: 1200
        val savedHeight = loadFromLocalStorage("window_height")?.toIntOrNull() ?: 800
        
        val windowState = rememberWindowState(
            size = DpSize(savedWidth.dp, savedHeight.dp)
        )

        LaunchedEffect(windowState) {
            snapshotFlow { windowState.size }
                .distinctUntilChanged()
                .onEach { size ->
                    saveToLocalStorage("window_width", size.width.value.toInt().toString())
                    saveToLocalStorage("window_height", size.height.value.toInt().toString())
                }
                .launchIn(this)
        }

        Window(
            onCloseRequest = ::exitApplication,
            title = "mindmap_todo",
            state = windowState
        ) {
            App()
        }
    }
}