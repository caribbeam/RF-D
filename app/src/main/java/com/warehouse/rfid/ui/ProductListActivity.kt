package com.warehouse.rfid.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.warehouse.rfid.R
import com.warehouse.rfid.data.database.AppDatabase
import com.warehouse.rfid.data.database.ProductEntity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Ürün Listesi Activity
 * 
 * ÖZELLİKLER:
 * - Arama (İsim, Kod)
 * - Filtreleme (Stok durumu, Konum)
 * - Sıralama (İsim, Kod, Miktar)
 * - Detaylı ürün bilgisi
 */
class ProductListActivity : AppCompatActivity() {
    
    private lateinit var database: AppDatabase
    private lateinit var adapter: ProductListAdapter
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmptyMessage: TextView
    private lateinit var chipGroup: ChipGroup
    private lateinit var chipAll: Chip
    private lateinit var chipInStock: Chip
    private lateinit var chipLowStock: Chip
    private lateinit var chipOutOfStock: Chip
    
    private var currentFilter = FilterType.ALL
    private var currentSearchQuery = ""
    private var currentSortBy = SortBy.NAME
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Ürün Listesi"
        
        database = AppDatabase.getDatabase(this)
        
        initViews()
        setupRecyclerView()
        setupFilters()
        loadProducts()
    }
    
    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerViewProducts)
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage)
        chipGroup = findViewById(R.id.chipGroupFilters)
        chipAll = findViewById(R.id.chipAll)
        chipInStock = findViewById(R.id.chipInStock)
        chipLowStock = findViewById(R.id.chipLowStock)
        chipOutOfStock = findViewById(R.id.chipOutOfStock)
    }
    
    private fun setupRecyclerView() {
        adapter = ProductListAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
    
    private fun setupFilters() {
        chipAll.setOnClickListener {
            currentFilter = FilterType.ALL
            loadProducts()
        }
        
        chipInStock.setOnClickListener {
            currentFilter = FilterType.IN_STOCK
            loadProducts()
        }
        
        chipLowStock.setOnClickListener {
            currentFilter = FilterType.LOW_STOCK
            loadProducts()
        }
        
        chipOutOfStock.setOnClickListener {
            currentFilter = FilterType.OUT_OF_STOCK
            loadProducts()
        }
    }
    
    private fun loadProducts() {
        lifecycleScope.launch {
            try {
                val products = when (currentFilter) {
                    FilterType.ALL -> {
                        if (currentSearchQuery.isEmpty()) {
                            database.productDao().getAllProductsList()
                        } else {
                            database.productDao().searchProducts(currentSearchQuery)
                        }
                    }
                    FilterType.IN_STOCK -> {
                        database.productDao().getInStockProducts()
                    }
                    FilterType.LOW_STOCK -> {
                        database.productDao().getLowStockProducts()
                    }
                    FilterType.OUT_OF_STOCK -> {
                        database.productDao().getOutOfStockProducts()
                    }
                }
                
                // Sıralama uygula
                val sortedProducts = when (currentSortBy) {
                    SortBy.NAME -> products.sortedBy { it.name }
                    SortBy.CODE -> products.sortedBy { it.productCode }
                    SortBy.QUANTITY_ASC -> products.sortedBy { it.quantity }
                    SortBy.QUANTITY_DESC -> products.sortedByDescending { it.quantity }
                }
                
                adapter.submitList(sortedProducts)
                updateEmptyState(sortedProducts.isEmpty())
                
            } catch (e: Exception) {
                // Hata durumu
                updateEmptyState(true)
            }
        }
    }
    
    private fun updateEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            tvEmptyMessage.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            tvEmptyMessage.text = when (currentFilter) {
                FilterType.ALL -> "Henüz ürün eklenmemiş"
                FilterType.IN_STOCK -> "Stokta ürün yok"
                FilterType.LOW_STOCK -> "Düşük stoklu ürün yok"
                FilterType.OUT_OF_STOCK -> "Tükenen ürün yok"
            }
        } else {
            tvEmptyMessage.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_product_list, menu)
        
        // Arama
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Ürün ara..."
        
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            
            override fun onQueryTextChange(newText: String?): Boolean {
                currentSearchQuery = newText ?: ""
                loadProducts()
                return true
            }
        })
        
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_name -> {
                currentSortBy = SortBy.NAME
                loadProducts()
                true
            }
            R.id.action_sort_code -> {
                currentSortBy = SortBy.CODE
                loadProducts()
                true
            }
            R.id.action_sort_quantity_asc -> {
                currentSortBy = SortBy.QUANTITY_ASC
                loadProducts()
                true
            }
            R.id.action_sort_quantity_desc -> {
                currentSortBy = SortBy.QUANTITY_DESC
                loadProducts()
                true
            }
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    enum class FilterType {
        ALL, IN_STOCK, LOW_STOCK, OUT_OF_STOCK
    }
    
    enum class SortBy {
        NAME, CODE, QUANTITY_ASC, QUANTITY_DESC
    }
}

/**
 * Ürün Listesi Adapter
 */
class ProductListAdapter : RecyclerView.Adapter<ProductListAdapter.ProductViewHolder>() {
    
    private var products: List<ProductEntity> = emptyList()
    
    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvProductCode: TextView = view.findViewById(R.id.tvProductCode)
        val tvProductName: TextView = view.findViewById(R.id.tvProductName)
        val tvQuantity: TextView = view.findViewById(R.id.tvQuantity)
        val tvLocation: TextView = view.findViewById(R.id.tvLocation)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
    }
    
    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ProductViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        
        holder.tvProductCode.text = product.productCode
        holder.tvProductName.text = product.name
        holder.tvQuantity.text = "${product.quantity} ${product.unit}"
        holder.tvLocation.text = product.getFullLocation()
        
        // Stok durumu
        when {
            product.isOutOfStock() -> {
                holder.tvStatus.text = "TÜKENDİ"
                holder.tvStatus.setBackgroundColor(android.graphics.Color.parseColor("#F44336"))
            }
            product.isLowStock() -> {
                holder.tvStatus.text = "DÜŞÜK STOK"
                holder.tvStatus.setBackgroundColor(android.graphics.Color.parseColor("#FF9800"))
            }
            else -> {
                holder.tvStatus.text = "STOKTA"
                holder.tvStatus.setBackgroundColor(android.graphics.Color.parseColor("#4CAF50"))
            }
        }
    }
    
    override fun getItemCount() = products.size
    
    fun submitList(newProducts: List<ProductEntity>) {
        val diffCallback = ProductDiffCallback(products, newProducts)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        
        products = newProducts
        diffResult.dispatchUpdatesTo(this)
    }
}

/**
 * DiffUtil Callback
 */
class ProductDiffCallback(
    private val oldList: List<ProductEntity>,
    private val newList: List<ProductEntity>
) : DiffUtil.Callback() {
    
    override fun getOldListSize() = oldList.size
    
    override fun getNewListSize() = newList.size
    
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }
    
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldProduct = oldList[oldItemPosition]
        val newProduct = newList[newItemPosition]
        return oldProduct.productCode == newProduct.productCode &&
                oldProduct.name == newProduct.name &&
                oldProduct.quantity == newProduct.quantity &&
                oldProduct.getFullLocation() == newProduct.getFullLocation()
    }
}
