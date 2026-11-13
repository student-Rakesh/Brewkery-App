package com.example.coffeeandbakery

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.coffeeandbakery.data.api.RetrofitClient
import com.example.coffeeandbakery.data.model.Product
import com.example.coffeeandbakery.data.model.ProductOption
import kotlinx.coroutines.launch
import java.util.Locale

class ProductDetailActivity : ComponentActivity() {

    private lateinit var product: Product

    private lateinit var imgProduct: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvPrice: TextView
    private lateinit var tvIngredients: TextView
    private lateinit var tvQuantity: TextView
    private lateinit var btnAddCart: Button

    private lateinit var sizeGroup: RadioGroup
    private lateinit var milkGroup: RadioGroup
    private lateinit var sugarGroup: RadioGroup

    private var selectedSize: ProductOption? = null
    private var selectedMilk: ProductOption? = null
    private var selectedSugar = ""

    private var quantity = 1
    private var basePrice = 0.0
    private var currentPrice = 0.0
    private var isAddingToCart = false

    private val optionMap = mutableMapOf<Int, ProductOption>()
    private val sugarMap = mutableMapOf<Int, String>()

    companion object {
        private const val TAG = "CART_DEBUG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_product_detail)

        imgProduct = findViewById(R.id.imgDetailProduct)
        tvName = findViewById(R.id.tvDetailName)
        tvDescription = findViewById(R.id.tvDetailDescription)
        tvPrice = findViewById(R.id.tvDetailPrice)
        tvIngredients = findViewById(R.id.tvIngredients)
        tvQuantity = findViewById(R.id.tvQuantity)

        sizeGroup = findViewById(R.id.rgSizes)
        milkGroup = findViewById(R.id.rgMilk)
        sugarGroup = findViewById(R.id.rgSugar)

        btnAddCart = findViewById(R.id.btnAddToCart)
        btnAddCart.isEnabled = false

        findViewById<TextView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnMinus).setOnClickListener {
            if (quantity > 1) {
                quantity--
                updatePrice()
            }
        }

        findViewById<Button>(R.id.btnPlus).setOnClickListener {
            quantity++
            updatePrice()
        }

        val productId = intent.getIntExtra("product_id", -1)

        if (productId == -1) {
            showMessage("Invalid product")
            finish()
            return
        }

        btnAddCart.setOnClickListener {
            addProductToCart()
        }

        loadProduct(productId)
    }

    // ADD PRODUCT TO CART
    private fun addProductToCart() {

        if (!::product.isInitialized) {
            showMessage("Please wait for product loading")
            return
        }

        if (isAddingToCart) return

        if (quantity <= 0 || currentPrice <= 0.0) {
            showMessage("Invalid product price")
            return
        }

        isAddingToCart = true
        btnAddCart.isEnabled = false

        try {

            val oldCount = CartManager.getCartCount()

            CartManager.addItem(
                product = product,
                size = selectedSize,
                milk = selectedMilk,
                sugar = selectedSugar,
                quantity = quantity,
                unitPrice = currentPrice
            )

            val newCount = CartManager.getCartCount()
            val subtotal = CartManager.getSubtotal()

            Log.d(TAG, "Product: ${product.name}")
            Log.d(TAG, "Old Count: $oldCount")
            Log.d(TAG, "New Count: $newCount")
            Log.d(TAG, "Items: ${CartManager.items.size}")
            Log.d(TAG, "Subtotal: $subtotal")

            if (newCount == oldCount + quantity) {

                Toast.makeText(
                    this,
                    "${product.name} added to cart successfully!",
                    Toast.LENGTH_LONG
                ).show()

                setResult(RESULT_OK)
                finish()

            } else {

                Log.e(TAG, "Cart count verification failed")

                showMessage("Unable to add product. Please try again.")

                isAddingToCart = false
                btnAddCart.isEnabled = true
            }

        } catch (e: Exception) {

            Log.e(TAG, "Add to cart error", e)

            isAddingToCart = false
            btnAddCart.isEnabled = true

            showMessage("Cart Error: ${e.localizedMessage}")
        }
    }

    // LOAD PRODUCT
    private fun loadProduct(id: Int) {

        lifecycleScope.launch {

            try {

                product = RetrofitClient.api.getProductDetails(id)

                showProductDetails()

                btnAddCart.isEnabled = true

                Log.d(TAG, "Product loaded: ${product.name}")

            } catch (e: Exception) {

                Log.e(TAG, "Product loading failed", e)

                showMessage(
                    "Unable to load product: ${e.localizedMessage}"
                )

                finish()
            }
        }
    }

    // DISPLAY PRODUCT DETAILS
    private fun showProductDetails() {

        basePrice = product.base_price

        tvName.text = product.name
        tvDescription.text = product.description

        tvIngredients.text =
            product.ingredients.joinToString(" • ")

        imgProduct.load(product.image_url) {
            crossfade(true)
            placeholder(android.R.drawable.ic_menu_gallery)
            error(android.R.drawable.ic_menu_report_image)
        }

        selectedSize = null
        selectedMilk = null
        selectedSugar = ""

        optionMap.clear()
        sugarMap.clear()

        createOptionButtons(
            sizeGroup,
            product.customizations.sizes,
            true
        )

        createOptionButtons(
            milkGroup,
            product.customizations.milk_options,
            false
        )

        sugarGroup.removeAllViews()

        product.customizations.sugar_levels.forEachIndexed {
                index, sugar ->

            val radio = RadioButton(this).apply {
                id = View.generateViewId()
                text = sugar
                textSize = 13f
                setTextColor(
                    android.graphics.Color.rgb(56, 36, 27)
                )
            }

            sugarMap[radio.id] = sugar
            sugarGroup.addView(radio)

            if (index == 0) {
                radio.isChecked = true
                selectedSugar = sugar
            }
        }

        sugarGroup.setOnCheckedChangeListener { _, checkedId ->

            selectedSugar = sugarMap[checkedId] ?: ""
            updatePrice()
        }

        updatePrice()
    }

    // CREATE OPTION BUTTONS
    private fun createOptionButtons(
        group: RadioGroup,
        options: List<ProductOption>,
        isSize: Boolean
    ) {

        group.removeAllViews()

        options.forEachIndexed { index, option ->

            val radio = RadioButton(this).apply {

                id = View.generateViewId()

                text = buildString {

                    append(
                        option.label ?: option.name ?: option.id
                    )

                    if (option.extra_price > 0) {
                        append(
                            " (+$" + String.format(
                                Locale.US,
                                "%.2f",
                                option.extra_price
                            ) + ")"
                        )
                    }
                }

                textSize = 13f

                setTextColor(
                    android.graphics.Color.rgb(56, 36, 27)
                )
            }

            optionMap[radio.id] = option
            group.addView(radio)

            if (index == 0) {

                radio.isChecked = true

                if (isSize) {
                    selectedSize = option
                } else {
                    selectedMilk = option
                }
            }
        }

        group.setOnCheckedChangeListener { _, checkedId ->

            val selected = optionMap[checkedId]

            if (isSize) {
                selectedSize = selected
            } else {
                selectedMilk = selected
            }

            updatePrice()
        }
    }

    // UPDATE PRICE
    private fun updatePrice() {

        if (!::product.isInitialized) return

        val sizePrice =
            selectedSize?.extra_price ?: 0.0

        val milkPrice =
            selectedMilk?.extra_price ?: 0.0

        currentPrice =
            basePrice + sizePrice + milkPrice

        val total = currentPrice * quantity

        tvPrice.text = String.format(
            Locale.US,
            "$%.2f",
            total
        )

        tvQuantity.text = quantity.toString()

        btnAddCart.text = String.format(
            Locale.US,
            "Add to Cart • $%.2f",
            total
        )
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

