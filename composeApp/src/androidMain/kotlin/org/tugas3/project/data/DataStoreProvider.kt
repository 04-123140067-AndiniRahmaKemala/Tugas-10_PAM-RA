@file:JvmName("DataStoreProviderAndroid")
package org.tugas3.project.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import java.io.File

actual fun createDataStore(context: Any?): DataStore<Preferences> {
    require(context is Context)
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = { File(context.filesDir, DATASTORE_FILE_NAME).absolutePath.toPath() }
    )
}
