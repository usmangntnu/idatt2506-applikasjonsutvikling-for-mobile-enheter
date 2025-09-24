package com.example.oving5

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import android.widget.EditText
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Et [Fragment] som håndterer brukerregistrering for tallspillet.
 *
 * Dette fragmentet lar brukeren oppgi navn og kortnummer. Ved trykk på "Start nytt spill"-knappen
 * sendes denne informasjonen til en web-tjener via en HTTP POST-forespørsel.
 * Tjenerens respons vises i et [TextView]. Hvis registreringen er vellykket og tjeneren
 * indikerer at spillet kan starte (typisk med en melding som "Oppgi et tall mellom X og Y"),
 * navigerer fragmentet brukeren til [GameFragment]. Navn og kortnummer sendes med
 * som argumenter til [GameFragment].
 *
 * Nettverkskall utføres asynkront ved hjelp av Kotlin Coroutines innenfor [lifecycleScope].
 * UI-oppdateringer skjer på hovedtråden ([Dispatchers.Main]).
 *
 * @see GameFragment
 * @see HttpWrapper
 */
class RegistrationFragment : Fragment(R.layout.fragment_registration) {
    /**
     * Instans av [HttpWrapper] for å håndtere nettverkskommunikasjon med tjeneren.
     * URL-en til tjeneren er hardkodet her.
     */
    private val network = HttpWrapper("https://bigdata.idi.ntnu.no/mobil/tallspill.jsp")

    // UI-elementer - initialiseres i onViewCreated
    private lateinit var etName: EditText
    private lateinit var etCard: EditText
    private lateinit var btnStart: Button
    private lateinit var tvMessage: TextView

    /**
     * Kalles når fragmentets view har blitt opprettet.
     *
     * Her initialiseres referanser til UI-elementene definert i `fragment_registration.xml`.
     * En [View.OnClickListener] settes på `btnStart` for å håndtere brukerens forsøk på å
     * registrere seg og starte spillet.
     *
     * @param view Viewet som ble returnert av [onCreateView].
     * @param savedInstanceState Hvis fragmentet re-opprettes fra en tidligere lagret tilstand,
     *                           er dette den lagrede tilstanden.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialiser UI-referanser
        etName = view.findViewById(R.id.etName)
        etCard = view.findViewById(R.id.etCard)
        btnStart = view.findViewById(R.id.btnStart)
        tvMessage = view.findViewById(R.id.tvMessage)

        btnStart.setOnClickListener {
            val name = etName.text.toString().trim() // Fjern eventuelle mellomrom før/etter
            val card = etCard.text.toString().trim()

            // Enkel validering for å unngå tomme felt (kan utvides)
            if (name.isEmpty() || card.isEmpty()) {
                tvMessage.text = "Navn og kortnummer kan ikke være tomme."
                Log.w("RegistrationFragment", "Registreringsforsøk med tomme felt.")
                return@setOnClickListener
            }

            Log.d("RegistrationFragment", "Start-knapp trykket. Navn: '$name', Kort: '$card'")

            // Start en coroutine innenfor fragmentets livssyklus for å håndtere nettverkskallet.
            // lifecycleScope kansellerer automatisk coroutinen hvis fragmentet ødelegges.
            lifecycleScope.launch {
                val params = mapOf("navn" to name, "kortnummer" to card)
                Log.d("RegistrationFragment", "Sender POST-request til tjener med params: $params")

                val response: String = try {
                    // Utfør nettverkskallet på IO-tråden (håndtert av HttpWrapper)
                    network.post(params)
                } catch (e: Exception) {
                    Log.e("RegistrationFragment", "Nettverksfeil under registrering", e)
                    // Gi en brukervennlig feilmelding i UI
                    "Feil ved nettverkskommunikasjon: ${e.message?.take(100) ?: "Ukjent feil"}"
                }

                Log.d("RegistrationFragment", "Mottatt respons fra tjener: $response")

                // Oppdater UI på hovedtråden (Main)
                withContext(Dispatchers.Main) {
                    tvMessage.text = response

                    // Sjekk om responsen indikerer at spillet kan starte.
                    // Tjeneren skal returnere "Oppgi et tall mellom X og Y!"
                    val startGameRegex = Regex("Oppgi et tall mellom\\s+(\\d+)\\s+og\\s+(\\d+)", RegexOption.IGNORE_CASE)
                    if (startGameRegex.containsMatchIn(response)) {
                        Log.i("RegistrationFragment", "Registrering vellykket. Navigerer til GameFragment.")
                        // Naviger til GameFragment.
                        // Navn og kort sendes med slik at GameFragment kan bruke dem om nødvendig
                        // (selv om tjeneren husker dem via cookies, kan det være nyttig for UI eller logging).
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, GameFragment.newInstance(name, card))
                            .addToBackStack(null) // Gjør det mulig å gå tilbake til dette fragmentet
                            .commit()
                    } else {
                        // Tjeneren returnerte ikke den forventede startmeldingen.
                        // Dette kan være en feilmelding som "Feil, du må registrer navn og kortnummer..."
                        // eller en annen uventet respons.
                        Log.w("RegistrationFragment", "Registrering ikke vellykket eller ukjent spillstart-respons: $response")
                    }
                }
            }
        }
    }
}
