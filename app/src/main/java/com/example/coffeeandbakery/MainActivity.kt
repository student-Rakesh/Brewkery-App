package com.example.coffeeandbakery

import android.os.Bundle
import android.graphics.Color
import android.content.Intent
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeandbakery.data.api.RetrofitClient
import com.example.coffeeandbakery.data.model.Product
import com.example.coffeeandbakery.data.model.BrewkeryResponse
import com.example.coffeeandbakery.data.model.Category
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var adapter: ProductAdapter

    private var allProducts: List<Product> = emptyList()
    private var categories: List<Category> = emptyList()

    private lateinit var categoryButtons: List<Button>
    private lateinit var viewCart: LinearLayout
    private lateinit var tvCartCount: TextView
    private lateinit var tvCartPrice: TextView

    private var selectedCategoryId: String? = null
    private var searchQuery = ""

    companion object {
        private const val TAG = "CART_DEBUG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.coffee_and_bakery)

        // PRODUCT LIST
        val recyclerView =
            findViewById<RecyclerView>(R.id.rvProducts1)

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        adapter = ProductAdapter(emptyList()) { product ->

            Log.d(TAG, "Opening product: ${product.name}")

            val intent = Intent(
                this,
                ProductDetailActivity::class.java
            )

            intent.putExtra("product_id", product.id)
            startActivity(intent)
        }

        recyclerView.adapter = adapter

        // CART INITIALIZATION
        viewCart = findViewById(R.id.View_cart)
        tvCartCount = findViewById(R.id.tvCartCount)
        tvCartPrice = findViewById(R.id.tvCartPrice)

        updateCartDisplay()

        // VIEW CART / CHECKOUT
        viewCart.setOnClickListener {

            val cartItems = CartManager.items
            val count = CartManager.getCartCount()
            val subtotal = CartManager.getSubtotal()

            Log.d(TAG, "Checkout clicked")
            Log.d(TAG, "Cart items: ${cartItems.size}")
            Log.d(TAG, "Cart count: $count")
            Log.d(TAG, "Subtotal: $subtotal")

            if (cartItems.isEmpty() || count <= 0) {
                showMessage(
                    "Your cart is empty! Add product first."
                )
                return@setOnClickListener
            }

            if (subtotal <= 0.0) {
                showMessage("Invalid cart total")
                return@setOnClickListener
            }

            try {
                val intent = Intent(
                    this@MainActivity,
                    CheckoutActivity::class.java
                )

                startActivity(intent)

            } catch (e: Exception) {
                Log.e(TAG, "Checkout navigation failed", e)
                showMessage("Unable to open checkout")
            }
        }

        // CATEGORY BUTTONS
        val btnAll = findViewById<Button>(R.id.btnAll)
        val btnHotCoffee = findViewById<Button>(R.id.btnHotCoffee)
        val btnColdBrews = findViewById<Button>(R.id.btnColdBrews)
        val btnBakery = findViewById<Button>(R.id.btnBakery)

        categoryButtons = listOf(
            btnAll,
            btnHotCoffee,
            btnColdBrews,
            btnBakery
        )

        selectCategoryButton(btnAll)

        btnAll.setOnClickListener {
            selectedCategoryId = null
            selectCategoryButton(it as Button)
            showProducts()
        }

        btnHotCoffee.setOnClickListener {

            val id = findCategoryId("hot", "coffee")

            if (id != null) {
                selectedCategoryId = id
                selectCategoryButton(it as Button)
                showProducts()
            } else {
                showMessage("Hot Coffee category not found")
            }
        }

        btnColdBrews.setOnClickListener {

            val id = findCategoryId("cold", "brew")

            if (id != null) {
                selectedCategoryId = id
                selectCategoryButton(it as Button)
                showProducts()
            } else {
                showMessage("Cold Brews category not found")
            }
        }

        btnBakery.setOnClickListener {

            val id = findCategoryId("bakery")

            if (id != null) {
                selectedCategoryId = id
                selectCategoryButton(it as Button)
                showProducts()
            } else {
                showMessage("Bakery category not found")
            }
        }

        // SEARCH
        findViewById<EditText>(R.id.etSearch)
            .addTextChangedListener(object :
                android.text.TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    searchQuery = s?.toString()?.trim() ?: ""
                    showProducts()
                }

                override fun afterTextChanged(
                    s: android.text.Editable?
                ) {}
            })

        // FETCH PRODUCTS
        fetchProducts()
    }

    // UPDATE CART DISPLAY
    private fun updateCartDisplay() {

        if (!::tvCartCount.isInitialized) return

        val count = CartManager.getCartCount()
        val subtotal = CartManager.getSubtotal()

        tvCartCount.text = count.toString()

        tvCartPrice.text = String.format(
            Locale.US,
            "$%.2f",
            subtotal
        )

        Log.d(TAG, "Cart display updated: $count items, $subtotal")

        viewCart.isEnabled = true
        viewCart.isClickable = true
    }

    // REFRESH WHEN RETURNING FROM PRODUCT DETAIL
    override fun onResume() {
        super.onResume()
        updateCartDisplay()
    }

    // CATEGORY DESIGN
    private fun selectCategoryButton(
        selectedButton: Button
    ) {

        categoryButtons.forEach { button ->

            if (button.id == selectedButton.id) {

                button.setBackgroundResource(
                    R.drawable.bg_category_selected
                )

                button.setTextColor(Color.WHITE)

            } else {

                button.setBackgroundResource(
                    R.drawable.bg_category
                )

                button.setTextColor(
                    Color.parseColor("#594538")
                )
            }
        }
    }

    // FETCH PRODUCTS
    private fun fetchProducts() {

        lifecycleScope.launch {

            try {

                val response: BrewkeryResponse =
                    RetrofitClient.api.getProducts()

                allProducts = response.items
                categories = response.categories

                showProducts()

                showMessage(
                    "${allProducts.size} products loaded"
                )

            } catch (e: Exception) {

                Log.e(TAG, "API error", e)

                showMessage(
                    "API Error: ${e.localizedMessage}"
                )
            }
        }
    }

    // FIND CATEGORY
    private fun findCategoryId(
        vararg keywords: String
    ): String? {

        return categories.firstOrNull { category ->

            val name = category.name.lowercase()

            keywords.all { keyword ->
                name.contains(keyword)
            }

        }?.id
    }

    // FILTER PRODUCTS
    private fun showProducts() {

        val filteredProducts =
            allProducts.filter { product ->

                val matchesCategory =
                    selectedCategoryId == null ||
                            product.category_id == selectedCategoryId

                val matchesSearch =
                    product.name.contains(
                        searchQuery,
                        ignoreCase = true
                    ) ||
                            product.description.contains(
                                searchQuery,
                                ignoreCase = true
                            )

                matchesCategory && matchesSearch
            }

        adapter.updateProducts(filteredProducts)

        Log.d(TAG, "Products displayed: ${filteredProducts.size}")
    }

    // SHOW MESSAGE
    private fun showMessage(message: String) {

        Toast.makeText(
            this,
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}

