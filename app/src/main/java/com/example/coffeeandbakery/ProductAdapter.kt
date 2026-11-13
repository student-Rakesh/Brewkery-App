
package com.example.coffeeandbakery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.coffeeandbakery.data.model.Product

class ProductAdapter(
    private var products: List<Product>,
    private val onCustomize: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val image: ImageView =
            view.findViewById(R.id.imgProduct)

        val badge: TextView =
            view.findViewById(R.id.tvBadge)

        val name: TextView =
            view.findViewById(R.id.tvProductName)

        val rating: TextView =
            view.findViewById(R.id.tvRating)

        val price: TextView =
            view.findViewById(R.id.tvPrice)

        val customize: Button =
            view.findViewById(R.id.btnCustomize)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)

        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ProductViewHolder,
        position: Int
    ) {
        val product = products[position]

        holder.name.text = product.name
        holder.rating.text =
            "⭐ ${product.rating} (${product.review_count})"

        holder.price.text =
            "$" + String.format("%.2f", product.base_price)

        holder.badge.text = product.badge ?: ""
        holder.badge.visibility =
            if (product.badge.isNullOrBlank()) View.GONE
            else View.VISIBLE

        holder.image.load(product.image_url) {
            crossfade(true)
            placeholder(android.R.drawable.ic_menu_gallery)
            error(android.R.drawable.ic_menu_report_image)
        }

        holder.customize.setOnClickListener {
            onCustomize(product)
        }
    }

    override fun getItemCount(): Int = products.size

    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }
}
