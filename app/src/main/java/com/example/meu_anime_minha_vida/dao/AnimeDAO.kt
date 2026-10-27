package com.example.meu_anime_minha_vida.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.meu_anime_minha_vida.database.DBHelper
import com.example.meu_anime_minha_vida.model.Anime

class AnimeDAO(context: Context) {
    private val dbHelper = DBHelper(context)

    fun inserir(anime: Anime): Long {
        val db = dbHelper.writableDatabase
        val id = db.insert("animes", null, criarValores(anime))
        db.close()
        return id
    }

    fun atualizar(anime: Anime): Boolean {
        val db = dbHelper.writableDatabase
        val linhas = db.update("animes", criarValores(anime), "id = ?", arrayOf(anime.id.toString()))
        db.close()
        return linhas == 1
    }

    fun excluir(id: Long): Boolean {
        val db = dbHelper.writableDatabase
        val linhas = db.delete("animes", "id = ?", arrayOf(id.toString()))
        db.close()
        return linhas == 1
    }

    fun buscarPorId(id: Long): Anime? {
        val db = dbHelper.readableDatabase
        val cursor = db.query("animes", null, "id = ?", arrayOf(id.toString()), null, null, null)
        var anime: Anime? = null
        if (cursor.moveToFirst()) {
            anime = lerAnime(cursor)
        }
        cursor.close()
        db.close()
        return anime
    }

    fun listar(busca: String, somenteDestaques: Boolean, ordem: Int): ArrayList<Anime> {
        var selecao: String? = null
        var argumentos: Array<String>? = null

        if (busca != "" && somenteDestaques) {
            selecao = "titulo LIKE ? AND recomendacao >= ?"
            argumentos = arrayOf("%$busca%", NOTA_DESTAQUE.toString())
        } else if (busca != "") {
            selecao = "titulo LIKE ?"
            argumentos = arrayOf("%$busca%")
        } else if (somenteDestaques) {
            selecao = "recomendacao >= ?"
            argumentos = arrayOf(NOTA_DESTAQUE.toString())
        }

        val db = dbHelper.readableDatabase
        val cursor = db.query("animes", null, selecao, argumentos, null, null, ordenacao(ordem))
        val animes = ArrayList<Anime>()
        while (cursor.moveToNext()) {
            animes.add(lerAnime(cursor))
        }
        cursor.close()
        db.close()
        return animes
    }

    private fun ordenacao(ordem: Int): String = when (ordem) {
        1 -> "episodios ASC, titulo COLLATE NOCASE ASC, id ASC"
        2 -> "episodios DESC, titulo COLLATE NOCASE ASC, id ASC"
        3 -> "recomendacao ASC, titulo COLLATE NOCASE ASC, id ASC"
        4 -> "recomendacao DESC, titulo COLLATE NOCASE ASC, id ASC"
        else -> "titulo COLLATE NOCASE ASC, id ASC"
    }

    private fun criarValores(anime: Anime): ContentValues {
        val valores = ContentValues()
        valores.put("titulo", anime.titulo)
        valores.put("episodios", anime.episodios)
        valores.put("opiniao", anime.opiniao)
        valores.put("recomendacao", anime.recomendacao)
        return valores
    }

    private fun lerAnime(cursor: Cursor): Anime {
        return Anime(
            cursor.getLong(cursor.getColumnIndexOrThrow("id")),
            cursor.getString(cursor.getColumnIndexOrThrow("titulo")),
            cursor.getInt(cursor.getColumnIndexOrThrow("episodios")),
            cursor.getString(cursor.getColumnIndexOrThrow("opiniao")),
            cursor.getInt(cursor.getColumnIndexOrThrow("recomendacao"))
        )
    }
}

const val NOTA_DESTAQUE = 4
