package ir.atriatech.pizzawifi.common.dagger.base_app.database

import android.net.Uri
import androidx.room.TypeConverter


class UriTypeConverter {
    @TypeConverter
    fun toString(value: String?): Uri? {
        return if (value == null) null else Uri.parse(value)
    }

    @TypeConverter
    fun toLong(value: Uri?): String? {
        return (value?.toString())
    }
}