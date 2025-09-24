package com.example.oving5

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import android.widget.TextView
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Et [Fragment] som håndterer selve tallspillet.
 * Brukeren kan tippe et tall. Tipset sendes til en web-tjener som validerer det.
 * Fragmentet viser respons fra tjeneren (f.eks. om tallet er for høyt, for lavt, eller korrekt).
 *
 * @property navn Navnet på spilleren, mottatt fra [RegistrationFragment].
 * @property kort Kortnummeret til spilleren, mottatt fra [RegistrationFragment].
 */
class GameFragment : Fragment(R.layout.fragment_game) {
    // URL til tjeneren som håndterer tallspillet.
    private val network = HttpWrapper("https://bigdata.idi.ntnu.no/mobil/tallspill.jsp")

    // UI-elementer
    private lateinit var etNumber: EditText
    private lateinit var btnTip: Button
    private lateinit var tvResponse: TextView

    // Spillerinformasjon mottatt via arguments
    private var navn: String? = null
    private var kort: String? = null

    /**
     * Kalles for å gjøre initiell opprettelse av fragmentet.
     * Henter argumenter (navn og kort) som ble sendt med da fragmentet ble opprettet.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            navn = it.getString(ARG_NAVN)
            kort = it.getString(ARG_KORT)
            Log.d("GameFragment", "Fragment opprettet med Navn: $navn, Kort: $kort")
        }
    }
    /**
     * Kalles umiddelbart etter at [Fragment.onCreateView] har returnert, men før noen lagret
     * tilstand er gjenopprettet i viewet.
     * Initialiserer UI-elementer og setter opp en OnClickListener for tipp-knappen.
     *
     * @param view Viewet returnert av [Fragment.onCreateView].
     * @param savedInstanceState Hvis ikke-null, blir dette fragmentet rekonstruert fra en
     *                           tidligere lagret tilstand som gitt her.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etNumber = view.findViewById(R.id.etNumber)
        btnTip = view.findViewById(R.id.btnTip)
        tvResponse = view.findViewById(R.id.tvResponse)

        // Hent den initielle meldingen fra tjeneren (f.eks. "Oppgi et tall mellom X og Y")
        // Dette kan hentes fra argumentene, eller ved et initiellt GET-kall hvis nødvendig.
        // For enkelhets skyld antar vi at RegistrationFragment har sendt en melding som kan vises,
        // eller vi kan sette en generell melding her.
        // Hvis tjeneren gir instruksjoner direkte etter registrering, kan den vises her.
        // For eksempel: tvResponse.text = arguments?.getString("initialMessageFromServer") ?: "Klar til å tippe!"


        btnTip.setOnClickListener {
            val tall = etNumber.text.toString().trim()
            if (tall.isEmpty()) {
                tvResponse.text = "Du må skrive inn et tall!"
                return@setOnClickListener
            }

            Log.d("GameFragment", "Tipp-knapp trykket. Tall: $tall. Spiller: Navn=$navn, Kort=$kort")

            // Start en coroutine innenfor fragmentets livssyklus for å håndtere nettverkskallet.
            lifecycleScope.launch {
                // Parameteren "tall" er spesifisert i oppgaveteksten.
                // Navn og kortnummer sendes ikke eksplisitt her, da tjeneren skal huske dem via cookies
                // som ble satt under registreringen (håndtert av HttpWrapper).
                val params = mapOf("tall" to tall)
                Log.d("GameFragment", "Sender GET-request til tjener med params: $params")

                val response: String = try {
                    // Utfør nettverkskallet på IO-tråden (håndtert av HttpWrapper)
                    // Ifølge oppgaveteksten kan tipping gjøres med GET: "...endre innholdet i adressefeltet til å sende parameteren tall=5..."
                    network.get(params)
                } catch (e: Exception) {
                    Log.e("GameFragment", "Nettverksfeil under tipping", e)
                    "Feil ved nettverkskommunikasjon: ${e.message}"
                }

                Log.d("GameFragment", "Mottatt respons fra tjener: $response")

                // Oppdater UI på hovedtråden (Main)
                withContext(Dispatchers.Main) {
                    tvResponse.text = response

                    // Sjekk om brukeren har vunnet eller tapt (brukt opp forsøk)
                    // Oppgaveteksten sier: "<Navn> du har vunnet <beløp> som kommer inn på ditt kort <kortnummer>"
                    // Eller en melding om at man har brukt opp antall sjanser.
                    if (response.contains("du har vunnet", ignoreCase = true) ||
                        response.contains("brukt opp dine 3 forsøk", ignoreCase = true) || // Antatt feilmelding
                        response.contains("Feil, du må registrer navn og kortnummer", ignoreCase = true) // Hvis session er tapt
                    ) {
                        Log.d("GameFragment", "Spillet er over. Viser dialog.")
                        showGameEndDialog(response)
                    }
                }
            }
        }
    }

    /**
     * Viser en dialogboks når spillet er over (enten vunnet eller tapt).
     * Gir brukeren mulighet til å starte et nytt spill, som fjerner alle fragmenter
     * fra backstacken og effektivt returnerer til [RegistrationFragment] (siden MainActivity
     * vil laste den på nytt hvis backstacken er tom).
     *
     * @param message Meldingen som skal vises i dialogen (f.eks. vinnermeldingen).
     */
    private fun showGameEndDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(if (message.contains("du har vunnet", ignoreCase = true)) "Gratulerer!" else "Spillet er over")
            .setMessage(message)
            .setPositiveButton("Start Nytt Spill") { dialog, _ ->
                Log.d("GameFragment", "Bruker valgte 'Start Nytt Spill'. Tømmer backstack.")
                // Fjerner alle fragmenter fra backstacken. Dette vil føre til at
                // MainActivity (hvis den er satt opp til det) laster RegistrationFragment på nytt.
                parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                dialog.dismiss()
            }
            .setCancelable(false) // Forhindrer at dialogen lukkes ved trykk utenfor
            .show()
    }

    companion object {
        private const val ARG_NAVN = "navn"
        private const val ARG_KORT = "kort"

        /**
         * Statisk factory-metode for å lage en ny instans av [GameFragment]
         * med nødvendige argumenter (navn og kort).
         *
         * @param name Spillerens navn.
         * @param card Spillerens kortnummer.
         * @return En ny instans av GameFragment.
         */
        @JvmStatic
        fun newInstance(name: String, card: String) =
            GameFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_NAVN, name)
                    putString(ARG_KORT, card)
                }
            }
    }
}
