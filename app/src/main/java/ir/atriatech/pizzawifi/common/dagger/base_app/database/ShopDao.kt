package ir.atriatech.pizzawifi.common.dagger.base_app.database

import androidx.lifecycle.LiveData
import androidx.room.*
import io.reactivex.Observable
import ir.atriatech.pizzawifi.entities.shopcart.ShopCartItem
import ir.atriatech.pizzawifi.entities.shopcart.ShopCartPrices

@Dao
interface ShopDao {
    //Get all in shop cart
    @Query("SELECT * FROM products ORDER BY id DESC")
    fun getAll(): MutableList<ShopCartItem>

    //Get all in shop cart where in
    @Query("SELECT * FROM products WHERE productID IN (:productIDs) AND materials IN (:productMaterials) ORDER BY id DESC")
    fun getAll(
        productIDs: MutableList<Int>,
        productMaterials: MutableList<String>
    ): MutableList<ShopCartItem>

    //Get all in shop cart
    @Query("SELECT * FROM products WHERE productFromMaker = 1 ORDER BY id DESC")
    fun getAllMaker(): LiveData<MutableList<ShopCartItem>>

    @Query("SELECT * FROM products WHERE productID = :productID AND type = :type AND materials = :materials AND pizza = 0")
    fun findByIdAndMaterial(productID: Int, type: Int, materials: String): LiveData<ShopCartItem>

    @Query("SELECT * FROM products WHERE productID = :productID AND type = :type AND materials = :materials AND pizza = 0")
    fun findByIdAndMaterialMainThread(productID: Int, type: Int, materials: String): ShopCartItem

    @Query("SELECT * FROM products WHERE productID = :productID AND materials = :materials AND pizza = 0")
    fun findByIdAndMaterialMainThreadNullable(productID: Int, materials: String): ShopCartItem?

    @Query("SELECT * FROM products WHERE productID = :productID AND pizza = 1")
    fun findPizzaByIdMainThread(productID: Int): ShopCartItem

    @Query("SELECT * FROM products WHERE productID = :productID AND pizza = 1")
    fun findPizzaByIdMainThreadNullable(productID: Int): ShopCartItem?

    @Query("SELECT * FROM products WHERE productID = :productID AND pizza = 1")
    fun findPizzaById(productID: Int): LiveData<ShopCartItem?>

    @Query("DELETE FROM products WHERE productID = :productID AND pizza = 1")
    fun deletePizzaById(productID: Int)

    //Count all
    @Query("SELECT COUNT(id) FROM products")
    fun countAll(): Observable<Int>

    //Count all main thread
    @Query("SELECT COUNT(id) FROM products")
    fun countAllMainThread(): Int

    //Count all
    @Query("SELECT COUNT(id) AS totalRows, SUM(price * qty) AS orderSum, SUM(discount * qty) AS totalDiscount FROM products")
    fun countAllAndPrices(): Observable<ShopCartPrices>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveItem(vararg shopCartItem: ShopCartItem): LongArray

    @Update
    fun updateItem(vararg shopCartItem: ShopCartItem)

    @Query("UPDATE products SET productFromMaker = 1 WHERE productID = :productID AND materials = :materials")
    fun setFromMakerByProductIDAndMaterial(productID: Int, materials: String)

    @Query("DELETE FROM products WHERE id = :ID")
    fun deleteByID(ID: Long)

    @Query("DELETE FROM products WHERE productID = :ID")
    fun deleteByProductID(ID: Int)

    @Query("DELETE FROM products WHERE productID = :productID AND materials = :materials")
    fun deleteByProductIDAndMaterial(productID: Int, materials: String)

    @Query("UPDATE products SET productFromMaker = 0 WHERE 1")
    fun destroyAllFromMakerProducts()

    @Delete
    fun deleteItem(vararg shopCartItem: ShopCartItem)

    @Query("DELETE FROM products")
    fun deleteAll()

    @Query("DELETE FROM SQLITE_SEQUENCE WHERE NAME = 'products'")
    fun resetTable()

    //Last item
    @Query("SELECT * FROM products WHERE branchId > 0 ORDER BY id DESC LIMIT 1")
    fun findLastItem(): ShopCartItem?

    //Update all rows branchId and branches
    @Query("UPDATE products SET branchId = :branchId, branchName = :branchName WHERE branchId = 0")
    fun updateAll(branchId: Int, branchName: String)

}