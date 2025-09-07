package com.example.oving_2a

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity/**
 * En aktivitet som genererer et tilfeldig heltall, som beskrevet i oppgave a, b, c, og d.
 *
 * Oppgave b: Øvre grense for tallgenerering kan sendes med i Intent-objektet
 * ved å bruke nøkkelen [EXTRA_LIMIT] (standard er 100).
 *
 * Oppgave c: Det tilfeldige tallet returneres som resultat fra aktiviteten.
 * Resultatet, et tilfeldig tall, gis tilbake til den kallende aktiviteten
 * via [Activity.setResult] med nøkkelen [EXTRA_RANDOM_NUMBER].
 *
 * Oppgave d: Aktiviteten avsluttes med `finish()` etter å ha satt resultatet.
 * Toast-utskrift er kommentert ut som spesifisert.
 *
 * For at denne aktiviteten skal kunne tas i bruk av andre aktiviteter (som i oppgave d),
 * har den et intent-filter i `AndroidManifest.xml` med action [RandomActivity.ACTION_GET_RANDOM_NUMBER].
 */
class MainActivity : AppCompatActivity() {

    companion object {
        /**
         * Nøkkel for Intent extra som spesifiserer den øvre grensen for tallgenerering (ref. oppgave b).
         * Verdien forventes å være en [Int].
         */
        const val EXTRA_LIMIT = "limit"

        /**
         * Nøkkel for Intent extra som inneholder det genererte tilfeldige tallet
         * i resultat-Intentet (ref. oppgave c).
         * Verdien vil være en [Int].
         */
        const val EXTRA_RANDOM_NUMBER = "randomNumber"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Denne aktiviteten trenger ikke et eget UI, da den kun returnerer et resultat.

        // Oppgave b: Les øvre grense fra Intent-objektet.
        val upperLimit = intent.getIntExtra(EXTRA_LIMIT, 100)

        // Oppgave a (delvis): Generer et tilfeldig tall.
        // Valgte å bruke 0..upperLimit for å inkludere øvre grense, som ofte er intuitivt.
        val randomNumber = (0..upperLimit).random()

        // Oppgave d: Kommenter ut toasten
        // val toastMessage = "Tilfeldig tall (0-$upperLimit): $randomNumber"
        // Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show()

        // Oppgave c: La det tilfeldige tallet være resultat fra aktiviteten.
        val resultIntent = Intent()
        resultIntent.putExtra(EXTRA_RANDOM_NUMBER, randomNumber)
        setResult(Activity.RESULT_OK, resultIntent)

        // Oppgave d: Sett finish() til slutt.
        finish()
    }
}
