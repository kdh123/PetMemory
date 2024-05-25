package com.dohyun.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dohyun.data.room.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun roomBuilder(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, "pet_memory"
        ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7)
            .build()
    }

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE 'diary' ADD COLUMN 'title' TEXT NOT NULL default '제목 없음'")
        }
    }

    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE 'diary' ADD COLUMN 'lat' DOUBLE DEFAULT NULL")
            database.execSQL("ALTER TABLE 'diary' ADD COLUMN 'lng' DOUBLE DEFAULT NULL")
        }
    }

    private val MIGRATION_3_4 = object : Migration(3, 4) {
        val SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS 'pet' (" +
                "'petId' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL default 0, " +
                "'petBigType' INTEGER NOT NULL default 0, " +
                "'petType' TEXT NOT NULL default '', " +
                "'petName' TEXT NOT NULL default '', " +
                "'petAge' INTEGER NOT NULL default 0, " +
                "'petBirthDay' TEXT NOT NULL default '', " +
                "'petSinceDay' TEXT NOT NULL default '', " +
                "'petWeight' DOUBLE NOT NULL default 0.0, " +
                "'petSex' INTEGER NOT NULL default 0, " +
                "'petImageUrl' TEXT NOT NULL default '')"

        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(SQL_CREATE_TABLE)
        }
    }

    private val MIGRATION_4_5 = object : Migration(4, 5) {

        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE 'diary' ADD COLUMN 'pet' TEXT")
        }
    }

    private val MIGRATION_5_6 = object : Migration(5, 6) {
        val SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS 'schedule' (" +
                "'scheduleId' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL default 0, " +
                "'date' TEXT NOT NULL default '', " +
                "'title' TEXT NOT NULL default '', " +
                "'content' TEXT NOT NULL default '', " +
                "'petName' TEXT NOT NULL default '', " +
                "'petImageUrl' TEXT NOT NULL default '')"

        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(SQL_CREATE_TABLE)
        }
    }

    private val MIGRATION_6_7 = object : Migration(6, 7) {
        val SQL_DROP_TABLE = "drop table schedule"

        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(SQL_DROP_TABLE)
        }
    }
}