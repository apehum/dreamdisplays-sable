package dev.apehum.dreamdisplays.sable.storage

import com.dreamdisplays.api.server.DreamDisplaysServer
import dev.apehum.dreamdisplays.sable.MOD_ID
import dev.apehum.dreamdisplays.sable.binding.moveTo
import dev.apehum.dreamdisplays.sable.binding.vanillaDisplay
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import net.minecraft.core.BlockPos
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.storage.LevelResource
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.UUID

private const val LEGACY_BINDINGS_FILE = "bindings.json"

private val logger = LoggerFactory.getLogger("DreamDisplaysSable/LegacyBindings")

@OptIn(ExperimentalSerializationApi::class)
private val json =
    Json {
        ignoreUnknownKeys = true
        namingStrategy = JsonNamingStrategy.SnakeCase
    }

fun migrateLegacyBindings(server: MinecraftServer) {
    val file =
        server
            .getWorldPath(LevelResource.LEVEL_DATA_FILE)
            .parent
            .resolve(MOD_ID)
            .resolve(LEGACY_BINDINGS_FILE)

    readLegacyBindings(file).forEach { binding ->
        val data = vanillaDisplay(binding.displayId) ?: return@forEach
        if (data.pos1 == binding.local1 && data.pos2 == binding.local2) return@forEach

        DreamDisplaysServer
            .display(binding.displayId)
            ?.moveTo(data.worldKey, binding.local1, binding.local2, data.facing)
    }
}

private fun readLegacyBindings(file: Path): List<LegacyBinding> {
    if (!Files.exists(file)) return emptyList()

    val bindings =
        runCatching { json.decodeFromString(BindingDocument.serializer(), Files.readString(file)) }
            .onFailure { logger.error("Failed to read legacy display bindings from $file", it) }
            .getOrNull()
            ?.bindings
            ?.map(BindingRecord::toBinding)
            ?: return emptyList()

    runCatching {
        Files.move(
            file,
            file.resolveSibling("${file.fileName}.imported"),
            StandardCopyOption.REPLACE_EXISTING,
        )
    }.onFailure { logger.error("Failed to copy legacy display bindings file $file", it) }

    return bindings
}

private data class LegacyBinding(
    val displayId: UUID,
    val local1: BlockPos,
    val local2: BlockPos,
)

@Serializable
private data class BindingDocument(
    val bindings: List<BindingRecord> = emptyList(),
)

@Serializable
private data class BindingRecord(
    val displayId: String,
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
    LegacyBinding(
        displayId = UUID.fromString(displayId),
        local1 = local1.toBlockPos(),
        local2 = local2.toBlockPos(),
    )

private fun PositionRecord.toBlockPos() = BlockPos(x, y, z)
