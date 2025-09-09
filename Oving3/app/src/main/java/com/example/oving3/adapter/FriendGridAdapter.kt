package com.example.oving3.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.oving3.R
import com.example.oving3.model.Friend

/**
 * Adapter for [android.widget.GridView] som viser en liste av [Friend]-objekter.
 *
 * Bruker ViewHolder-mønsteret for effektiv gjenbruk av views, noe som er god praksis for listekomponenter.
 *
 * **Samspill:**
 * - Brukes av [com.example.oving3.MainActivity] til å populere `gridViewFriends`.
 * - Tar imot en `MutableList<Friend>` fra `MainActivity` som datakilde.
 * - Inflater layoutet `R.layout.item_friend` for hver venn som skal vises.
 * - Binder data fra hvert [Friend]-objekt til [TextView]s i `item_friend.xml`.
 *
 * @param context Applikasjons- eller Activity-konteksten, brukes av [LayoutInflater].
 * @param friends Referansen til listen av [Friend]-objekter som skal vises. Adapteren jobber direkte med denne listen.
 */
class FriendGridAdapter(
    private val context: Context,
    private val friends: MutableList<Friend> // Direkte referanse for dynamiske oppdateringer
) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    /**
     * Returnerer antall venner i listen.
     * Kalles av GridView for å vite hvor mange items som skal vises.
     */
    override fun getCount(): Int = friends.size

    /**
     * Returnerer [Friend]-objektet på en gitt posisjon.
     */
    override fun getItem(position: Int): Friend = friends[position]

    /**
     * Returnerer ID-en for et item på en gitt posisjon (her brukes bare posisjonen som ID).
     */
    override fun getItemId(position: Int): Long = position.toLong()

    /**
     * Holder referanser til views i hvert item-layout (`R.layout.item_friend`)
     * for å unngå gjentatte `findViewById`-kall (ViewHolder-mønsteret).
     */
    private data class ViewHolder(val nameView: TextView, val dateView: TextView)

    /**
     * Oppretter eller gjenbruker en [View] for hvert item i GridView og binder data til den.
     *
     * Dette er kjernen i adapteren. Hvis `convertView` er null, inflates et nytt layout.
     * Hvis ikke, gjenbrukes det eksisterende `convertView` for bedre ytelse.
     * Data fra `friends[position]` hentes og settes på de relevante [TextView]s.
     *
     * @param position Posisjonen til itemet som skal vises.
     * @param convertView Den gamle viewen som skal gjenbrukes, hvis mulig. Kan være null.
     * @param parent Foreldre-ViewGroup som denne viewen vil bli lagt til i.
     * @return Viewen som representerer dataen på den gitte posisjonen.
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val holder: ViewHolder
        val view: View

        if (convertView == null) {
            // Inflater nytt item-layout hvis ingen view kan gjenbrukes
            view = inflater.inflate(R.layout.item_friend, parent, false)
            // Opprett en ny ViewHolder og finn referanser til TextViews
            holder = ViewHolder(
                nameView = view.findViewById(R.id.textName),
                dateView = view.findViewById(R.id.textBirthdate)
            )
            // Lagre ViewHolder i viewens tag for senere gjenbruk
            view.tag = holder
        } else {
            // Gjenbruk eksisterende view og hent ViewHolder fra taggen
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        // Hent den aktuelle vennen
        val friend = getItem(position) // Bruker getItem() for konsistens

        // Bind data fra Friend-objektet til TextViews
        holder.nameView.text = friend.name
        holder.dateView.text = friend.birthdate

        return view
    }
}
