package org.tugas3.project

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level
import org.koin.dsl.module
import org.tugas3.project.data.AndroidDatabaseDriverFactory
import org.tugas3.project.data.DatabaseDriverFactory
import org.tugas3.project.di.initKoin

class AndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        initKoin {
            // Memberikan context Android ke Koin
            androidContext(this@AndroidApp)
            // Log error Koin (opsional)
            androidLogger(Level.ERROR)
            
            modules(module {
                single<DatabaseDriverFactory> {
                    AndroidDatabaseDriverFactory(this@AndroidApp) 
                }
                // Menyediakan context secara umum jika dibutuhkan oleh provider lain
                single<Any> { this@AndroidApp }
            })
        }
    }
}
