package com.pitiq.app.`data`.local.db.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.pitiq.app.`data`.local.db.entity.SessionEntity
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
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
public class UploadQueueDao_Impl(
  __db: RoomDatabase,
) : UploadQueueDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfSessionEntity: EntityInsertAdapter<SessionEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfSessionEntity = object : EntityInsertAdapter<SessionEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `upload_queue` (`sessionId`,`locationId`,`coinsInserted`,`thermalImagePath`,`colorImagePath`,`gifPath`,`uploadStatus`,`uploadAttemptedAt`,`errorLog`,`createdAt`) VALUES (?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: SessionEntity) {
        statement.bindText(1, entity.sessionId)
        statement.bindText(2, entity.locationId)
        statement.bindLong(3, entity.coinsInserted.toLong())
        val _tmpThermalImagePath: String? = entity.thermalImagePath
        if (_tmpThermalImagePath == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpThermalImagePath)
        }
        val _tmpColorImagePath: String? = entity.colorImagePath
        if (_tmpColorImagePath == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpColorImagePath)
        }
        val _tmpGifPath: String? = entity.gifPath
        if (_tmpGifPath == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpGifPath)
        }
        statement.bindText(7, entity.uploadStatus)
        val _tmpUploadAttemptedAt: Long? = entity.uploadAttemptedAt
        if (_tmpUploadAttemptedAt == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmpUploadAttemptedAt)
        }
        statement.bindText(9, entity.errorLog)
        statement.bindLong(10, entity.createdAt)
      }
    }
  }

  public override suspend fun enqueue(session: SessionEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfSessionEntity.insert(_connection, session)
  }

  public override fun observePending(): Flow<List<SessionEntity>> {
    val _sql: String =
        "SELECT * FROM upload_queue WHERE uploadStatus = 'pending' ORDER BY createdAt ASC"
    return createFlow(__db, false, arrayOf("upload_queue")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfSessionId: Int = getColumnIndexOrThrow(_stmt, "sessionId")
        val _columnIndexOfLocationId: Int = getColumnIndexOrThrow(_stmt, "locationId")
        val _columnIndexOfCoinsInserted: Int = getColumnIndexOrThrow(_stmt, "coinsInserted")
        val _columnIndexOfThermalImagePath: Int = getColumnIndexOrThrow(_stmt, "thermalImagePath")
        val _columnIndexOfColorImagePath: Int = getColumnIndexOrThrow(_stmt, "colorImagePath")
        val _columnIndexOfGifPath: Int = getColumnIndexOrThrow(_stmt, "gifPath")
        val _columnIndexOfUploadStatus: Int = getColumnIndexOrThrow(_stmt, "uploadStatus")
        val _columnIndexOfUploadAttemptedAt: Int = getColumnIndexOrThrow(_stmt, "uploadAttemptedAt")
        val _columnIndexOfErrorLog: Int = getColumnIndexOrThrow(_stmt, "errorLog")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<SessionEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: SessionEntity
          val _tmpSessionId: String
          _tmpSessionId = _stmt.getText(_columnIndexOfSessionId)
          val _tmpLocationId: String
          _tmpLocationId = _stmt.getText(_columnIndexOfLocationId)
          val _tmpCoinsInserted: Int
          _tmpCoinsInserted = _stmt.getLong(_columnIndexOfCoinsInserted).toInt()
          val _tmpThermalImagePath: String?
          if (_stmt.isNull(_columnIndexOfThermalImagePath)) {
            _tmpThermalImagePath = null
          } else {
            _tmpThermalImagePath = _stmt.getText(_columnIndexOfThermalImagePath)
          }
          val _tmpColorImagePath: String?
          if (_stmt.isNull(_columnIndexOfColorImagePath)) {
            _tmpColorImagePath = null
          } else {
            _tmpColorImagePath = _stmt.getText(_columnIndexOfColorImagePath)
          }
          val _tmpGifPath: String?
          if (_stmt.isNull(_columnIndexOfGifPath)) {
            _tmpGifPath = null
          } else {
            _tmpGifPath = _stmt.getText(_columnIndexOfGifPath)
          }
          val _tmpUploadStatus: String
          _tmpUploadStatus = _stmt.getText(_columnIndexOfUploadStatus)
          val _tmpUploadAttemptedAt: Long?
          if (_stmt.isNull(_columnIndexOfUploadAttemptedAt)) {
            _tmpUploadAttemptedAt = null
          } else {
            _tmpUploadAttemptedAt = _stmt.getLong(_columnIndexOfUploadAttemptedAt)
          }
          val _tmpErrorLog: String
          _tmpErrorLog = _stmt.getText(_columnIndexOfErrorLog)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              SessionEntity(_tmpSessionId,_tmpLocationId,_tmpCoinsInserted,_tmpThermalImagePath,_tmpColorImagePath,_tmpGifPath,_tmpUploadStatus,_tmpUploadAttemptedAt,_tmpErrorLog,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getPending(): List<SessionEntity> {
    val _sql: String =
        "SELECT * FROM upload_queue WHERE uploadStatus = 'pending' ORDER BY createdAt ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfSessionId: Int = getColumnIndexOrThrow(_stmt, "sessionId")
        val _columnIndexOfLocationId: Int = getColumnIndexOrThrow(_stmt, "locationId")
        val _columnIndexOfCoinsInserted: Int = getColumnIndexOrThrow(_stmt, "coinsInserted")
        val _columnIndexOfThermalImagePath: Int = getColumnIndexOrThrow(_stmt, "thermalImagePath")
        val _columnIndexOfColorImagePath: Int = getColumnIndexOrThrow(_stmt, "colorImagePath")
        val _columnIndexOfGifPath: Int = getColumnIndexOrThrow(_stmt, "gifPath")
        val _columnIndexOfUploadStatus: Int = getColumnIndexOrThrow(_stmt, "uploadStatus")
        val _columnIndexOfUploadAttemptedAt: Int = getColumnIndexOrThrow(_stmt, "uploadAttemptedAt")
        val _columnIndexOfErrorLog: Int = getColumnIndexOrThrow(_stmt, "errorLog")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<SessionEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: SessionEntity
          val _tmpSessionId: String
          _tmpSessionId = _stmt.getText(_columnIndexOfSessionId)
          val _tmpLocationId: String
          _tmpLocationId = _stmt.getText(_columnIndexOfLocationId)
          val _tmpCoinsInserted: Int
          _tmpCoinsInserted = _stmt.getLong(_columnIndexOfCoinsInserted).toInt()
          val _tmpThermalImagePath: String?
          if (_stmt.isNull(_columnIndexOfThermalImagePath)) {
            _tmpThermalImagePath = null
          } else {
            _tmpThermalImagePath = _stmt.getText(_columnIndexOfThermalImagePath)
          }
          val _tmpColorImagePath: String?
          if (_stmt.isNull(_columnIndexOfColorImagePath)) {
            _tmpColorImagePath = null
          } else {
            _tmpColorImagePath = _stmt.getText(_columnIndexOfColorImagePath)
          }
          val _tmpGifPath: String?
          if (_stmt.isNull(_columnIndexOfGifPath)) {
            _tmpGifPath = null
          } else {
            _tmpGifPath = _stmt.getText(_columnIndexOfGifPath)
          }
          val _tmpUploadStatus: String
          _tmpUploadStatus = _stmt.getText(_columnIndexOfUploadStatus)
          val _tmpUploadAttemptedAt: Long?
          if (_stmt.isNull(_columnIndexOfUploadAttemptedAt)) {
            _tmpUploadAttemptedAt = null
          } else {
            _tmpUploadAttemptedAt = _stmt.getLong(_columnIndexOfUploadAttemptedAt)
          }
          val _tmpErrorLog: String
          _tmpErrorLog = _stmt.getText(_columnIndexOfErrorLog)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              SessionEntity(_tmpSessionId,_tmpLocationId,_tmpCoinsInserted,_tmpThermalImagePath,_tmpColorImagePath,_tmpGifPath,_tmpUploadStatus,_tmpUploadAttemptedAt,_tmpErrorLog,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateStatus(
    sessionId: String,
    status: String,
    attemptedAt: Long,
  ) {
    val _sql: String =
        "UPDATE upload_queue SET uploadStatus = ?, uploadAttemptedAt = ? WHERE sessionId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        _argIndex = 2
        _stmt.bindLong(_argIndex, attemptedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, sessionId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun delete(sessionId: String) {
    val _sql: String = "DELETE FROM upload_queue WHERE sessionId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, sessionId)
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
