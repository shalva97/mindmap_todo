package org.example.mindmap_todo

expect fun saveToLocalStorage(key: String, value: String)
expect fun loadFromLocalStorage(key: String): String?
