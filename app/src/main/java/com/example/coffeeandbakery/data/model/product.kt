
package com.example.coffeeandbakery.data.model

data class BrewkeryResponse(
    val meta: StoreMeta,
    val categories: List<Category>,
    val items: List<Product>
)

data class StoreMeta(
    val app: String,
    val version: String,
    val tagline: String,
    val currency: String,
    val currency_symbol: String,
    val delivery_fee: Double,
    val tax_rate_percent: Double,
    val estimated_delivery_time: String
)

data class Category(
    val id: String,
    val name: String,
    val icon: String,
    val item_count: Int
)

data class Product(
    val id: Int,
    val category_id: String,
    val name: String,
    val tagline: String,
    val description: String,
    val base_price: Double,
    val rating: Double,
    val review_count: Int,
    val prep_time: String,
    val calories: Int,
    val image_url: String,
    val badge: String?,
    val ingredients: List<String>,
    val customizations: Customizations
)

data class Customizations(
    val sizes: List<ProductOption>,
    val sugar_levels: List<String>,
    val milk_options: List<ProductOption>
)

data class ProductOption(
    val id: String,
    val label: String? = null,
    val name: String? = null,
    val extra_price: Double
)
