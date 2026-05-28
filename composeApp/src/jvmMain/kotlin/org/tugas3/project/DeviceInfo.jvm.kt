package org.tugas3.project

class JvmDeviceInfo : DeviceInfo {
    override val model: String = System.getProperty("os.arch") ?: "Unknown"
    override val osVersion: String = System.getProperty("os.version") ?: "Unknown"
    override val platform: String = System.getProperty("os.name") ?: "JVM"
    override val manufacturer: String = System.getProperty("java.vendor") ?: "Unknown"
}

actual fun getDeviceInfo(): DeviceInfo = JvmDeviceInfo()
