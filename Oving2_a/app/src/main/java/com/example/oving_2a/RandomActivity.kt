package com.example.oving_2a

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

/**
 * En test-applikasjon som implementerer oppgave e.
 *
 * Oppgave e: Denne aktiviteten tester funksjonaliteten definert i oppgave b, c, og d
 * ved å starte [MainActivity] for å hente et tilfeldig tall.
 * Verdien som kommer tilbake (via ActivityResultLauncher som erstatter onActivityResult)
 * blir lagt inn i et [TextView].
 *
 * Aktiviteten inneholder en knapp som, når trykket, starter [MainActivity]
 * ved hjelp av et implisitt Intent med action [ACTION_GET_RANDOM_NUMBER].
 */
class RandomActivity : AppCompatActivity() {

    private lateinit var textViewResultDisplay: TextView
    private lateinit var buttonGetRandomNumber: Button

    /**
     * Action-streng brukt for å starte [MainActivity] via et implisitt Intent.
     * Dette er en del av mekanismen som gjør at [MainActivity] kan "tas i bruk av andre aktiviteter"
     * (ref. oppgave d). Må samsvare med action-strengen definert i
     * [MainActivity]s intent-filter i `AndroidManifest.xml`.
     */
    internal val ACTION_GET_RANDOM_NUMBER = "com.example.oving_2a.ACTION_GET_NUMBER"
    // Gjort 'internal' hvis den kun brukes innenfor denne modulen, ellers 'public' om nødvendig.
    // Eller behold 'private' hvis MainActivity definerer sin egen konstant for dette og det er den som
    // er den "offisielle" action-strengen. For øyeblikket er den her for klarhet i RandomActivity.

    /**
     * ActivityResultLauncher for å håndtere resultatet fra [MainActivity] (ref. oppgave e).
     * Oppdaterer [textViewResultDisplay] med det mottatte tilfeldige tallet
     * eller en feilmelding hvis operasjonen ikke var vellykket.
     */
    private val numberGeneratorLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                // Bruker MainActivity.EXTRA_RANDOM_NUMBER for å hente resultatet (ref. oppgave c)
                val randomNumber = data?.getIntExtra(MainActivity.EXTRA_RANDOM_NUMBER, -1)
                textViewResultDisplay.text = "Tilfeldig tall fra MainActivity: $randomNumber"
            } else {
                textViewResultDisplay.text =
                    "MainActivity returnerte ikke OK (Resultat: ${result.resultCode})"
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Oppgave e: Bruk findViewById() for TextView og Button.
        textViewResultDisplay = findViewById(R.id.textViewResultDisplay)
        buttonGetRandomNumber = findViewById(R.id.buttonGetRandomNumber)

        buttonGetRandomNumber.setOnClickListener {
            val intent = Intent(ACTION_GET_RANDOM_NUMBER)
            // Sender med øvre grense som i oppgave b, testet med 75 her.
            intent.putExtra(MainActivity.EXTRA_LIMIT, 75)
            numberGeneratorLauncher.launch(intent)
        }
    }
}
