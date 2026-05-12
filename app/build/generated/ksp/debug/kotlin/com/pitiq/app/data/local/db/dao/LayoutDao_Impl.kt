package com.pitiq.app.`data`.local.db.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.EntityUpsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.pitiq.app.`data`.local.db.entity.LayoutEntity
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class LayoutDao_Impl(
  __db: RoomDatabase,
) : LayoutDao {
  private val __db: RoomDatabase

  private val __upsertAdapterOfLayoutEntity: EntityUpsertAdapter<LayoutEntity>
  init {
    this.__db = __db
    this.__upsertAdapterOfLayoutEntity = EntityUpsertAdapter<LayoutEntity>(object :
        EntityInsertAdapter<LayoutEntity>() {
      protected override fun createQuery(): String =
          "INSERT INTO `layout_cache` (`id`,`name`,`slotCount`,`frameAssetUrl`,`previewUrl`,`textFieldsJson`,`version`,`isActive`,`sortOrder`) VALUES (?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: LayoutEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindLong(3, entity.slotCount.toLong())
        statement.bindText(4, entity.frameAssetUrl)
        statement.bindText(5, entity.previewUrl)
        statement.bindText(6, entity.textFieldsJson)
        statement.bindLong(7, entity.version.toLong())
        val _tmp: Int = if (entity.isActive) 1 else 0
        statement.bindLong(8, _tmp.toLong())
        statement.bindLong(9, entity.sortOrder.toLong())
      }
    }, object : EntityDeleteOrUpdateAdapter<LayoutEntity>() {
      protected override fun createQuery(): String =
          "UPDATE `layout_cache` SET `id` = ?,`name` = ?,`slotCount` = ?,`frameAssetUrl` = ?,`previewUrl` = ?,`textFieldsJson` = ?,`version` = ?,`isActive` = ?,`sortOrder` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: LayoutEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindLong(3, entity.slotCount.toLong())
        statement.bindText(4, entity.frameAssetUrl)
        statement.bindText(5, entity.previewUrl)
        statement.bindText(6, entity.textFieldsJson)
        statement.bindLong(7, entity.version.toLong())
        val _tmp: Int = if (entity.isActive) 1 else 0
        statement.bindLong(8, _tmp.toLong())
        statement.bindLong(9, entity.sortOrder.toLong())
        statement.bindText(10, entity.id)
      }
    })
  }

  public override suspend fun upsertAll(layouts: List<LayoutEntity>): Unit = performSuspending(__db,
      false, true) { _connection ->
    __upsertAdapterOfLayoutEntity.upsert(_connection, layouts)
  }

  public override fun getActiveLayouts(): Flow<List<LayoutEntity>> {
    val _sql: String = "SELECT * FROM layout_cache WHERE isActive = 1 ORDER BY sortOrder ASC"
    return createFlow(__db, false, arrayOf("layout_cache")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfSlotCount: Int = getColumnIndexOrThrow(_stmt, "slotCount")
        val _columnIndexOfFrameAssetUrl: Int = getColumnIndexOrThrow(_stmt, "frameAssetUrl")
        val _columnIndexOfPreviewUrl: Int = getColumnIndexOrThrow(_stmt, "previewUrl")
        val _columnIndexOfTextFieldsJson: Int = getColumnIndexOrThrow(_stmt, "textFieldsJson")
        val _columnIndexOfVersion: Int = getColumnIndexOrThrow(_stmt, "version")
        val _columnIndexOfIsActive: Int = getColumnIndexOrThrow(_stmt, "isActive")
        val _columnIndexOfSortOrder: Int = getColumnIndexOrThrow(_stmt, "sortOrder")
        val _result: MutableList<LayoutEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: LayoutEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpSlotCount: Int
          _tmpSlotCount = _stmt.getLong(_columnIndexOfSlotCount).toInt()
          val _tmpFrameAssetUrl: String
          _tmpFrameAssetUrl = _stmt.getText(_columnIndexOfFrameAssetUrl)
          val _tmpPreviewUrl: String
          _tmpPreviewUrl = _stmt.getText(_columnIndexOfPreviewUrl)
          val _tmpTextFieldsJson: String
          _tmpTextFieldsJson = _stmt.getText(_columnIndexOfTextFieldsJson)
          val _tmpVersion: Int
          _tmpVersion = _stmt.getLong(_columnIndexOfVersion).toInt()
          val _tmpIsActive: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsActive).toInt()
          _tmpIsActive = _tmp != 0
          val _tmpSortOrder: Int
          _tmpSortOrder = _stmt.getLong(_columnIndexOfSortOrder).toInt()
          _item =
              LayoutEntity(_tmpId,_tmpName,_tmpSlotCount,_tmpFrameAssetUrl,_tmpPreviewUrl,_tmpTextFieldsJson,_tmpVersion,_tmpIsActive,_tmpSortOrder)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAll(): List<LayoutEntity> {
    val _sql: String = "SELECT * FROM layout_cache"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfSlotCount: Int = getColumnIndexOrThrow(_stmt, "slotCount")
        val _columnIndexOfFrameAssetUrl: Int = getColumnIndexOrThrow(_stmt, "frameAssetUrl")
        val _columnIndexOfPreviewUrl: Int = getColumnIndexOrThrow(_stmt, "previewUrl")
        val _columnIndexOfTextFieldsJson: Int = getColumnIndexOrThrow(_stmt, "textFieldsJson")
        val _columnIndexOfVersion: Int = getColumnIndexOrThrow(_stmt, "version")
        val _columnIndexOfIsActive: Int = getColumnIndexOrThrow(_stmt, "isActive")
        val _columnIndexOfSortOrder: Int = getColumnIndexOrThrow(_stmt, "sortOrder")
        val _result: MutableList<LayoutEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: LayoutEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpSlotCount: Int
          _tmpSlotCount = _stmt.getLong(_columnIndexOfSlotCount).toInt()
          val _tmpFrameAssetUrl: String
          _tmpFrameAssetUrl = _stmt.getText(_columnIndexOfFrameAssetUrl)
          val _tmpPreviewUrl: String
          _tmpPreviewUrl = _stmt.getText(_columnIndexOfPreviewUrl)
          val _tmpTextFieldsJson: String
          _tmpTextFieldsJson = _stmt.getText(_columnIndexOfTextFieldsJson)
          val _tmpVersion: Int
          _tmpVersion = _stmt.getLong(_columnIndexOfVersion).toInt()
          val _tmpIsActive: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsActive).toInt()
          _tmpIsActive = _tmp != 0
          val _tmpSortOrder: Int
          _tmpSortOrder = _stmt.getLong(_columnIndexOfSortOrder).toInt()
          _item =
              LayoutEntity(_tmpId,_tmpName,_tmpSlotCount,_tmpFrameAssetUrl,_tmpPreviewUrl,_tmpTextFieldsJson,_tmpVersion,_tmpIsActive,_tmpSortOrder)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteById(id: String) {
    val _sql: String = "DELETE FROM layout_cache WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
