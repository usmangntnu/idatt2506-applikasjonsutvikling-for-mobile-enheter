package com.example.oving2_b

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Hovedaktiviteten for Oppgave 2 (Oving2_b).
 * Denne aktiviteten presenterer en enkel regneapp hvor brukeren kan øve på
 * addisjon og multiplikasjon. Tallene til oppgavene hentes dynamisk fra
 * en ekstern applikasjon (Oppgave 1 - Oving_2a) via implisitte Intents.
 *
 * @see R.layout.activity_main Layout for denne aktiviteten (Oppgave 2b).
 * @see R.string Strenger brukt i denne aktiviteten (Oppgave 2a).
 */
class MainActivity : AppCompatActivity() {

    // View-referanser til UI-elementer definert i activity_main.xml (Oppgave 2b)
    private lateinit var number1TextView: TextView
    private lateinit var number2TextView: TextView
    private lateinit var answerEditText: EditText
    private lateinit var upperLimitEditText: EditText

    /**
     * Konstanter for kommunikasjon med Prosjekt 1 (Oving_2a - tallgeneratoren).
     * Disse må samsvare nøyaktig med konstantene definert i Prosjekt 1.
     * Dette er en del av "kontrakten" for inter-app kommunikasjon (Oppgave 2d).
     */
    companion object {
        /** Nøkkel for Intent extra som spesifiserer øvre grense sendt TIL Prosjekt 1. */
        const val EXTRA_LIMIT_PROSJEKT1 = "limit"
        /** Nøkkel for Intent extra som inneholder det genererte tallet mottatt FRA Prosjekt 1. */
        const val EXTRA_RANDOM_NUMBER_PROSJEKT1 = "randomNumber"
        /** Action-streng for å starte Prosjekt 1 sin MainActivity via et implisitt Intent. */
        const val ACTION_GET_NUMBER_PROSJEKT1 = "com.example.oving_2a.ACTION_GET_NUMBER"
    }

    /** Hjelpevariabel for å vite om vi henter det første eller det andre tallet fra Prosjekt 1. */
    private var isFetchingFirstNumber = true
    /** Hjelpevariabel for å forhindre samtidige, overlappende kall for å hente tall. */
    private var fetchingNumbersInProgress = false

    /**
     * ActivityResultLauncher for å starte Prosjekt 1 sin tallgenereringsaktivitet
     * og motta resultatet (det tilfeldige tallet).
     * Dette er den moderne måten å håndtere `startActivityForResult` på. (Oppgave 2d)
     */
    private lateinit var numberFetcherLauncher: ActivityResultLauncher<Intent>

    /**
     * Kalles når aktiviteten først opprettes.
     * Initialiserer UI, setter opp ActivityResultLauncher, og starter henting av de første tallene.
     * @param savedInstanceState Hvis aktiviteten gjenopprettes etter å ha blitt avsluttet,
     * inneholder denne Bundle-en dataen den sist leverte i onSaveInstanceState(Bundle).
     * Ellers er den null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // For kant-til-kant UI (moderne Android)
        setContentView(R.layout.activity_main) // Setter layouten (Oppgave 2b)

        // Håndterer system-insets (status bar, navigation bar) for kant-til-kant UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialiserer View-referanser fra layouten (Oppgave 2b)
        number1TextView = findViewById(R.id.number1TextView)
        number2TextView = findViewById(R.id.number2TextView)
        answerEditText = findViewById(R.id.answerEditText)
        upperLimitEditText = findViewById(R.id.upperLimitEditText)

        // Oppgave 2d: Setter opp ActivityResultLauncher for å motta tall fra Prosjekt 1.
        // Lambda-funksjonen her blir kalt når Prosjekt 1 sin aktivitet returnerer et resultat.
        numberFetcherLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            fetchingNumbersInProgress = false // Henting (forsøk) er fullført

            if (result.resultCode == Activity.RESULT_OK) { // Sjekker om operasjonen i Prosjekt 1 var vellykket
                val data: Intent? = result.data // Intent som inneholder resultatet
                // Henter det tilfeldige tallet fra Intent-data, bruker -1 som default hvis ikke funnet.
                val randomNumber = data?.getIntExtra(EXTRA_RANDOM_NUMBER_PROSJEKT1, -1) ?: -1

                if (randomNumber != -1) { // Sjekker om vi faktisk fikk et gyldig tall
                    if (isFetchingFirstNumber) {
                        // Hvis dette var det første tallet vi ba om:
                        number1TextView.text = randomNumber.toString()
                        isFetchingFirstNumber = false // Nå skal vi hente det andre tallet
                        fetchSingleRandomNumberFromProject1() // Start henting av det andre tallet
                    } else {
                        // Hvis dette var det andre tallet vi ba om:
                        number2TextView.text = randomNumber.toString()
                        answerEditText.text.clear() // Tøm svarfeltet for ny oppgave
                        // Begge tallene er nå hentet og vist.
                    }
                } else {
                    // Hvis randomNumber var -1 (feil ved uthenting av extra)
                    handleFetchError(getString(R.string.error_fetching_number))
                }
            } else {
                // Hvis Prosjekt 1 returnerte en annen resultCode enn RESULT_OK (f.eks. RESULT_CANCELED)
                handleFetchError(getString(R.string.error_fetching_number_detailed, result.resultCode))
            }
        }

        // Oppgave 2d: Starter prosessen med å hente de to første tallene når aktiviteten lages.
        initiateNewNumberGenerationFromProject1()
    }

    /**
     * Håndterer og viser feilmeldinger relatert til henting av tall fra Prosjekt 1.
     * Oppdaterer UI for å reflektere feiltilstanden.
     * @param message Den spesifikke feilmeldingen som skal vises i en Toast.
     */
    private fun handleFetchError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        // Indikerer feil i TextViews for å gi brukeren visuell feedback.
        if (isFetchingFirstNumber) {
            number1TextView.text = "X" // Indikerer feil for første tall
            number2TextView.text = ""
        } else {
            number2TextView.text = "X" // Indikerer feil for andre tall
        }
        fetchingNumbersInProgress = false // Tillater at brukeren kan prøve å generere nye tall igjen.
    }

    /**
     * Oppgave 2d: Starter prosessen med å generere (hente) to nye tilfeldige tall.
     * Denne metoden kalles ved oppstart og etter hver besvarte oppgave.
     * Den sørger for at UI oppdateres til en "laster"-tilstand og starter
     * hentingen av det første tallet.
     */
    private fun initiateNewNumberGenerationFromProject1() {
        // Hvis en henting allerede pågår, ikke start en ny for å unngå race conditions.
        if (fetchingNumbersInProgress) {
            Toast.makeText(this, getString(R.string.wait_for_numbers), Toast.LENGTH_SHORT).show()
            return
        }

        fetchingNumbersInProgress = true // Marker at en henteprosess har startet.
        isFetchingFirstNumber = true   // Vi begynner med å hente det første tallet.

        // Oppdater UI for å vise at tall genereres (Oppgave 2b, tilpasset for 2d)
        number1TextView.text = getString(R.string.generating_numbers)
        number2TextView.text = "" // Tøm feltet for det andre tallet
        answerEditText.text.clear() // Tøm brukerens forrige svar

        // Start henting av det første tilfeldige tallet fra Prosjekt 1.
        fetchSingleRandomNumberFromProject1()
    }

    /**
     * Oppgave 2d: Forbereder og sender et implisitt Intent for å hente ETT
     * tilfeldig tall fra tallgenereringsappen (Prosjekt 1).
     * Verdien fra `upperLimitEditText` sendes med som en `extra` i Intent-et.
     */
    private fun fetchSingleRandomNumberFromProject1() {
        val upperLimitString = upperLimitEditText.text.toString()
        // Konverter øvre grense til Int, bruk 10 som default hvis ugyldig input.
        val upperLimit = upperLimitString.toIntOrNull() ?: 10
        // Sikrer at øvre grense er minst 1, da Random.nextInt(0,0) eller negativ ville feile.
        val safeUpperLimit = if (upperLimit > 0) upperLimit else 1

        // Lag et Intent med den definerte ACTION-strengen for Prosjekt 1.
        val intentToGetNumber = Intent(ACTION_GET_NUMBER_PROSJEKT1)
        // Legg til øvre grense som en extra, med nøkkelen Prosjekt 1 forventer.
        intentToGetNumber.putExtra(EXTRA_LIMIT_PROSJEKT1, safeUpperLimit)

        try {
            // Start Prosjekt 1 sin aktivitet og forvent et resultat via `numberFetcherLauncher`.
            numberFetcherLauncher.launch(intentToGetNumber)
        } catch (e: ActivityNotFoundException) {
            // Dette skjer hvis ingen app (spesifikt Prosjekt 1 sin app) er installert
            // som kan håndtere `ACTION_GET_NUMBER_PROSJEKT1`.
            handleFetchError(getString(R.string.error_oving2a_not_installed))
        }
    }

    /**
     * Oppgave 2c: onClick-handler for "Adder"-knappen.
     * Kalles når brukeren trykker på knappen (definert via `android:onClick` i XML).
     * @param view Knappen som ble trykket.
     */
    fun onAddClick(view: View) {
        checkAnswer(Operation.ADD)
    }

    /**
     * Oppgave 2c: onClick-handler for "Multipliser"-knappen.
     * Kalles når brukeren trykker på knappen (definert via `android:onClick` i XML).
     * @param view Knappen som ble trykket.
     */
    fun onMultiplyClick(view: View) {
        checkAnswer(Operation.MULTIPLY)
    }

    /**
     * Oppgave 2c: Sjekker brukerens svar mot det korrekte svaret for den valgte operasjonen.
     * Viser en Toast-melding som indikerer om svaret var riktig eller galt.
     * Henter verdier fra UI-elementer, utfører beregningen, og sammenligner.
     *
     * Oppgave 2d: Etter at svaret er sjekket, kalles
     * `initiateNewNumberGenerationFromProject1()` for å forberede neste oppgave
     * med nye, tilfeldige tall fra Prosjekt 1.
     *
     * @param operation Typen matematisk operasjon som skal utføres ([Operation.ADD] eller [Operation.MULTIPLY]).
     */
    private fun checkAnswer(operation: Operation) {
        // Ikke gjør noe hvis vi fortsatt venter på at tall skal genereres.
        if (fetchingNumbersInProgress) {
            Toast.makeText(this, getString(R.string.wait_for_numbers), Toast.LENGTH_SHORT).show()
            return
        }

        // Hent tekst-representasjonen av tallene og brukerens svar (Oppgave 2c).
        val num1String = number1TextView.text.toString()
        val num2String = number2TextView.text.toString()
        val userAnswerString = answerEditText.text.toString()

        // Sjekk om tallene faktisk er lastet og ikke er i en "laster"- eller "feil"-tilstand.
        if (num1String.isEmpty() || num1String == getString(R.string.generating_numbers) || num1String == "X" ||
            num2String.isEmpty() || num2String == "X") {
            Toast.makeText(this, getString(R.string.wait_for_numbers), Toast.LENGTH_SHORT).show()
            return
        }

        // Konverter strengene til Int. Returnerer null hvis konvertering feiler.
        val num1 = num1String.toIntOrNull()
        val num2 = num2String.toIntOrNull()
        val userAnswer = userAnswerString.toIntOrNull()

        // Valider at tallene i oppgaven er gyldige heltall.
        if (num1 == null || num2 == null) {
            Toast.makeText(this, getString(R.string.invalid_numbers_in_task), Toast.LENGTH_SHORT).show()
            // Ikke generer nye tall her, da det kan være et problem med selve hentingen/visningen.
            return
        }

        // Valider at brukerens svar er et gyldig heltall.
        if (userAnswer == null) {
            Toast.makeText(this, getString(R.string.enter_answer), Toast.LENGTH_SHORT).show()
            return
        }

        // Utfør den matematiske operasjonen.
        val correctAnswer = when (operation) {
            Operation.ADD -> num1 + num2
            Operation.MULTIPLY -> num1 * num2
        }

        // Sammenlign brukerens svar med det korrekte svaret og vis passende Toast (Oppgave 2c).
        // Bruker getString(R.string...) for å hente lokaliserte strenger (Oppgave 2a).
        if (userAnswer == correctAnswer) {
            Toast.makeText(this, getString(R.string.correct), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, getString(R.string.wrong, correctAnswer), Toast.LENGTH_LONG).show()
        }

        // Oppgave 2d: Etter at svaret er sjekket, start prosessen med å hente nye tall
        // fra Prosjekt 1 for neste oppgave.
        initiateNewNumberGenerationFromProject1()
    }

    /**
     * Enum for å representere de matematiske operasjonene som støttes.
     * Gjør koden i `checkAnswer` mer lesbar og robust.
     */
    private enum class Operation {
        ADD, MULTIPLY
    }
}

