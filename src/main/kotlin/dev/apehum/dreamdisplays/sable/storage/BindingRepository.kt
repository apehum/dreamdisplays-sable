package dev.apehum.dreamdisplays.sable.storage

import dev.apehum.dreamdisplays.sable.binding.DisplayBinding

interface BindingRepository {
    suspend fun loadAll(): List<DisplayBinding>

    suspend fun save(bindings: Collection<DisplayBinding>)
}
