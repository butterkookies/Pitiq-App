package com.pitiq.app.`data`.local.db

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.pitiq.app.`data`.local.db.dao.UploadQueueDao
import com.pitiq.app.`data`.local.db.dao.UploadQueueDao_Impl
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class PitiqDatabase_Impl : PitiqDatabase() {
  private val _uploadQueueDao: Lazy<UploadQueueDao> = lazy {
    UploadQueueDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1,
        "554e068203039384a4254a54fa129858", "20080d45f060fa4e3b33d18d1f8a6cdc") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `upload_queue` (`sessionId` TEXT NOT NULL, `locationId` TEXT NOT NULL, `coinsInserted` INTEGER NOT NULL, `thermalImagePath` TEXT, `colorImagePath` TEXT, `gifPath` TEXT, `uploadStatus` TEXT NOT NULL, `uploadAttemptedAt` INTEGER, `errorLog` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`sessionId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '554e068203039384a4254a54fa129858')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `upload_queue`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsUploadQueue: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsUploadQueue.put("sessionId", TableInfo.Column("sessionId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUploadQueue.put("locationId", TableInfo.Column("locationId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUploadQueue.put("coinsInserted", TableInfo.Column("coinsInserted", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUploadQueue.put("thermalImagePath", TableInfo.Column("thermalImagePath", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUploadQueue.put("colorImagePath", TableInfo.Column("colorImagePath", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUploadQueue.put("gifPath", TableInfo.Column("gifPath", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUploadQueue.put("uploadStatus", TableInfo.Column("uploadStatus", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUploadQueue.put("uploadAttemptedAt", TableInfo.Column("uploadAttemptedAt",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUploadQueue.put("errorLog", TableInfo.Column("errorLog", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUploadQueue.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysUploadQueue: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesUploadQueue: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoUploadQueue: TableInfo = TableInfo("upload_queue", _columnsUploadQueue,
            _foreignKeysUploadQueue, _indicesUploadQueue)
        val _existingUploadQueue: TableInfo = read(connection, "upload_queue")
        if (!_infoUploadQueue.equals(_existingUploadQueue)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |upload_queue(com.pitiq.app.data.local.db.entity.SessionEntity).
              | Expected:
              |""".trimMargin() + _infoUploadQueue + """
              |
              | Found:
              |""".trimMargin() + _existingUploadQueue)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "upload_queue")
  }

  public override fun clearAllTables() {
    super.performClear(false, "upload_queue")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(UploadQueueDao::class, UploadQueueDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun uploadQueueDao(): UploadQueueDao = _uploadQueueDao.value
}
