package org.example.mindmap_todo

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.formdev.flatlaf.FlatDarkLaf
import javax.swing.UIManager

fun main() {
    System.setProperty("apple.awt.application.appearance", "system")
    System.setProperty("flatlaf.menuBarEmbedded", "true")
    UIManager.setLookAndFeel(FlatDarkLaf())
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "mindmap_todo",
        ) {
            App()
        }
    }
}