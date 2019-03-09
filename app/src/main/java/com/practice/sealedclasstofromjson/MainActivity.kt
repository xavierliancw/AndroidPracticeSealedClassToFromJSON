package com.practice.sealedclasstofromjson

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.squareup.moshi.FromJson
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import org.json.JSONException

class MainActivity : AppCompatActivity()
{
    data class DTO(
        val property: String,
        val config: Config
    )

    sealed class Config
    {
        object StateIchi : Config()
        object StateNi : Config()

        companion object
        {
            fun rawInit(str: String): Config?
            {
                when (str)
                {
                    StateIchi.javaClass.name -> StateIchi
                    StateNi.javaClass.name   -> StateNi
                    else                     -> null
                }.let { return it }
            }
        }

        object JSONAdapterConfig
        {
            @ToJson
            fun toJson(config: Config): String
            {
                return config.javaClass.name
            }

            @FromJson
            fun fromJson(json: String): Config
            {
                return Config.rawInit(str = json) ?:
                       throw JSONException("Could not initialize Config from json: $json")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val adapter = Moshi.Builder()
                .add(Config.JSONAdapterConfig)
                .add(KotlinJsonAdapterFactory())
                .build()
                .adapter(DTO::class.java)
        val whatever = DTO(property = "whatever",
                           config = Config.StateNi)
        val json = adapter.toJson(whatever)
        println(json)

        val bringItBack = adapter.fromJson(json) ?: throw JSONException("dang it")
        when (bringItBack.config) {
            Config.StateIchi -> println("yep got ichi")
            Config.StateNi   -> println("yep got ni")
        }.let {  }
    }
}
