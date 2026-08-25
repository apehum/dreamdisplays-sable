package dev.apehum.dreamdisplays.sable.binding.event

import com.dreamdisplays.platform.server.utils.RegionUtil
import dev.apehum.dreamdisplays.sable.companion.SubLevelPose
import dev.apehum.dreamdisplays.sable.companion.observeSubLevels
import java.util.UUID
import java.util.concurrent.ConcurrentLinkedQueue

sealed interface BindingEvent {
    data object Reconcile : BindingEvent

    data class DisplayCreated(
        val displayId: UUID,
    ) : BindingEvent

    data class DisplayRemoved(
        val displayId: UUID,
    ) : BindingEvent

    data class SubLevelAdded(
        val worldKey: String,
        val subLevelId: UUID,
    ) : BindingEvent

    data class SubLevelRemoved(
        val subLevelId: UUID,
        val pose: SubLevelPose,
    ) : BindingEvent
}

object PendingBindingEvents {
    private val queue = ConcurrentLinkedQueue<BindingEvent>()

    fun push(event: BindingEvent) {
        queue += event
    }

    fun drain(): List<BindingEvent> = generateSequence(queue::poll).toList()

    fun clear() = queue.clear()
}

fun observeSubLevelEvents() {
    observeSubLevels(
        onAdded = { level, subLevelId ->
            PendingBindingEvents.push(BindingEvent.SubLevelAdded(RegionUtil.INSTANCE.getLevelKey(level), subLevelId))
        },
        onRemoved = { subLevelId, pose -> PendingBindingEvents.push(BindingEvent.SubLevelRemoved(subLevelId, pose)) },
    )
}
