package amirlabs.sapasemua.base

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface DevDao<model : DbModel> : DbService {

    /**
     * For Reading data purposes, please define that in implemented class
     */

    /**
     * Method do save an object
     * @param data object to be saved
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: model)

    /**
     * Method to update an object
     * @param data object to be updated
     */
    @Update
    fun update(vararg data: model)

    /**
     * Method to remove an object
     * @param data object to remove
     */
    @Delete
    fun remove(vararg data: model)
}

open class DbModel

interface DbService