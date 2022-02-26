package com.example.myhttp.util

object Util {
    fun createJson(id:String = "",title:String, author:String):String
            ="{" +
            "  \"id\": \"${id}\"," +
            "  \"title\": \"${title}\"," +
            "  \"author\": \"${author}\"" +
            "}"
}