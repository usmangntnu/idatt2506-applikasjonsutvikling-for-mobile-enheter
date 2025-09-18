import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.FragmentActivity
import com.example.oving4.ItemDetailFragment
import com.example.oving4.ItemListFragment
import com.example.oving4.R

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
class MainActivity : FragmentActivity(), ItemListFragment.Callback {

    /** Liste av titler hentet fra res/values/strings.xml */
    private lateinit var titles: Array<String>

    /** Nåværende element som vises (index i arrays) */
    private var currentIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            R.layout.activity_main)

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
        showItem(currentIndex)
    }

    /**
     * Callback fra ItemListFragment når et element i lista velges.
     * Aktiviteten mottar posisjon og videreformidler visningen til ItemDetailFragment.
     */
    override fun onItemSelected(position: Int) {
        currentIndex = position
        showItem(currentIndex)
    }

    /**
     * Viser element på posisjon i ItemDetailFragment.
     */
    private fun showItem(index: Int) {
        val detail = supportFragmentManager.findFragmentById(R.id.detail_container)
        if (detail is ItemDetailFragment) {
            detail.showItem(index)
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
                val n = titles.size
                if (currentIndex < n - 1) {
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
        setOrientation(newConfig)
    }

    /**
     * Setter LinearLayout orientation i activity_main basert på Configuration.
     * Portrett -> vertical (detail over list). Landskap -> horizontal (side-by-side).
     */
    private fun setOrientation(config: Configuration) {
        val root = findViewById<android.widget.LinearLayout>(R.id.root_container)
        root.orientation =
            if (config.orientation == Configuration.ORIENTATION_PORTRAIT)
                android.widget.LinearLayout.VERTICAL
            else
                android.widget.LinearLayout.HORIZONTAL
    }
}