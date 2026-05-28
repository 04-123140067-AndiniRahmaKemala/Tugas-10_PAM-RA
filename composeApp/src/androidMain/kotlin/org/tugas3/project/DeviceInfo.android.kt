package org.tugas3.project

import android.os.Build

class AndroidDeviceInfo : DeviceInfo {
    override val model: String = Build.MODEL
    override val osVersion: String = Build.VERSION.RELEASE
    override val platform: String = "Android"
    override val manufacturer: String = Build.MANUFACTURER
}

actual fun getDeviceInfo(): DeviceInfo = AndroidDeviceInfo()
