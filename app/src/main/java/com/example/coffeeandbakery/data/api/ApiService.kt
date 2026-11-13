//package com.example.coffeeandbakery.data.api
//
//import com.example.coffeeandbakery.data.model.BrewkeryResponse
//import retrofit2.http.GET
//
//interface ApiService {
//
//    @GET("data.json")
//    suspend fun getProducts(): BrewkeryResponse
//}



package com.example.coffeeandbakery.data.api

import com.example.coffeeandbakery.data.model.BrewkeryResponse
import com.example.coffeeandbakery.data.model.Product
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    // FULL MENU API
    @GET("data.json")
    suspend fun getProducts(): BrewkeryResponse

    // SINGLE PRODUCT DETAIL API
    @GET("api/items/{id}.json")
    suspend fun getProductDetails(
        @Path("id") id: Int
    ): Product
}

