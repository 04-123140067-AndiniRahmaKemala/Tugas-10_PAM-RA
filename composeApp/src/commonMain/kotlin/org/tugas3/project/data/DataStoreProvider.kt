package org.tugas3.project.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

expect fun createDataStore(context: Any? = null): DataStore<Preferences>

internal const val DATASTORE_FILE_NAME = "settings.preferences_pb"
