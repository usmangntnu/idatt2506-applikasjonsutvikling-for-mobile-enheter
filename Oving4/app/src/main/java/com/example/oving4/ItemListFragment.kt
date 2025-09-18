package com.example.oving4

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.ListFragment

/**
 * ItemListFragment viser en enkel liste med titler.
 *
 * Fragmentet tilbyr et callback interface som aktiviteten implementerer for å håndtere
 * listeklikk. Dette er et standard mønster for kommunikasjon mellom fragmenter via aktivitet.
 */
class ItemListFragment : ListFragment() {

    /** Callback som aktiviteten må implementere */
    interface Callback {
        fun onItemSelected(position: Int)
    }

    private var callback: Callback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = try {
            context as Callback
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement ItemListFragment.Callback")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Hent titler fra resources og sett adapter
        val titles = resources.getStringArray(R.array.titles)
        listAdapter = activity?.let {
            ArrayAdapter(it, android.R.layout.simple_list_item_1, titles)
        }
    }

    override fun onListItemClick(l: android.widget.ListView, v: View, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)
        callback?.onItemSelected(position)
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }
}