package dev.apehum.dreamdisplays.sable.storage

import dev.apehum.dreamdisplays.sable.binding.DisplayBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import net.minecraft.core.BlockPos
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.UUID

class JsonBindingRepository(
    private val file: Path,
) : BindingRepository {
    override suspend fun loadAll(): List<DisplayBinding> =
        withContext(Dispatchers.IO) {
            if (!Files.exists(file)) return@withContext emptyList()

            val document =
                json.decodeFromString(
                    BindingDocument.serializer(),
                    Files.readString(file),
                )
            document.bindings.map(BindingRecord::toBinding)
        }

    override suspend fun save(bindings: Collection<DisplayBinding>) {
        withContext(Dispatchers.IO) {
            file.parent?.let(Files::createDirectories)

            val document = BindingDocument(bindings.map(DisplayBinding::toRecord))
            val temporary = file.resolveSibling("${file.fileName}.tmp")

            Files.writeString(
                temporary,
                json.encodeToString(BindingDocument.serializer(), document),
            )
            Files.move(
                temporary,
                file,
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE,
            )
        }
    }

    private companion object {
        @OptIn(ExperimentalSerializationApi::class)
        val json =
            Json {
                prettyPrint = true
                ignoreUnknownKeys = true
                namingStrategy = JsonNamingStrategy.SnakeCase
            }
    }
}

@Serializable
private data class BindingDocument(
    val bindings: List<BindingRecord> = emptyList(),
)

@Serializable
private data class BindingRecord(
    val displayId: String,
    val subLevelId: String,
    val local1: PositionRecord,
    val local2: PositionRecord,
)

@Serializable
private data class PositionRecord(
    val x: Int,
    val y: Int,
    val z: Int,
)

private fun BindingRecord.toBinding() =
    DisplayBinding(
        displayId = UUID.fromString(displayId),
        subLevelId = UUID.fromString(subLevelId),
        local1 = local1.toBlockPos(),
        local2 = local2.toBlockPos(),
    )

private fun DisplayBinding.toRecord() =
    BindingRecord(
        displayId = displayId.toString(),
        subLevelId = subLevelId.toString(),
        local1 = local1.toRecord(),
        local2 = local2.toRecord(),
    )

private fun PositionRecord.toBlockPos() = BlockPos(x, y, z)

private fun BlockPos.toRecord() = PositionRecord(x, y, z)
