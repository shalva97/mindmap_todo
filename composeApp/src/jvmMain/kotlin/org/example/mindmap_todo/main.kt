package org.example.mindmap_todo

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "mindmap_todo",
    ) {
        App()
    }
}