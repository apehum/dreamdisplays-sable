package dev.apehum.dreamdisplays.sable.binding

import com.dreamdisplays.platform.server.datatypes.display.VanillaDisplayData
import com.dreamdisplays.platform.server.managers.DisplayManager
import java.util.UUID

fun vanillaDisplays(): List<VanillaDisplayData> = DisplayManager.INSTANCE.displays.filterIsInstance<VanillaDisplayData>()

fun vanillaDisplay(displayId: UUID): VanillaDisplayData? = DisplayManager.getDisplayData(displayId) as? VanillaDisplayData
