package org.example.mindmap_todo

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.SupervisorJob

class MindMapViewModel {
    var state by mutableStateOf(MindMapState(saveStatus = SaveStatus.Initializing))
        private set

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var saveJob: Job? = null

    init {
        scope.launch {
            val saved = withContext(Dispatchers.Default) { loadFromLocalStorage("mindmap_state") }
            if (saved != null) {
                try {
                    val loadedState = Json.decodeFromString<MindMapState>(saved)
                    if (loadedState.nodes.isEmpty()) {
                        createInitialState()
                    } else {
                        state = loadedState.copy(saveStatus = SaveStatus.Synced)
                    }
                } catch (e: Exception) {
                    createInitialState()
                }
            } else {
                createInitialState()
            }
        }
    }

    fun resetState() {
        createInitialState()
    }

    private fun createInitialState() {
        val rootNode = Node(
            id = "root",
            position = Offset(200f, 200f),
            tasks = List(5) { i -> Task(id = "task_$i", text = "Task ${i + 1}") }
        )
        state = MindMapState(
            nodes = mapOf(rootNode.id to rootNode),
            panOffset = Offset.Zero,
            saveStatus = SaveStatus.Synced
        )
        saveState()
    }

    private fun saveState() {
        saveJob?.cancel()
        saveJob = scope.launch {
            state = state.copy(saveStatus = SaveStatus.Saving)
            delay(500) // Debounce
            
            val stateToSave = state 
            val json = Json.encodeToString(stateToSave)
            
            withContext(Dispatchers.Default) {
                saveToLocalStorage("mindmap_state", json)
            }
            
            state = state.copy(saveStatus = SaveStatus.Synced)
        }
    }

    fun onPan(delta: Offset) {
        state = state.copy(panOffset = state.panOffset + delta)
        saveState()
    }

    fun onNodeDrag(nodeId: String, delta: Offset) {
        val node = state.nodes[nodeId] ?: return
        val newNode = node.copy(position = node.position + delta)
        state = state.copy(nodes = state.nodes.toMutableMap().apply { put(nodeId, newNode) })
        saveState()
    }

    fun toggleTask(nodeId: String, taskId: String) {
        val node = state.nodes[nodeId] ?: return
        val newTasks = node.tasks.map {
            if (it.id == taskId) it.copy(isCompleted = !it.isCompleted) else it
        }
        val newNode = node.copy(tasks = newTasks)
        val updatedNodes = state.nodes.toMutableMap()
        updatedNodes[nodeId] = newNode

        val taskIndex = node.tasks.indexOfFirst { it.id == taskId }
        val taskCompleted = newTasks[taskIndex].isCompleted
        val childId = "${nodeId}_child_$taskIndex"

        if (taskCompleted) {
            if (childId !in node.childIds) {
                val newNodeWithChild = newNode.copy(childIds = newNode.childIds + childId)
                updatedNodes[nodeId] = newNodeWithChild
                val childNode = Node(
                    id = childId,
                    position = newNode.position + Offset(350f, (taskIndex - 2) * 100f),
                    tasks = List(5) { i -> Task(id = "${childId}_task_$i", text = "Sub-task ${i + 1}") },
                    parentId = nodeId
                )
                updatedNodes[childId] = childNode
            }
        } else {
            if (childId in node.childIds) {
                val newNodeWithoutChild = newNode.copy(childIds = newNode.childIds - childId)
                updatedNodes[nodeId] = newNodeWithoutChild
                removeNodeAndDescendants(updatedNodes, childId)
            }
        }

        state = state.copy(nodes = updatedNodes)
        saveState()
    }

    private fun removeNodeAndDescendants(nodes: MutableMap<String, Node>, nodeId: String) {
        val node = nodes[nodeId] ?: return
        node.childIds.forEach { removeNodeAndDescendants(nodes, it) }
        nodes.remove(nodeId)
    }

    fun updateTaskText(nodeId: String, taskId: String, newText: String) {
        val node = state.nodes[nodeId] ?: return
        val newTasks = node.tasks.map {
            if (it.id == taskId) it.copy(text = newText) else it
        }
        val newNode = node.copy(tasks = newTasks)
        state = state.copy(nodes = state.nodes.toMutableMap().apply { put(nodeId, newNode) })
        saveState()
    }
}
