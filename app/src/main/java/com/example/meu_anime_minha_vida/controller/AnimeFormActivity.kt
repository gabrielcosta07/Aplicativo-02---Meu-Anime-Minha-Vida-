package com.example.meu_anime_minha_vida.controller

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.meu_anime_minha_vida.R

class AnimeFormActivity : AppCompatActivity() {
    private lateinit var controller: AnimeController

    private lateinit var titleEditText: EditText
    private lateinit var episodesEditText: EditText
    private lateinit var opinionEditText: EditText
    private lateinit var starsSpinner: Spinner

    private var animeId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_anime_form)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        controller = AnimeController(this)

        titleEditText = findViewById<EditText>(R.id.titleEditText)
        episodesEditText = findViewById<EditText>(R.id.episodesEditText)
        opinionEditText = findViewById<EditText>(R.id.opinionEditText)
        starsSpinner = findViewById<Spinner>(R.id.starsSpinner)
        val formTitleTextView = findViewById<TextView>(R.id.formTitleTextView)
        val deleteButton = findViewById<Button>(R.id.deleteButton)

        val stars = arrayOf(
            "Escolha as estrelas",
            "★☆☆☆☆ (1)",
            "★★☆☆☆ (2)",
            "★★★☆☆ (3)",
            "★★★★☆ (4)",
            "★★★★★ (5)"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, stars)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        starsSpinner.adapter = adapter

        findViewById<Button>(R.id.cancelButton).setOnClickListener {
            finish()
        }

        animeId = intent.getLongExtra("anime_id", -1L)

        if (animeId == -1L) {
            formTitleTextView.text = "Novo anime"
            deleteButton.visibility = View.GONE
        } else {
            formTitleTextView.text = "Editar anime"
            val anime = controller.buscarPorId(animeId)

            if (anime == null) {
                Toast.makeText(this, "Anime não encontrado", Toast.LENGTH_SHORT).show()
                finish()
            } else if (savedInstanceState == null) {
                titleEditText.setText(anime.titulo)
                episodesEditText.setText("${anime.episodios}")
                opinionEditText.setText(anime.opiniao)
                starsSpinner.setSelection(anime.recomendacao)
            }
        }
    }

    fun save(view: View) {
        val titulo = titleEditText.text.toString()
        val episodios = episodesEditText.text.toString()
        val opiniao = opinionEditText.text.toString()
        val recomendacao = starsSpinner.selectedItemPosition

        val erroTitulo = controller.validarTitulo(titulo)
        val erroEpisodios = controller.validarEpisodios(episodios)
        val erroOpiniao = controller.validarOpiniao(opiniao)
        val erroRecomendacao = controller.validarRecomendacao(recomendacao)

        if (erroTitulo != "") {
            showError(erroTitulo, titleEditText)
        } else if (erroEpisodios != "") {
            showError(erroEpisodios, episodesEditText)
        } else if (erroOpiniao != "") {
            showError(erroOpiniao, opinionEditText)
        } else if (erroRecomendacao != "") {
            Toast.makeText(this, erroRecomendacao, Toast.LENGTH_SHORT).show()
        } else if (controller.salvar(animeId, titulo, episodios, opiniao, recomendacao)) {
            Toast.makeText(this, "Anime salvo!", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Erro ao salvar no banco de dados", Toast.LENGTH_SHORT).show()
        }
    }

    fun delete(view: View) {
        AlertDialog.Builder(this)
            .setMessage("Excluir este anime?")
            .setPositiveButton("Excluir") { dialog, which ->
                if (controller.excluir(animeId)) {
                    Toast.makeText(this, "Anime excluído", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Erro ao excluir o anime", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showError(message: String, field: EditText) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        field.requestFocus()
    }
}
