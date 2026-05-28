package org.tugas3.project

import platform.UIKit.UIDevice

class IosDeviceInfo : DeviceInfo {
    override val model: String = UIDevice.currentDevice.model
    override val osVersion: String = UIDevice.currentDevice.systemVersion
    override val platform: String = "iOS"
    override val manufacturer: String = "Apple"
}

actual fun getDeviceInfo(): DeviceInfo = IosDeviceInfo()
