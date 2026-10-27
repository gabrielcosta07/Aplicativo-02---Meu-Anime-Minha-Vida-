package com.example.meu_anime_minha_vida.controller

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.meu_anime_minha_vida.R
import com.example.meu_anime_minha_vida.model.Anime

class MainActivity : AppCompatActivity() {
    private lateinit var controller: AnimeController
    private lateinit var animes: ArrayList<Anime>

    private lateinit var searchEditText: EditText
    private lateinit var allRadioButton: RadioButton
    private lateinit var highlightsRadioButton: RadioButton
    private lateinit var orderSpinner: Spinner
    private lateinit var resultTextView: TextView
    private lateinit var animesListView: ListView

    private var appliedSearch: String = ""
    private var appliedOnlyHighlights: Boolean = false
    private var appliedOrder: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        controller = AnimeController(this)

        searchEditText = findViewById<EditText>(R.id.searchEditText)
        allRadioButton = findViewById<RadioButton>(R.id.allRadioButton)
        highlightsRadioButton = findViewById<RadioButton>(R.id.highlightsRadioButton)
        orderSpinner = findViewById<Spinner>(R.id.orderSpinner)
        resultTextView = findViewById<TextView>(R.id.resultTextView)
        animesListView = findViewById<ListView>(R.id.animesListView)

        val orders = arrayOf(
            "Título: A–Z",
            "Episódios: menor para maior",
            "Episódios: maior para menor",
            "Recomendação: menor para maior",
            "Recomendação: maior para menor"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, orders)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        orderSpinner.adapter = adapter

        animesListView.setOnItemClickListener { adapterView, view, position, id ->
            val intent = Intent(this, AnimeFormActivity::class.java)
            intent.putExtra("anime_id", animes[position].id)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadList()
    }

    fun add(view: View) {
        val intent = Intent(this, AnimeFormActivity::class.java)
        startActivity(intent)
    }

    fun search(view: View) {
        appliedSearch = searchEditText.text.toString()
        appliedOnlyHighlights = highlightsRadioButton.isChecked
        appliedOrder = orderSpinner.selectedItemPosition
        loadList()
    }

    fun listAll(view: View) {
        searchEditText.setText("")
        allRadioButton.isChecked = true
        appliedSearch = ""
        appliedOnlyHighlights = false
        appliedOrder = orderSpinner.selectedItemPosition
        loadList()
    }

    private fun loadList() {
        animes = controller.listar(appliedSearch, appliedOnlyHighlights, appliedOrder)
        val texts = animes.map { controller.textoDoItem(it) }
        animesListView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, texts)

        if (animes.size == 0) {
            resultTextView.text = "Nenhum anime encontrado."
        } else {
            resultTextView.text = "${animes.size} anime(s) na lista"
        }
    }
}
