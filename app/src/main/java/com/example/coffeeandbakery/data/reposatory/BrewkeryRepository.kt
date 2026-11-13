//
//package com.example.coffeeandbakery.data.repository
//
//import com.example.coffeeandbakery.RetrofitClient
//import com.example.coffeeandbakery.data.api.RetrofitClient
//import com.example.coffeeandbakery.data.model.BrewkeryResponse
//
//class BrewkeryRepository {
//
//    suspend fun getMenu(): BrewkeryResponse {
//        return RetrofitClient.apiService.getMenu()
//    }
//}



package com.example.coffeeandbakery.data.repository

import com.example.coffeeandbakery.data.api.RetrofitClient
import com.example.coffeeandbakery.data.model.BrewkeryResponse

class BrewkeryRepository {

    suspend fun getMenu(): BrewkeryResponse {
        return RetrofitClient.api.getProducts()
    }
}

