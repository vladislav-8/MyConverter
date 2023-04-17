package com.practicum.myconverter

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.practicum.myconverter.databinding.ActivityMainBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL


class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding

    var fromCurrency = "EUR"
    var toCurrency = "USD"
    private var amount = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        spinnerSetup()
        textChangedStuff()
    }

    private fun textChangedStuff() {
        mainBinding.etFirstConversion.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                try {
                    getApiResult()
                } catch (e: Exception) {
                    Toast.makeText(
                        applicationContext,
                        "type a value",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d("Main", "Before Text Changed")

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("Main", "OnTextChanged")
            }
        })
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun getApiResult() {

        if (mainBinding.etFirstConversion.text.isNotEmpty() && mainBinding.etFirstConversion.text.isNotBlank()) {
            val api =
                "https://api.frankfurter.app/latest?amount=1&from=$fromCurrency&to=$toCurrency"
            if (fromCurrency == toCurrency) {
                Toast.makeText(
                    applicationContext,
                    "Please pick a currency to convert",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    try {

                        val apiResult = URL(api).readText()
                        val jsonObject = JSONObject(apiResult)
                        amount =
                            jsonObject.getJSONObject("rates").getString(toCurrency)
                                .toFloat()

                        Log.d("Main", "$amount")
                        Log.d("Main", apiResult)

                        withContext(Dispatchers.Main) {
                            val text =
                                ((mainBinding.etFirstConversion.text.toString()
                                    .toFloat()) * amount).toString()
                            mainBinding.etSecondConversion.setText(text)
                        }

                    } catch (e: Exception) {
                        Log.e("Main", "$e")
                    }
                }
            }
        }
    }

    private fun spinnerSetup() {
        val spinner: Spinner = findViewById(R.id.spinner_firstConversion)
        val spinner2: Spinner = findViewById(R.id.spinner_secondConversion)

        ArrayAdapter.createFromResource(
            this,
            R.array.currencies,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spinner.adapter = adapter

        }

        ArrayAdapter.createFromResource(
            this,
            R.array.currencies2,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spinner2.adapter = adapter

        }


        spinner.onItemSelectedListener = (
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        TODO("Not yet implemented")
                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        fromCurrency = parent?.getItemAtPosition(position).toString()
                        getApiResult()
                    }

                })

        spinner2.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                toCurrency = parent?.getItemAtPosition(position).toString()
                getApiResult()
            }

        })
    }
}