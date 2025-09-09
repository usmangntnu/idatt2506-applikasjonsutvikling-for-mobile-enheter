package com.example.oving3.model

/**
 * Representerer en venn med navn og fødselsdato.
 *
 * @property name Navnet på vennen.
 * @property birthdate Fødselsdatoen til vennen, lagret som en String. Validering av format er ikke en del av denne oppgaven.
 */
data class Friend(var name: String, var birthdate: String)
