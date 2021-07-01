package upc.smtpra.perfeet.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import kotlinx.android.synthetic.main.activity_inicio.*
import upc.smtpra.perfeet.R

class InicioActivity : AppCompatActivity() {

    private val buttonArrayList = ArrayList<String>()
    private lateinit var toMeasurement: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        val buttonArray = resources
                .getStringArray(R.array.arcore_measurement_buttons)

        buttonArray.map{it->
            buttonArrayList.add(it)
        }

        cirMedidaButton.setOnClickListener{
            val intent = Intent(application, Medicion::class.java)
            startActivity(intent)
        }
    }
}