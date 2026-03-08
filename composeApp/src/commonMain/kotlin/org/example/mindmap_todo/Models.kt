package org.example.mindmap_todo

import androidx.compose.ui.geometry.Offset
import androidx.compose.runtime.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object OffsetSerializer : KSerializer<Offset> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Offset", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: Offset) = encoder.encodeString("${value.x},${value.y}")
    override fun deserialize(decoder: Decoder): Offset {
        val (x, y) = decoder.decodeString().split(",").map { it.toFloat() }
        return Offset(x, y)
    }
}

@Serializable
data class Task(
    val id: String,
    val text: String,
    val isCompleted: Boolean = false
)

@Serializable
data class Node(
    val id: String,
    @Serializable(with = OffsetSerializer::class)
    val position: Offset,
    val tasks: List<Task>,
    val parentId: String? = null,
    val childIds: Set<String> = emptySet()
) {
    val completionPercentage: Float
        get() = if (tasks.isEmpty()) 0f else tasks.count { it.isCompleted }.toFloat() / tasks.size
}

@Serializable
data class MindMapState(
    val nodes: Map<String, Node> = emptyMap(),
    @Serializable(with = OffsetSerializer::class)
    val panOffset: Offset = Offset.Zero,
    val saveStatus: SaveStatus = SaveStatus.Synced
)

@Serializable
enum class SaveStatus {
    Initializing, Saving, Synced
}
