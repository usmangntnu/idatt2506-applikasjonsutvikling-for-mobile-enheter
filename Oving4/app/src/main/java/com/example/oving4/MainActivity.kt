package com.example.oving4

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log // Anbefalt for feilsøking
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

/**
 * MainActivity fungerer som "mellommann" mellom ItemListFragment og ItemDetailFragment.
 *
 * - Holder nåværende index for hvilket element som vises.
 * - Implementerer ItemListFragment.Callback for å motta listeklikk.
 * - Håndterer menyvalg (Forrige / Neste).
 *
 * Merk: vi håndterer configChanges i manifest (orientation|screenSize), derfor får vi
 * onConfigurationChanged istedenfor full restart ved rotasjon.
 */
class MainActivity : AppCompatActivity(), ItemListFragment.Callback {

    /** Liste av titler hentet fra res/values/strings.xml */
    private lateinit var titles: Array<String>

    /** Nåværende element som vises (index i arrays) */
    private var currentIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Bruker activity_main.xml

        titles = resources.getStringArray(R.array.titles)

        // Initialiser fragmenter dersom de ikke allerede er lagt inn
        if (supportFragmentManager.findFragmentById(R.id.list_container) == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.list_container, ItemListFragment())
                .commit()
        }
        if (supportFragmentManager.findFragmentById(R.id.detail_container) == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.detail_container, ItemDetailFragment())
                .commit()
        }

        // Vis start-element
        // Det er lurt å sjekke om titles faktisk har elementer før du kaller showItem
        // for å unngå krasj hvis R.array.titles er tom.
        if (titles.isNotEmpty()) {
            showItem(currentIndex)
        }
    }

    /**
     * Callback fra ItemListFragment når et element i lista velges.
     * Aktiviteten mottar posisjon og videreformidler visningen til ItemDetailFragment.
     */
    override fun onItemSelected(position: Int) {
        // Sikrer at posisjonen er gyldig for listen
        if (position >= 0 && position < titles.size) {
            currentIndex = position
            showItem(currentIndex)
        }
    }

    /**
     * Viser element på posisjon i ItemDetailFragment.
     */
    private fun showItem(index: Int) {
        // Sikrer at indeksen er gyldig
        if (this::titles.isInitialized && index >= 0 && index < titles.size) { // Lagt til sjekk for titles.isInitialized
            val detailFragment = supportFragmentManager.findFragmentById(R.id.detail_container)
            if (detailFragment is ItemDetailFragment) {
                Log.d("MainActivity", "showItem - Showing item at index: $index") // For feilsøking
                detailFragment.showItem(index)
            }
        } else {
            Log.w("MainActivity", "showItem - Invalid index or titles not initialized. Index: $index") // For feilsøking
        }
    }

    // --- Meny (ActionBar) ---
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_prev -> {
                if (currentIndex > 0) {
                    currentIndex--
                    showItem(currentIndex)
                }
                true
            }
            R.id.action_next -> {
                // Sørg for at titles.size er tilgjengelig og ikke tom
                if (this::titles.isInitialized && titles.isNotEmpty() && currentIndex < titles.size - 1) { // Lagt til sjekk for titles.isInitialized
                    currentIndex++
                    showItem(currentIndex)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Håndterer rotasjon uten å gjøre full restart av aktiviteten.
     * Vi bruker setOrientation til å justere layoutretning.
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d("MainActivity", "onConfigurationChanged - New orientation: ${newConfig.orientation}") // For feilsøking

        setOrientation(newConfig) // Justerer layout-retningen

        // VIKTIG: Fortell detaljfragmentet å vise riktig element på nytt
        // Sjekk også at 'titles' er initialisert
        if (this::titles.isInitialized && titles.isNotEmpty()) {
            Log.d("MainActivity", "onConfigurationChanged - Calling showItem($currentIndex)") // For feilsøking
            showItem(currentIndex)
        } else {
            Log.w("MainActivity", "onConfigurationChanged - Titles not initialized or empty, cannot show item.") // For feilsøking
        }

        // VIKTIG: Be systemet tegne menyen på nytt
        invalidateOptionsMenu()
        Log.d("MainActivity", "onConfigurationChanged - Called invalidateOptionsMenu()") // For feilsøking
    }

    /**
     * Setter LinearLayout orientation i activity_main basert på Configuration.
     * Portrett -> vertical (detail over list). Landskap -> horizontal (side-by-side).
     */
    private fun setOrientation(config: Configuration) {
        // Det er tryggere å bruke view binding eller findViewById hver gang
        // istedenfor å lagre en referanse hvis viewet kan bli nullstilt.
        // Men i dette tilfellet, siden det er i onConfigurationChanged og aktiviteten ikke restartes,
        // er det greit.
        val root = findViewById<android.widget.LinearLayout>(R.id.root_container)
        val newOrientation = if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            android.widget.LinearLayout.VERTICAL
        } else {
            android.widget.LinearLayout.HORIZONTAL
        }
        Log.d("MainActivity", "setOrientation - Setting LinearLayout to: ${if (newOrientation == android.widget.LinearLayout.VERTICAL) "VERTICAL" else "HORIZONTAL"}") // For feilsøking
        root?.orientation = newOrientation // Legg til null-sjekk for sikkerhets skyld
    }
}
