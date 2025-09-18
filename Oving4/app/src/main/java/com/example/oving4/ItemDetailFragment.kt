package com.example.oving4

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

/**
 * ItemDetailFragment viser bildet, tittel og beskrivelse for et valgt element.
 *
 * Bruk showItem(index) for å oppdatere visningen. Denne metoden finner drawable-navnet i
 * string-array 'image_names' og henter ressurs-id via Resources.getIdentifier.
 */
class ItemDetailFragment : Fragment() {

    private var imageView: ImageView? = null
    private var titleView: TextView? = null
    private var descriptionView: TextView? = null

    private lateinit var titles: Array<String>
    private lateinit var descriptions: Array<String>
    private lateinit var imageNames: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        titles = resources.getStringArray(R.array.titles)
        descriptions = resources.getStringArray(R.array.descriptions)
        imageNames = resources.getStringArray(R.array.image_names)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_detail, container, false)
        imageView = view.findViewById(R.id.detail_image)
        titleView = view.findViewById(R.id.detail_title)
        descriptionView = view.findViewById(R.id.detail_description)
        return view
    }

    /**
     * Oppdaterer detaljvisningen med data for index.
     * Henter drawable-id fra imageNames og setter bildet.
     */
    fun showItem(index: Int) {
        if (!this::titles.isInitialized) return
        val safeIndex = index.coerceIn(0, titles.size - 1)
        titleView?.text = titles[safeIndex]
        descriptionView?.text = descriptions[safeIndex]

        // Finn drawable id fra navn
        val res: Resources = resources
        val drawableName = imageNames[safeIndex]
        val drawableId = res.getIdentifier(drawableName, "drawable", requireContext().packageName)
        if (drawableId != 0) {
            imageView?.setImageResource(drawableId)
        } else {
            // Fallback - hvis bildet ikke finnes, sett en innebygd placeholder
            imageView?.setImageResource(android.R.drawable.ic_menu_report_image)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        imageView = null
        titleView = null
        descriptionView = null
    }
}