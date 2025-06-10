package com.example.pr

import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("contacts/read.php")
    fun getContacts(): Call<List<Contact>>

    @POST("contacts/create.php")
    fun createContact(@Body contact: Contact): Call<Map<String, String>>

    @POST("contacts/update.php")
    fun updateContact(@Body contact: Contact): Call<Map<String, String>>

    @POST("contacts/delete.php")
    fun deleteContact(@Body contact: Contact): Call<Map<String, String>>
}
