package org.tugas3.project

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class IosNetworkMonitor : NetworkMonitor {
    // Simple implementation for iOS
    override val isConnected: Flow<Boolean> = flowOf(true)
}

actual fun getNetworkMonitor(context: Any?): NetworkMonitor = IosNetworkMonitor()
