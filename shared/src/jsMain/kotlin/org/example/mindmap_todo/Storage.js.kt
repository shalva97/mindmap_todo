package org.example.mindmap_todo

actual fun saveToLocalStorage(key: String, value: String) {
    js("window.localStorage.setItem(key, value)")
}

actual fun loadFromLocalStorage(key: String): String? {
    return js("window.localStorage.getItem(key)")
}
