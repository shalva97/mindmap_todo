package org.example.mindmap_todo

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun App() {
    val viewModel = remember { MindMapViewModel() }
    val state = viewModel.state

    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CyberpunkNoir.Slate950)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        viewModel.onPan(dragAmount)
                    }
                }
        ) {
            // Infinite Grid Background (Optional but nice for spatial awareness)
            InfiniteGrid(state.panOffset)

            // Connections
            ConnectionsView(state.nodes, state.panOffset)

            // Nodes
            Box(modifier = Modifier.fillMaxSize()) {
                state.nodes.values.forEach { node ->
                    key(node.id) {
                        NodeView(
                            node = node.copy(position = node.position + state.panOffset),
                            onDrag = { delta -> viewModel.onNodeDrag(node.id, delta) },
                            onToggleTask = { taskId -> viewModel.toggleTask(node.id, taskId) },
                            onUpdateTaskText = { taskId, text -> viewModel.updateTaskText(node.id, taskId, text) }
                        )
                    }
                }
            }

            // UI Overlays
            SaveStatusIndicator(state.saveStatus, modifier = Modifier.align(Alignment.BottomStart))
            MiniMap(state.nodes, modifier = Modifier.align(Alignment.BottomEnd))
        }
    }
}

@Composable
fun InfiniteGrid(panOffset: Offset) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val gridSize = 50.dp.toPx()
        val offsetX = panOffset.x % gridSize
        val offsetY = panOffset.y % gridSize

        for (x in 0..(size.width / gridSize).toInt() + 1) {
            drawLine(
                color = CyberpunkNoir.Slate900,
                start = Offset(x * gridSize + offsetX, 0f),
                end = Offset(x * gridSize + offsetX, size.height),
                strokeWidth = 1f
            )
        }
        for (y in 0..(size.height / gridSize).toInt() + 1) {
            drawLine(
                color = CyberpunkNoir.Slate900,
                start = Offset(0f, y * gridSize + offsetY),
                end = Offset(size.width, y * gridSize + offsetY),
                strokeWidth = 1f
            )
        }
    }
}

@Composable
fun SaveStatusIndicator(status: SaveStatus, modifier: Modifier = Modifier) {
    Text(
        text = status.name,
        color = CyberpunkNoir.Indigo300,
        fontSize = 12.sp,
        modifier = modifier.padding(16.dp)
    )
}

@Composable
fun MiniMap(nodes: Map<String, Node>, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(16.dp)
            .size(150.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(CyberpunkNoir.GlassWhite)
            .border(1.dp, CyberpunkNoir.Indigo500.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            // Very simplified mini-map
            nodes.values.forEach { node ->
                drawRect(
                    color = CyberpunkNoir.Indigo500,
                    topLeft = Offset(node.position.x * 0.05f, node.position.y * 0.05f),
                    size = androidx.compose.ui.geometry.Size(10f, 6f)
                )
            }
        }
    }
}