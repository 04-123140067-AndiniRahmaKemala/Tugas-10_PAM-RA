package org.tugas3.project

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform