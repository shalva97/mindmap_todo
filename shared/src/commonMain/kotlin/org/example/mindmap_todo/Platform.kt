package org.example.mindmap_todo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform