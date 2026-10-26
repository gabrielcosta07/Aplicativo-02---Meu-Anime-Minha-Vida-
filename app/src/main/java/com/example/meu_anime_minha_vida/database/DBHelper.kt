package com.example.meu_anime_minha_vida.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "animes.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE animes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "titulo TEXT NOT NULL, " +
                    "episodios INTEGER NOT NULL CHECK (episodios >= 0), " +
                    "opiniao TEXT NOT NULL, " +
                    "recomendacao INTEGER NOT NULL CHECK (recomendacao BETWEEN 1 AND 5))"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }
}
