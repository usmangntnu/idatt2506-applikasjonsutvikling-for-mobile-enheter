package com.example.oving5

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

/**
 * Hovedaktiviteten for applikasjonen.
 * Ansvarlig for å sette opp det initielle brukergrensesnittet ved å laste [RegistrationFragment].
 * Fungerer som vert for fragmentene som utgjør appens ulike skjermbilder.
 */
class MainActivity : AppCompatActivity() {
    /**
     * Kalles når aktiviteten først opprettes.
     * Setter content view til layouten definert i `R.layout.activity_main`.
     * Hvis det ikke er noen lagret instans tilstand (f.eks. ved første oppstart, ikke ved rotasjon),
     * erstattes innholdet i `R.id.fragment_container` med en ny instans av [RegistrationFragment].
     *
     * @param savedInstanceState Hvis aktiviteten blir re-initialisert etter tidligere å ha blitt
     *                           lukket ned, inneholder denne Bundle data den sist leverte i
     *                           [onSaveInstanceState]. Ellers er den null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Setter layouten fra activity_main.xml

        // Kun last RegistrationFragment hvis appen starter helt på nytt (ikke f.eks. ved skjermrotasjon)
        // savedInstanceState er null ved første oppstart.
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RegistrationFragment()) // Bytt ut innholdet i FrameLayout
                .commit() // Utfør transaksjonen
        }
    }
}
