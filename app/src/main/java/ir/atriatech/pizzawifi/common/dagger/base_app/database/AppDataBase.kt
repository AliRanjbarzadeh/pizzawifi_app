package ir.atriatech.pizzawifi.common.dagger.base_app.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ir.atriatech.pizzawifi.entities.shopcart.ShopCartItem

@Database(entities = [ShopCartItem::class], version = 8, exportSchema = false)
@TypeConverters(UriTypeConverter::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun shopDao(): ShopDao
}