package com.example.oving3

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.oving3.adapter.FriendGridAdapter
import com.example.oving3.model.Friend

// Konstanter for å sende data mellom MainActivity og DetailActivity via Intent.
// Dette sikrer konsistens i nøkkelnavn.
const val EXTRA_INDEX = "com.example.oving3.EXTRA_INDEX"
const val EXTRA_NAME = "com.example.oving3.EXTRA_NAME"
const val EXTRA_BIRTHDATE = "com.example.oving3.EXTRA_BIRTHDATE"

/**
 * Hovedaktiviteten i applikasjonen.
 * Lar brukeren registrere nye venner, vise listen over registrerte venner,
 * og initiere endring av en eksisterende venns opplysninger.
 *
 * **Samspill:**
 * - Bruker `activity_main.xml` for sitt layout.
 * - Holder en liste av [Friend]-objekter i minnet (`friends`).
 * - Bruker [FriendGridAdapter] for å vise `friends` i en `GridView`.
 * - Bruker en `ArrayAdapter<String>` for å vise venners navn i en `Spinner`.
 * - Starter [DetailActivity] for å la brukeren redigere en valgt venn.
 * - Mottar resultater fra [DetailActivity] via [ActivityResultLauncher] (`editFriendLauncher`) for å oppdatere en venns informasjon.
 */
class MainActivity : AppCompatActivity() {

    // UI-komponenter definert i activity_main.xml
    private lateinit var editName: EditText
    private lateinit var editBirthdate: EditText
    private lateinit var buttonAdd: Button
    private lateinit var spinner: Spinner
    private lateinit var gridView: GridView

    /** Listen som holder alle registrerte venner. Lagres kun i minnet. */
    private val friends: MutableList<Friend> = mutableListOf()

    /** Adapter for GridView som viser detaljer om hver venn. */
    private lateinit var gridAdapter: FriendGridAdapter

    /** Adapter for Spinner som kun viser navnene på vennene. */
    private lateinit var spinnerAdapter: ArrayAdapter<String>

    /**
     * ActivityResultLauncher for å motta oppdatert venninformasjon tilbake fra [DetailActivity].
     * Dette er den moderne måten å håndtere `startActivityForResult` på.
     */
    private lateinit var editFriendLauncher: ActivityResultLauncher<Intent>

    /**
     * Hjelpeflagg for å unngå at [Spinner.onItemSelectedListener] trigger
     * umiddelbart ved initialisering av spinneren, før brukeren har gjort et aktivt valg.
     */
    private var spinnerInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialiser UI-komponenter ved å koble dem til deres respektive views i layoutet.
        editName = findViewById(R.id.editName)
        editBirthdate = findViewById(R.id.editBirthdate)
        buttonAdd = findViewById(R.id.buttonAdd)
        spinner = findViewById(R.id.spinnerFriends)
        gridView = findViewById(R.id.gridViewFriends)

        // Sett opp adapter for GridView.
        // FriendGridAdapter er ansvarlig for å konvertere Friend-objekter til Views.
        gridAdapter = FriendGridAdapter(this, friends)
        gridView.adapter = gridAdapter

        // Sett opp adapter for Spinner.
        // Bruker en standard ArrayAdapter for å vise en liste av strenger (vennenavn).
        spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mutableListOf<String>())
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        // Initialiser ActivityResultLauncher for å håndtere resultatet fra DetailActivity.
        editFriendLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data // Intent som returneres fra DetailActivity
                if (data != null) {
                    val index = data.getIntExtra(EXTRA_INDEX, -1)
                    val name = data.getStringExtra(EXTRA_NAME)
                    val birth = data.getStringExtra(EXTRA_BIRTHDATE)

                    // Valider at data er mottatt korrekt og at indeksen er gyldig.
                    if (index != -1 && name != null && birth != null && index < friends.size) {
                        // Oppdater den eksisterende vennen i listen.
                        friends[index].name = name
                        friends[index].birthdate = birth
                        // Oppdater begge adapterne for å reflektere endringene i UI.
                        refreshAdapters()
                        Toast.makeText(this, "Oppdatert: $name", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Lytter for klikk på "Legg til"-knappen.
        // Oppretter et nytt Friend-objekt og legger det til listen.
        buttonAdd.setOnClickListener {
            val name = editName.text.toString().trim()
            val birth = editBirthdate.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(this, "Navn kan ikke være tomt", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            friends.add(Friend(name, birth))
            // Oppdater UI for å vise den nye vennen.
            refreshAdapters()
            // Tøm inputfeltene for neste registrering.
            editName.text.clear()
            editBirthdate.text.clear()
            Toast.makeText(this, "$name lagt til", Toast.LENGTH_SHORT).show()
        }

        // Lytter for klikk på et item i GridView.
        // Åpner DetailActivity for å endre den valgte vennen.
        // Oppfyller kravet: "Komponenten med adapter skal ha en lytter som gjør det mulig å endre på vedkommende registering."
        gridView.setOnItemClickListener { _, _, position, _ ->
            openDetailActivityFor(position)
        }

        // Lytter for valg i Spinner.
        // Åpner også DetailActivity for å endre den valgte vennen.
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                // Ignorer den første automatiske triggeren når spinneren initialiseres.
                if (!spinnerInitialized) {
                    spinnerInitialized = true
                    return
                }
                // Sørg for at posisjonen er gyldig før DetailActivity åpnes.
                if (position >= 0 && position < friends.size) {
                    openDetailActivityFor(position)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Ingen handling nødvendig her for denne appen.
            }
        }
    }

    /**
     * Åpner [DetailActivity] for å redigere en venn på en gitt posisjon i `friends`-listen.
     * Sender nødvendig informasjon (indeks, nåværende navn og fødselsdato) til [DetailActivity] via Intent extras.
     *
     * @param index Indeksen til vennen i `friends`-listen som skal redigeres.
     */
    private fun openDetailActivityFor(index: Int) {
        val friendToEdit = friends[index]
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra(EXTRA_INDEX, index)
            putExtra(EXTRA_NAME, friendToEdit.name)
            putExtra(EXTRA_BIRTHDATE, friendToEdit.birthdate)
        }
        // Starter DetailActivity og forventer et resultat tilbake via editFriendLauncher.
        editFriendLauncher.launch(intent)
    }

    /**
     * Oppdaterer både `gridAdapter` (for GridView) og `spinnerAdapter` (for Spinner)
     * for å reflektere den nåværende tilstanden til `friends`-listen.
     * Kalles etter at en venn er lagt til eller endret.
     */
    private fun refreshAdapters() {
        // Oppdater spinnerAdapter med den nye listen av navn.
        spinnerAdapter.clear()
        spinnerAdapter.addAll(friends.map { it.name })
        // spinnerAdapter.notifyDataSetChanged() // Ikke strengt nødvendig etter clear() og addAll() for ArrayAdapter

        // Informer gridAdapter om at datagrunnlaget har endret seg, slik at GridView tegnes på nytt.
        gridAdapter.notifyDataSetChanged()
    }
}

