package upc.smtpra.perfeet.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.view.View
import android.widget.Button
import upc.smtpra.perfeet.R

class ArcoreMedicion : AppCompatActivity() {
    private val TAG = "ArcoreMedicion"
    private val buttonArrayList = ArrayList<String>()
    private lateinit var toMeasurement: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arcore_medicion)

        val buttonArray = resources
            .getStringArray(R.array.arcore_measurement_buttons)

        buttonArray.map{it->
            buttonArrayList.add(it)
        }
        toMeasurement = findViewById(R.id.ir_a_medicion)
        toMeasurement.text = buttonArrayList[0]
        toMeasurement.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(application, Medicion::class.java)
                startActivity(intent)
            }
        })

    }
}