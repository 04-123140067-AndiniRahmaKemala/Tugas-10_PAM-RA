package org.tugas3.project

import kotlinx.coroutines.flow.Flow

interface NetworkMonitor {
    val isConnected: Flow<Boolean>
}

expect fun getNetworkMonitor(context: Any? = null): NetworkMonitor
