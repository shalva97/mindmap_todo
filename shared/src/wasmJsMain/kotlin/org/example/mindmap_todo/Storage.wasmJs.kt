package org.example.mindmap_todo

fun localStorageSetItem(key: String, value: String): Unit = js("localStorage.setItem(key, value)")
fun localStorageGetItem(key: String): String? = js("localStorage.getItem(key)")

actual fun saveToLocalStorage(key: String, value: String) {
    localStorageSetItem(key, value)
}

actual fun loadFromLocalStorage(key: String): String? {
    return localStorageGetItem(key)
}
