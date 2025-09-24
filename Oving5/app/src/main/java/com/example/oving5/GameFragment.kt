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
 * Viser en dialog når spillet er over, med mulighet for å starte på nytt.
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


        btnTip.setOnClickListener {
            val tall = etNumber.text.toString().trim()
            if (tall.isEmpty()) {
                tvResponse.text = "Du må skrive inn et tall!"
                return@setOnClickListener
            }

            Log.d("GameFragment", "Tipp-knapp trykket. Tall: $tall. Spiller: Navn=$navn, Kort=$kort")

            lifecycleScope.launch {
                val params = mapOf("tall" to tall)
                Log.d("GameFragment", "Sender GET-request til tjener med params: $params")

                val response: String = try {
                    network.get(params)
                } catch (e: Exception) {
                    Log.e("GameFragment", "Nettverksfeil under tipping", e)
                    "Feil ved nettverkskommunikasjon: ${e.message}"
                }

                Log.d("GameFragment", "Mottatt respons fra tjener: $response")

                withContext(Dispatchers.Main) {
                    tvResponse.text = response

                    val hasWon = response.contains("du har vunnet", ignoreCase = true)
                    val hasLostGame = response.contains("Beklager ingen flere sjanser, du må starte på nytt", ignoreCase = true)
                    val sessionErrorNeedsRegistration = response.contains("Feil, du må registrer navn og kortnummer", ignoreCase = true)
                    val cookieOrInitialParamError = response.contains("Du har glemt å støtte cookies, eller du har ikke oppgit parameterene navn og kortnummer", ignoreCase = true)

                    if (hasWon || hasLostGame || sessionErrorNeedsRegistration || cookieOrInitialParamError) {
                        Log.d("GameFragment", "Spillet er over. Viser dialog. Vunnet: $hasWon, Tapt: $hasLostGame, SesjonsfeilReg: $sessionErrorNeedsRegistration, CookieFeil: $cookieOrInitialParamError")
                        showGameEndDialog(response, hasWon)
                    }
                }
            }
        }
    }

    /**
     * Viser en dialogboks når spillet er over (enten vunnet, tapt, eller ved sesjonsfeil).
     * Gir brukeren mulighet til å starte et nytt spill, som navigerer tilbake til [RegistrationFragment].
     *
     * @param message Meldingen som skal vises i dialogen (f.eks. vinnermeldingen fra tjeneren).
     * @param playerWon True hvis spilleren har vunnet, false ellers. Brukes for å sette passende tittel.
     */
    private fun showGameEndDialog(message: String, playerWon: Boolean) {
        AlertDialog.Builder(requireContext())
            .setTitle(if (playerWon) "Gratulerer!" else "Spillet er over")
            .setMessage(message)
            .setPositiveButton("Start Nytt Spill") { dialog, _ ->
                Log.d("GameFragment", "Bruker valgte 'Start Nytt Spill'. Navigerer til RegistrationFragment.")

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, RegistrationFragment())
                    .commit()
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
