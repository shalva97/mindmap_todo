package org.example.mindmap_todo

import java.util.prefs.Preferences

actual fun saveToLocalStorage(key: String, value: String) {
    val prefs = Preferences.userRoot().node("mindmap_todo")
    prefs.put(key, value)
}

actual fun loadFromLocalStorage(key: String): String? {
    val prefs = Preferences.userRoot().node("mindmap_todo")
    return prefs.get(key, null)
}
