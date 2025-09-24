package com.example.oving5

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

private const val ENCODING = "UTF-8"

/**
 * En wrapper-klasse for å forenkle HTTP GET og POST forespørsler.
 * Håndterer automatisk cookies og URL-enkoding av parametere.
 * Alle nettverksoperasjoner utføres asynkront ved hjelp av coroutines på `Dispatchers.IO`.
 *
 * @property baseUrl Basis-URLen for alle forespørsler sendt via denne instansen.
 */
class HttpWrapper(private val baseUrl: String) {

    init {
        // Sikrer at CookieHandler er satt opp for å tillate sending og mottak av cookies.
        // Dette er essensielt for at tjeneren skal kunne huske tilstanden til klienten
        // (f.eks. hvem som er registrert, antall gjett).
        if (CookieHandler.getDefault() == null) {
            CookieHandler.setDefault(CookieManager(null, CookiePolicy.ACCEPT_ALL))
        }
    }

    /**
     * Konverterer et map av parametere til en URL query-streng.
     * Eksempel: `mapOf("navn" to "Ola Nordmann", "by" to "Trondheim")` blir til `?navn=Ola+Nordmann&by=Trondheim`.
     * Verdiene blir URL-enkodet.
     *
     * @param params Et map med nøkkel-verdi-par som representerer parameterne.
     * @return En URL query-streng, eller en tom streng hvis `params` er tom.
     */
    private fun toQueryString(params: Map<String, String>): String {
        if (params.isEmpty()) return ""
        return params.entries.joinToString(prefix = "?", separator = "&") { (k, v) ->
            "${URLEncoder.encode(k, ENCODING)}=${URLEncoder.encode(v, ENCODING)}"
        }
    }

    /**
     * Konverterer et map av parametere til en form body-streng for POST-forespørsler.
     * Eksempel: `mapOf("navn" to "Ola Nordmann", "kortnummer" to "123")` blir til `navn=Ola+Nordmann&kortnummer=123`.
     * Verdiene blir URL-enkodet.
     *
     * @param params Et map med nøkkel-verdi-par som representerer parameterne.
     * @return En form body-streng, eller en tom streng hvis `params` er tom.
     */
    private fun toFormBody(params: Map<String, String>): String {
        if (params.isEmpty()) return ""
        return params.entries.joinToString(separator = "&") { (k, v) ->
            "${URLEncoder.encode(k, ENCODING)}=${URLEncoder.encode(v, ENCODING)}"
        }
    }

    /**
     * Åpner en HTTP-tilkobling til den gitte URL-en med standardinnstillinger.
     *
     * @param fullUrl Den komplette URL-en som tilkoblingen skal åpnes mot.
     * @return En åpen [HttpURLConnection].
     * @throws java.io.IOException Hvis en I/O-feil oppstår under åpning av tilkoblingen.
     */
    private fun openConnection(fullUrl: String): HttpURLConnection {
        val conn = URL(fullUrl).openConnection() as HttpURLConnection
        conn.connectTimeout = 10_000 // 10 sekunder
        conn.readTimeout = 10_000    // 10 sekunder
        conn.setRequestProperty("Accept-Charset", ENCODING)
        return conn
    }

    /**
     * Leser responsen fra en [HttpURLConnection] og returnerer den som en streng.
     * Forsøker å hente tegnsett fra `Content-Type` header, ellers brukes [ENCODING].
     *
     * @param conn Den aktive [HttpURLConnection] hvor responsen skal leses fra.
     * @return Responsdataene som en streng.
     * @throws java.io.IOException Hvis en I/O-feil oppstår under lesing av responsen.
     */
    private fun readResponse(conn: HttpURLConnection): String {
        val charset = conn.contentType?.let { ct ->
            ct.split(";").map { it.trim() }.firstOrNull { it.startsWith("charset=", ignoreCase = true) }
                ?.substringAfter("=", ENCODING)
        } ?: ENCODING

        conn.inputStream.bufferedReader(charset(charset)).use { br ->
            return br.readText()
        }
    }

    /**
     * Utfører en HTTP GET-forespørsel til [baseUrl] med de gitte parameterne.
     * Parameterne legges til som en query-streng i URL-en.
     * Kjøres asynkront på `Dispatchers.IO`.
     *
     * @param params Et map med parametere som skal sendes med forespørselen.
     * @return Tjenerens respons som en streng.
     * @throws Exception Hvis en nettverksfeil eller annen feil oppstår.
     */
    suspend fun get(params: Map<String, String> = emptyMap()): String = withContext(Dispatchers.IO) {
        val fullUrl = baseUrl + toQueryString(params)
        val conn = openConnection(fullUrl)
        conn.requestMethod = "GET"
        try {
            readResponse(conn)
        } finally {
            conn.disconnect() // Sikrer at tilkoblingen alltid lukkes
        }
    }

    /**
     * Utfører en HTTP POST-forespørsel til [baseUrl] med de gitte parameterne.
     * Parameterne sendes som URL-enkodet form data i request body.
     * Kjøres asynkront på `Dispatchers.IO`.
     *
     * @param params Et map med parametere som skal sendes med forespørselen.
     * @return Tjenerens respons som en streng.
     * @throws Exception Hvis en nettverksfeil eller annen feil oppstår.
     */
    suspend fun post(params: Map<String, String> = emptyMap()): String = withContext(Dispatchers.IO) {
        val conn = openConnection(baseUrl) // For POST, query-params er ikke i baseUrl her
        conn.requestMethod = "POST"
        conn.doOutput = true // Indikerer at vi skal sende data i body
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=$ENCODING")

        val body = toFormBody(params)
        // Skriv body til output stream
        conn.outputStream.use { os ->
            OutputStreamWriter(os, ENCODING).use { writer ->
                writer.write(body)
            }
        }
        try {
            readResponse(conn)
        } finally {
            conn.disconnect() // Sikrer at tilkoblingen alltid lukkes
        }
    }

    /**
     * Utfører en HTTP GET-forespørsel og returnerer både headere og body som en streng.
     * Nyttig for debugging for å se alle detaljer i tjenerens respons.
     * Kjøres asynkront på `Dispatchers.IO`.
     *
     * @param params Et map med parametere som skal sendes med forespørselen.
     * @return En streng som inneholder både headere og body fra tjenerens respons.
     * @throws Exception Hvis en nettverksfeil eller annen feil oppstår.
     */
    suspend fun getWithHeader(params: Map<String, String> = emptyMap()): String = withContext(Dispatchers.IO) {
        val fullUrl = baseUrl + toQueryString(params)
        val conn = openConnection(fullUrl)
        conn.requestMethod = "GET"
        val headers = StringBuilder()
        conn.headerFields.forEach { (k, v) ->
            headers.append("$k=$v\n")
        }
        val responseBody = try {
            readResponse(conn)
        } finally {
            conn.disconnect()
        }
        headers.append(responseBody)
        headers.toString()
    }
}
