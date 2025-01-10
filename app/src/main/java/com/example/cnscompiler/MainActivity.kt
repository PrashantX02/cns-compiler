package com.example.cnscompiler

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.example.cnscompiler.codeView.codeView
import com.example.cnscompiler.model.JDoodleRequest
import com.example.cnscompiler.model.JDoodleResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var codeView: codeView
    private lateinit var output: TextView
    private lateinit var run: TextView
    private lateinit var spinner: Spinner
    private lateinit var  handwait: LottieAnimationView
    private lateinit var redo : TextView
    private lateinit var undo : TextView

    val languageVersionMap = hashMapOf(
        "java" to "4",
        "rust" to "1",
        "c" to "4",
        "cpp" to "4",
        "nodejs" to "4",
        "go" to "4",
        "csharp" to "4",
        "python3" to "4"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        codeView = findViewById(R.id.codeView)
        output = findViewById(R.id.output)
        run = findViewById(R.id.run)
        spinner = findViewById(R.id.textView)
        handwait = findViewById(R.id.lottieAnimationView)
        undo = findViewById(R.id.undo)
        redo = findViewById(R.id.textView3)

        val items = listOf("java", "rust", "c", "cpp","nodejs","go","csharp","python3")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        adapter.setDropDownViewResource(R.layout.spinner_item)
        spinner.adapter = adapter
        handwait.visibility = View.GONE

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                val selectedItem = parentView?.getItemAtPosition(position).toString()

                when (selectedItem) {
                    "java" -> {
                        codeView.codeText = """
                            public class Main {
                                public static void main(String[] args) {
                                    System.out.println("Hello, World!");
                                }
                            }
                        """.trimIndent()
                        codeView.clearStack()
                    }
                    "rust" -> {
                        codeView.codeText = """
                            fn main() {
                                println!("Hello, World!");
                            }
                        """.trimIndent()
                        codeView.clearStack()
                    }
                    "c" -> {
                        codeView.codeText = """
                            #include <stdio.h>

                            int main() {
                                printf("Hello, World!\\n");
                                return 0;
                            }
                        """.trimIndent()
                        codeView.clearStack()
                    }
                    "cpp" -> {
                        codeView.codeText = """
                            #include <iostream>

                            int main() {
                                std::cout << "Hello, World!" << std::endl;
                                return 0;
                            }
                        """.trimIndent()
                        codeView.clearStack()
                    }
                    "nodejs" -> {
                        codeView.codeText = """
                            console.log("Hello, World!");
                        """.trimIndent()
                        codeView.clearStack()
                    }
                    "go" -> {
                        codeView.codeText = """
                            package main

                            import "fmt"

                            func main() {
                                fmt.Println("Hello, World!")
                            }
                        """.trimIndent()
                        codeView.clearStack()
                    }
                    "csharp" -> {
                        codeView.codeText = """
                            using System;

                            class Program {
                                static void Main() {
                                    Console.WriteLine("Hello, World!");
                                }
                            }
                        """.trimIndent()
                        codeView.clearStack()
                    }
                    "python3" -> {
                        codeView.codeText = """
                            print("Hello, World!")
                        """.trimIndent()
                        codeView.clearStack()
                    }
                    else -> {
                        codeView.codeText = ""
                    }
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Handle case when no item is selected (optional)
            }
        }

        run.setOnClickListener {
            closeKeyboard(codeView)
            val currentBackground = ContextCompat.getDrawable(this, R.drawable.background_run_run)
            if (run.background != currentBackground) {
                run.background = currentBackground
            }

            handwait.visibility = View.VISIBLE
            handwait.playAnimation()
            val lang = spinner.selectedItem.toString()
            val code = codeView.codeText.trimIndent()
            executeCode(code,lang)
        }

        undo.setOnClickListener {
            codeView.undo()
        }
        redo.setOnClickListener {
            codeView.redo()
        }


    }

    private fun executeCode(code: String,lang : String) {
        val api = RetrofitClient.instance

        val request = JDoodleRequest(
            script = code,
            language = lang,
            versionIndex = languageVersionMap.get(lang).toString(),
            clientId = "d3ea6976abafca8d62bd0916e1dcc024",
            clientSecret = "75884ab8c070e2838b9e1230b0baa558f8d59bc6288b6140522abe2a749d00b1"
        )

        api.executeCode(request).enqueue(object : Callback<JDoodleResponse> {
            override fun onResponse(call: Call<JDoodleResponse>, response: Response<JDoodleResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    val outputText = result?.output ?: "No output received"
                    output.text = outputText
                    handwait.visibility = View.GONE
                    val back = ContextCompat.getDrawable(applicationContext,R.drawable.background_run_button)
                    run.background = back
                } else {
                    output.text = "Error: ${response.errorBody()?.string()}"
                    handwait.visibility = View.GONE
                    val back = ContextCompat.getDrawable(applicationContext,R.drawable.background_run_button)
                    run.background = back
                }
            }

            override fun onFailure(call: Call<JDoodleResponse>, t: Throwable) {
                output.text = "Failure: ${t.message}"
                handwait.visibility = View.GONE
                val back = ContextCompat.getDrawable(applicationContext,R.drawable.background_run_button)
                run.background = back
            }
        })
    }

    fun closeKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}
