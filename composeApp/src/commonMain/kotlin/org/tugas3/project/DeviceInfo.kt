package org.tugas3.project

interface DeviceInfo {
    val model: String
    val osVersion: String
    val platform: String
    val manufacturer: String
}

expect fun getDeviceInfo(): DeviceInfo
