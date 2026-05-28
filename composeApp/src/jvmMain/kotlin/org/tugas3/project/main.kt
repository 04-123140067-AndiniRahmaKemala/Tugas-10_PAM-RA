package org.tugas3.project

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.tugas3.project.di.initKoin
import org.tugas3.project.data.DatabaseDriverFactory
import org.tugas3.project.data.JvmDatabaseDriverFactory
import org.koin.dsl.module

fun main() {
    initKoin {
        modules(module {
            single<DatabaseDriverFactory> { JvmDatabaseDriverFactory() }
            single<Any?> { null }
        })
    }
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "TugasIndividu3",
        ) {
            App()
        }
    }
}
