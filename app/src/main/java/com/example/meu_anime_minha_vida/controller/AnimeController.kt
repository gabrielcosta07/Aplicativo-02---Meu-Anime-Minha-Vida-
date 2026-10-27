package com.example.meu_anime_minha_vida.controller

import android.content.Context
import com.example.meu_anime_minha_vida.dao.AnimeDAO
import com.example.meu_anime_minha_vida.dao.NOTA_DESTAQUE
import com.example.meu_anime_minha_vida.model.Anime

class AnimeController(context: Context) {
    private val dao = AnimeDAO(context)

    fun validarTitulo(titulo: String): String {
        val texto = titulo.trim()
        if (texto.length == 0) {
            return "Informe o título"
        } else if (texto.length > 120) {
            return "O título pode ter no máximo 120 caracteres"
        }
        return ""
    }

    fun validarEpisodios(episodios: String): String {
        val numero = episodios.trim().toIntOrNull()
        if (numero == null || numero < 0) {
            return "Informe um número inteiro de episódios, igual ou maior que zero"
        }
        return ""
    }

    fun validarOpiniao(opiniao: String): String {
        val texto = opiniao.trim()
        if (texto.length == 0) {
            return "Escreva sua opinião"
        } else if (texto.length > 2000) {
            return "A opinião pode ter no máximo 2000 caracteres"
        }
        return ""
    }

    fun validarRecomendacao(recomendacao: Int): String {
        if (recomendacao < 1 || recomendacao > 5) {
            return "Escolha de 1 a 5 estrelas"
        }
        return ""
    }

    fun salvar(id: Long, titulo: String, episodios: String, opiniao: String, recomendacao: Int): Boolean {
        val anime = Anime(id, titulo.trim(), episodios.trim().toInt(), opiniao.trim(), recomendacao)
        if (id == -1L) {
            return dao.inserir(anime) != -1L
        } else {
            return dao.atualizar(anime)
        }
    }

    fun excluir(id: Long): Boolean {
        return dao.excluir(id)
    }

    fun buscarPorId(id: Long): Anime? {
        return dao.buscarPorId(id)
    }

    fun listar(busca: String, somenteDestaques: Boolean, ordem: Int): ArrayList<Anime> {
        return dao.listar(busca.trim(), somenteDestaques, ordem)
    }

    fun ehDestaque(anime: Anime): Boolean {
        return anime.recomendacao >= NOTA_DESTAQUE
    }

    fun estrelas(nota: Int): String = when (nota) {
        1 -> "★☆☆☆☆"
        2 -> "★★☆☆☆"
        3 -> "★★★☆☆"
        4 -> "★★★★☆"
        5 -> "★★★★★"
        else -> "☆☆☆☆☆"
    }

    fun textoDoItem(anime: Anime): String {
        var opiniao = anime.opiniao
        if (opiniao.length > 80) {
            opiniao = opiniao.substring(0, 80) + "..."
        }

        var avaliacao = "${estrelas(anime.recomendacao)} (${anime.recomendacao}/5)"
        if (ehDestaque(anime)) {
            avaliacao = "$avaliacao · Destaque"
        }

        return "${anime.titulo}\nEpisódios disponíveis: ${anime.episodios}\n$avaliacao\nOpinião: $opiniao"
    }
}
