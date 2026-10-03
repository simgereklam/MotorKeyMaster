package com.motorkey.mastertool

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private lateinit var statusTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusTextView = findViewById(R.id.statusTextView)
        
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this, "Bu cihazda NFC bulunmuyor!", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.let { adapter ->
            if (!adapter.isEnabled) {
                statusTextView.text = "Lütfen telefonun ayarlarından NFC'yi açın!"
            } else {
                statusTextView.text = "Kart okutmaya hazır. Kartı arkaya yaklaştırın..."
                val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
                val filters = arrayOf(IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED))
                adapter.enableForegroundDispatch(this, pendingIntent, filters, null)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        readNfcTag(intent)
    }

    private fun readNfcTag(intent: Intent) {
        val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        tag?.let {
            val payload = it.id
            val uidString = payload.joinToString("") { byte -> java.lang.String.format("%02X", byte) }
            statusTextView.text = "Kart Algılandı!\n\nKart UID: $uidString"
            Toast.makeText(this, "UID Kopyalandı: $uidString", Toast.LENGTH_SHORT).show()
        }
    }
}
