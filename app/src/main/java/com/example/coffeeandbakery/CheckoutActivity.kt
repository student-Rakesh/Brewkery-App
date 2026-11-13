//package com.example.coffeeandbakery
//
//import android.os.Bundle
//import android.graphics.Color
//import android.util.Log
//import android.view.View
//import android.widget.*
//import androidx.activity.ComponentActivity
//import java.util.Locale
//
//class CheckoutActivity : ComponentActivity() {
//
//    private lateinit var tvSubtotal: TextView
//    private lateinit var tvDelivery: TextView
//    private lateinit var tvTax: TextView
//    private lateinit var tvTotal: TextView
//    private lateinit var btnPlaceOrder: Button
//    private lateinit var productsContainer: LinearLayout
//
//    companion object {
//        private const val TAG = "CHECKOUT_DEBUG"
//        private const val DELIVERY_FEE = 2.50
//        private const val TAX_RATE = 0.08
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        setContentView(R.layout.activity_checkout)
//
//        try {
//            initializeViews()
//            setupBackButton()
//            displayCart()
//        } catch (e: Exception) {
//            Log.e(TAG, "Checkout initialization failed", e)
//            Toast.makeText(
//                this,
//                "Checkout error: ${e.localizedMessage}",
//                Toast.LENGTH_LONG
//            ).show()
//            finish()
//        }
//    }
//
//    private fun initializeViews() {
//
//        tvSubtotal = findViewById(R.id.tvSubtotal)
//        tvDelivery = findViewById(R.id.tvDelivery)
//        tvTax = findViewById(R.id.tvTax)
//        tvTotal = findViewById(R.id.tvTotal)
//
//        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
//
//        productsContainer =
//            findViewById(R.id.productsContainer)
//
//        if (tvSubtotal == null ||
//            tvDelivery == null ||
//            tvTax == null ||
//            tvTotal == null ||
//            btnPlaceOrder == null ||
//            productsContainer == null
//        ) {
//            throw IllegalStateException(
//                "Checkout XML views are missing"
//            )
//        }
//    }
//
//    private fun setupBackButton() {
//
//        findViewById<TextView>(R.id.btnBack)
//            .setOnClickListener {
//                finish()
//            }
//    }
//
//    private fun money(value: Double): String {
//        return String.format(
//            Locale.US,
//            "$%.2f",
//            value
//        )
//    }
//
//    private fun displayCart() {
//
//        val cartItems = CartManager.items
//
//        Log.d(TAG, "Cart items: ${cartItems.size}")
//        Log.d(TAG, "Cart count: ${CartManager.getCartCount()}")
//        Log.d(TAG, "Subtotal: ${CartManager.getSubtotal()}")
//
//        if (cartItems.isEmpty()) {
//
//            Toast.makeText(
//                this,
//                "Your cart is empty! Add products first.",
//                Toast.LENGTH_LONG
//            ).show()
//
//            finish()
//            return
//        }
//
//        productsContainer.removeAllViews()
//
//        cartItems.forEach { item ->
//
//            val productView = TextView(this).apply {
//
//                text = buildString {
//
//                    append(item.product.name)
//                    append("\nSize: ${item.size?.label ?: item.size?.name ?: "Regular"}")
//                    append("\nMilk: ${item.milk?.label ?: item.milk?.name ?: "Default"}")
//                    append("\nSugar: ${item.sugar}")
//                    append("\nQuantity: ${item.quantity}")
//                    append("\nUnit Price: ${money(item.unitPrice)}")
//                    append("\nTotal: ${money(item.unitPrice * item.quantity)}")
//                }
//
//                textSize = 14f
//
//                setTextColor(
//                    Color.rgb(56, 36, 27)
//                )
//
//                setPadding(0, 16, 0, 16)
//            }
//
//            productsContainer.addView(productView)
//
//            val divider = View(this).apply {
//                setBackgroundColor(
//                    Color.parseColor("#DDCFC4")
//                )
//            }
//
//            productsContainer.addView(
//                divider,
//                LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    1
//                )
//            )
//        }
//
//        calculateTotal()
//    }
//
//    private fun calculateTotal() {
//
//        val subtotal = CartManager.getSubtotal()
//
//        val deliveryFee = DELIVERY_FEE
//        val tax = subtotal * TAX_RATE
//        val total = subtotal + deliveryFee + tax
//
//        tvSubtotal.text = money(subtotal)
//        tvDelivery.text = money(deliveryFee)
//        tvTax.text = money(tax)
//        tvTotal.text = money(total)
//
//        Log.d(TAG, "Subtotal: $subtotal")
//        Log.d(TAG, "Delivery: $deliveryFee")
//        Log.d(TAG, "Tax: $tax")
//        Log.d(TAG, "Total: $total")
//
//        btnPlaceOrder.isEnabled = total > 0
//    }
//
//    override fun onResume() {
//        super.onResume()
//
//        if (::productsContainer.isInitialized) {
//            if (CartManager.items.isNotEmpty()) {
//                calculateTotal()
//            }
//        }
//    }
//}
//



package com.example.coffeeandbakery

import android.os.Bundle
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.ComponentActivity
import java.util.Locale

class CheckoutActivity : ComponentActivity() {

    private lateinit var tvSubtotal: TextView
    private lateinit var tvDelivery: TextView
    private lateinit var tvTax: TextView
    private lateinit var tvTotal: TextView
    private lateinit var btnPlaceOrder: Button
    private lateinit var productsContainer: LinearLayout

    companion object {
        private const val TAG = "CHECKOUT_DEBUG"
        private const val DELIVERY_FEE = 2.50
        private const val TAX_RATE = 0.08
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        try {
            initializeViews()
            setupBackButton()
            setupPlaceOrderButton()
            displayCart()

        } catch (e: Exception) {
            Log.e(TAG, "Checkout initialization failed", e)

            Toast.makeText(
                this,
                "Checkout error: ${e.localizedMessage}",
                Toast.LENGTH_LONG
            ).show()

            finish()
        }
    }

    private fun initializeViews() {
        tvSubtotal = findViewById(R.id.tvSubtotal)
        tvDelivery = findViewById(R.id.tvDelivery)
        tvTax = findViewById(R.id.tvTax)
        tvTotal = findViewById(R.id.tvTotal)

        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        productsContainer = findViewById(R.id.productsContainer)
    }

    private fun setupBackButton() {
        findViewById<TextView>(R.id.btnBack)
            .setOnClickListener {
                finish()
            }
    }

    private fun setupPlaceOrderButton() {

        btnPlaceOrder.setOnClickListener {

            if (CartManager.isEmpty()) {
                Toast.makeText(
                    this,
                    "Your cart is empty!",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val total = calculateTotal()

            if (total <= 0) {
                Toast.makeText(
                    this,
                    "Invalid order amount!",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            btnPlaceOrder.isEnabled = false

            try {
                Log.d(TAG, "Order placed successfully")
                Log.d(TAG, "Order total: $total")

                // Correct way to clear cart
                CartManager.clearCart()

                Toast.makeText(
                    this,
                    "Order placed successfully!",
                    Toast.LENGTH_LONG
                ).show()

                finish()

            } catch (e: Exception) {
                Log.e(TAG, "Order placement failed", e)

                btnPlaceOrder.isEnabled = true

                Toast.makeText(
                    this,
                    "Order failed: ${e.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun money(value: Double): String {
        return String.format(
            Locale.US,
            "$%.2f",
            value
        )
    }

    private fun displayCart() {

        val cartItems = CartManager.items

        Log.d(TAG, "Cart items: ${cartItems.size}")
        Log.d(TAG, "Cart count: ${CartManager.getCartCount()}")

        if (cartItems.isEmpty()) {
            Toast.makeText(
                this,
                "Your cart is empty! Add products first.",
                Toast.LENGTH_LONG
            ).show()

            finish()
            return
        }

        productsContainer.removeAllViews()

        cartItems.forEach { item ->

            val productView = TextView(this).apply {

                text = buildString {
                    append(item.product.name)

                    append("\nSize: ${item.size?.label ?: item.size?.name ?: "Regular"}")

                    append("\nMilk: ${item.milk?.label ?: item.milk?.name ?: "Default"}")

                    append("\nSugar: ${item.sugar}")
                    append("\nQuantity: ${item.quantity}")

                    append("\nUnit Price: ${money(item.unitPrice)}")

                    append("\nTotal: ${money(item.unitPrice * item.quantity)}")
                }

                textSize = 14f

                setTextColor(Color.rgb(56, 36, 27))

                setPadding(0, 16, 0, 16)
            }

            productsContainer.addView(productView)

            val divider = View(this).apply {
                setBackgroundColor(Color.parseColor("#DDCFC4"))
            }

            productsContainer.addView(
                divider,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1
                )
            )
        }

        calculateTotal()
    }

    private fun calculateTotal(): Double {

        val subtotal = CartManager.getSubtotal()

        val deliveryFee = DELIVERY_FEE
        val tax = subtotal * TAX_RATE
        val total = subtotal + deliveryFee + tax

        tvSubtotal.text = money(subtotal)
        tvDelivery.text = money(deliveryFee)
        tvTax.text = money(tax)
        tvTotal.text = money(total)

        Log.d(TAG, "Subtotal: $subtotal")
        Log.d(TAG, "Delivery: $deliveryFee")
        Log.d(TAG, "Tax: $tax")
        Log.d(TAG, "Total: $total")

        btnPlaceOrder.isEnabled =
            !CartManager.isEmpty() && total > 0

        return total
    }

    override fun onResume() {
        super.onResume()

        if (::productsContainer.isInitialized &&
            !CartManager.isEmpty()
        ) {
            calculateTotal()
        }
    }
}

