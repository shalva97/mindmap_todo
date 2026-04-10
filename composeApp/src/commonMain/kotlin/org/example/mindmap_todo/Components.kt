package org.example.mindmap_todo

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.*
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.key.*
import androidx.compose.ui.focus.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction

@Composable
fun NodeView(
    node: Node,
    onDrag: (Offset) -> Unit,
    onToggleTask: (String) -> Unit,
    onUpdateTaskText: (String, String) -> Unit
) {
    Box(
        modifier = Modifier
            .offset { IntOffset(node.position.x.toInt(), node.position.y.toInt()) }
            .width(350.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CyberpunkNoir.GlassWhite)
            .border(1.dp, CyberpunkNoir.Indigo500.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
    ) {
        // Drag layer: handles node movement when clicking on node's background
        Box(
            modifier = Modifier
                .matchParentSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount)
                    }
                }
        )
        
        val focusManager = LocalFocusManager.current
        Column(modifier = Modifier.padding(16.dp)) {
            // Progress Bar
            LinearProgressIndicator(
                progress = { node.completionPercentage },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = CyberpunkNoir.Indigo500,
                trackColor = CyberpunkNoir.Slate800
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Tasks
            node.tasks.forEach { task ->
                TaskRow(
                    task = task,
                    onToggle = { onToggleTask(task.id) },
                    onUpdateText = { onUpdateTaskText(task.id, it) },
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            }
        }
    }
}

@Composable
fun TaskRow(
    task: Task,
    onToggle: () -> Unit,
    onUpdateText: (String) -> Unit,
    onNext: () -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(
                checkedColor = CyberpunkNoir.Indigo500,
                uncheckedColor = CyberpunkNoir.Indigo300,
                checkmarkColor = CyberpunkNoir.Slate950
            )
        )

        if (isEditing) {
            val focusRequester = remember { FocusRequester() }
            var hasGainedFocus by remember { mutableStateOf(false) }
            BasicTextField(
                value = task.text,
                onValueChange = onUpdateText,
                textStyle = TextStyle(color = CyberpunkNoir.TextMain, fontSize = 14.sp),
                cursorBrush = SolidColor(CyberpunkNoir.Indigo500),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { onNext() }),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
                    .focusRequester(focusRequester)
                    .onKeyEvent {
                        if (it.key == Key.Enter || it.key == Key.Tab) {
                            if (it.type == KeyEventType.KeyDown) {
                                onNext()
                            }
                            true
                        } else {
                            false
                        }
                    }
                    .onFocusChanged {
                        if (it.isFocused) {
                            hasGainedFocus = true
                        } else if (hasGainedFocus) {
                            isEditing = false
                        }
                    },
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .background(CyberpunkNoir.Slate800, RoundedCornerShape(4.dp))
                            .padding(4.dp)
                    ) {
                        innerTextField()
                    }
                }
            )
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        } else {
            Text(
                text = task.text,
                color = if (task.isCompleted) CyberpunkNoir.TextDim else CyberpunkNoir.TextMain,
                fontSize = 14.sp,
                maxLines = 1,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
                    .clickable { isEditing = true }
                    .onFocusChanged {
                        if (it.isFocused) {
                            isEditing = true
                        }
                    }
                    .focusTarget() // Allow focus movement to find this even when not editing
            )
        }
    }
}

@Composable
fun ConnectionsView(nodes: Map<String, Node>, panOffset: Offset) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        nodes.values.forEach { node ->
            node.parentId?.let { parentId ->
                val parent = nodes[parentId]
                if (parent != null) {
                    val start = parent.position + Offset(350f, 100f) + panOffset // Approximate center-right of parent
                    val end = node.position + panOffset // Approximate top-left of child
                    
                    val path = Path().apply {
                        moveTo(start.x, start.y)
                        cubicTo(
                            start.x + 100f, start.y,
                            end.x - 100f, end.y,
                            end.x, end.y
                        )
                    }
                    
                    drawPath(
                        path = path,
                        color = CyberpunkNoir.Indigo500.copy(alpha = 0.4f),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }
        }
    }
}
