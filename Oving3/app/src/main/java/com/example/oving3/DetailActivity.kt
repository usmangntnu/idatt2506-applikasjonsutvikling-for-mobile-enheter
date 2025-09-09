package com.example.oving3

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * Aktivitet for å vise og redigere detaljene (navn og fødselsdato) for en enkelt venn.
 *
 * **Samspill:**
 * - Bruker `activity_detail.xml` for sitt layout.
 * - Startes av [MainActivity] med Intent extras ([EXTRA_INDEX], [EXTRA_NAME], [EXTRA_BIRTHDATE]).
 * - Viser mottatt navn og fødselsdato i [EditText]-felt.
 * - Ved "Lagre"-klikk: Sender oppdatert navn, fødselsdato og opprinnelig indeks tilbake til [MainActivity] med `setResult(Activity.RESULT_OK, intent)`.
 * - Ved "Avbryt"-klikk: Returnerer `Activity.RESULT_CANCELED` til [MainActivity].
 */
class DetailActivity : AppCompatActivity() {

    // UI-komponenter definert i activity_detail.xml
    private lateinit var editDetailName: EditText
    private lateinit var editDetailBirthdate: EditText
    private lateinit var buttonSave: Button
    private lateinit var buttonCancel: Button

    /** Indeksen til vennen som redigeres, mottatt fra MainActivity. Viktig for å identifisere hvilken venn som skal oppdateres. */
    private var friendIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Initialiser UI-komponenter.
        editDetailName = findViewById(R.id.editDetailName)
        editDetailBirthdate = findViewById(R.id.editDetailBirthdate)
        buttonSave = findViewById(R.id.buttonSave)
        buttonCancel = findViewById(R.id.buttonCancel)

        // Hent data sendt fra MainActivity.
        friendIndex = intent.getIntExtra(EXTRA_INDEX, -1) // -1 som default hvis ikke funnet.
        val currentName = intent.getStringExtra(EXTRA_NAME) ?: ""
        val currentBirthdate = intent.getStringExtra(EXTRA_BIRTHDATE) ?: ""

        // Fyll ut inputfeltene med mottatt data.
        editDetailName.setText(currentName)
        editDetailBirthdate.setText(currentBirthdate)

        // Lytter for klikk på "Avbryt"-knappen.
        // Setter resultatet til CANCELED og lukker aktiviteten. Ingen data returneres.
        buttonCancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish() // Lukker DetailActivity og returnerer til MainActivity.
        }

        // Lytter for klikk på "Lagre"-knappen.
        // Samler inn endret data, pakker det i et Intent, setter resultatet til OK, og lukker aktiviteten.
        buttonSave.setOnClickListener {
            val updatedName = editDetailName.text.toString().trim()
            val updatedBirthdate = editDetailBirthdate.text.toString().trim()

            if (updatedName.isEmpty()) {
                Toast.makeText(this, "Navn kan ikke være tomt", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Opprett et nytt Intent for å returnere data.
            val resultIntent = Intent().apply {
                putExtra(EXTRA_INDEX, friendIndex) // Send tilbake indeksen slik at MainActivity vet hvilken venn som ble endret.
                putExtra(EXTRA_NAME, updatedName)
                putExtra(EXTRA_BIRTHDATE, updatedBirthdate)
            }
            // Sett resultatet til OK og inkluder data-Intentet.
            setResult(Activity.RESULT_OK, resultIntent)
            finish() // Lukker DetailActivity og returnerer til MainActivity.
        }
    }
}
