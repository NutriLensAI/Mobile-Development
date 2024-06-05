package com.capstone.mobiledevelopment.nutrilens.view.hasil

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.mobiledevelopment.nutrilens.R
import com.capstone.mobiledevelopment.nutrilens.view.adapter.macros.Makanan
import com.capstone.mobiledevelopment.nutrilens.view.adapter.macros.MakananAdapter
import com.capstone.mobiledevelopment.nutrilens.view.catatan.CatatanMakanan

class HasilMakanan : AppCompatActivity() {

    private lateinit var makananList: List<Makanan>
    private lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hasil_makanan)

        imageUri = intent.getStringExtra("image_uri")?.let { Uri.parse(it) } ?: Uri.EMPTY
        val imageView: ImageView = findViewById(R.id.img_makanan)

        imageUri?.let {
            Glide.with(this)
                .load(it)
                .into(imageView)
        }

        val mealTimeLayout: LinearLayout = findViewById(R.id.meal_time_layout)
        val addButton: ImageView = findViewById(R.id.btn_add)
        addButton.setOnClickListener {
            if (mealTimeLayout.visibility == LinearLayout.GONE) {
                mealTimeLayout.visibility = LinearLayout.VISIBLE
            } else {
                mealTimeLayout.visibility = LinearLayout.GONE
            }
        }

        val breakfastButton: ImageButton = findViewById(R.id.breakfast_button)
        val lunchButton: ImageButton = findViewById(R.id.lunch_button)
        val dinnerButton: ImageButton = findViewById(R.id.dinner_button)

        val makanan = Makanan("Bakso Urat", 450, 14, 14, 14, "60%", "14%", "26%")

        breakfastButton.setOnClickListener { sendMealData("breakfast", makanan) }
        lunchButton.setOnClickListener { sendMealData("lunch", makanan) }
        dinnerButton.setOnClickListener { sendMealData("dinner", makanan) }

        val recyclerView = findViewById<RecyclerView>(R.id.rv_makanan)
        recyclerView.layoutManager = LinearLayoutManager(this)
        makananList = getMakananList()
        recyclerView.adapter = MakananAdapter(makananList)

        val viewIngredientsButton: Button = findViewById(R.id.btn_view_ingredients)
        viewIngredientsButton.setOnClickListener { viewIngredients() }
    }

    private fun sendMealData(mealType: String, makanan: Makanan) {
        val intent = Intent(this, CatatanMakanan::class.java).apply {
            putExtra("meal_type", mealType)
            putExtra("nama_makanan", makanan.nama)
            putExtra("calories", makanan.calories)
            putExtra("carbs", makanan.carbs)
            putExtra("fat", makanan.fat)
            putExtra("protein", makanan.protein)
        }
        startActivity(intent)
    }

    private fun getMakananList(): List<Makanan> {
        // Buat data dummy untuk sementara
        return listOf(
            Makanan("Bakso Urat", 450, 14, 14, 14, "60%", "14%", "26%")
            // Tambahkan data lainnya
        )
    }

    private fun viewIngredients() {
        val intent = Intent(this, Ingredients::class.java).apply {
            putExtra("image_uri", imageUri.toString())
            putParcelableArrayListExtra("makanan_list", ArrayList(makananList))
        }
        startActivity(intent)
    }
}
