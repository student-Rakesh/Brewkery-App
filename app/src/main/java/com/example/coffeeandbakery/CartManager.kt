package com.example.coffeeandbakery

import android.util.Log
import com.example.coffeeandbakery.data.model.Product
import com.example.coffeeandbakery.data.model.ProductOption

data class CartItem(
    val product: Product,
    val size: ProductOption?,
    val milk: ProductOption?,
    val sugar: String,
    val unitPrice: Double,
    var quantity: Int
)

object CartManager {

    private const val TAG = "CART_DEBUG"

    private val cartItems = mutableListOf<CartItem>()

    val items: List<CartItem>
        get() = cartItems.toList()

    fun addItem(
        product: Product,
        size: ProductOption?,
        milk: ProductOption?,
        sugar: String,
        quantity: Int,
        unitPrice: Double
    ) {
        if (quantity <= 0 || unitPrice <= 0.0) {
            Log.e(TAG, "Invalid product quantity or price")
            return
        }

        val existing = cartItems.find {
            it.product.id == product.id &&
                    it.size?.id == size?.id &&
                    it.milk?.id == milk?.id &&
                    it.sugar == sugar &&
                    it.unitPrice == unitPrice
        }

        if (existing != null) {
            existing.quantity += quantity
        } else {
            cartItems.add(
                CartItem(
                    product = product,
                    size = size,
                    milk = milk,
                    sugar = sugar,
                    unitPrice = unitPrice,
                    quantity = quantity
                )
            )
        }

        Log.d(TAG, "PRODUCT ADDED: ${product.name}")
        Log.d(TAG, "TOTAL ITEMS: ${getCartCount()}")
        Log.d(TAG, "SUBTOTAL: ${getSubtotal()}")
    }

    fun getCartCount(): Int {
        return cartItems.sumOf { it.quantity }
    }

    fun getSubtotal(): Double {
        return cartItems.sumOf {
            it.unitPrice * it.quantity
        }
    }

    fun updateQuantity(item: CartItem, newQuantity: Int) {

        val existing = cartItems.find {
            it.product.id == item.product.id &&
                    it.size?.id == item.size?.id &&
                    it.milk?.id == item.milk?.id &&
                    it.sugar == item.sugar &&
                    it.unitPrice == item.unitPrice
        } ?: return

        if (newQuantity <= 0) {
            cartItems.remove(existing)
        } else {
            existing.quantity = newQuantity
        }

        Log.d(TAG, "UPDATED COUNT: ${getCartCount()}")
    }

    fun removeItem(item: CartItem) {
        cartItems.remove(item)
    }

    fun clearCart() {
        cartItems.clear()
        Log.d(TAG, "CART CLEARED")
    }

    fun isEmpty(): Boolean {
        return cartItems.isEmpty()
    }
}



