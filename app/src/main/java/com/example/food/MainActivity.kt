package com.example.food

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MealAdapter
    private var randomMeals: List<Meal> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler_view)
        adapter = MealAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        // Add scroll listener to RecyclerView
        val randomFoodBtn: Button = findViewById(R.id.random_food_btn)
        randomFoodBtn.setOnClickListener {
            loadRandomImage()
        }

        fetchData()
    }

    private fun loadRandomImage() {
        if (randomMeals.isNotEmpty()) {
            val randomIndex = (0 until randomMeals.size).random()
            val randomMeal = randomMeals[randomIndex]
            val imgRandomMeal: ImageView = findViewById(R.id.img_random_meal)
            Picasso.get().load(randomMeal.strMealThumb).into(imgRandomMeal)
        }
    }
    private fun fetchData() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.themealdb.com/api/json/v1/1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(MealApiService::class.java)
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = service.getMealsByCategory("Dessert").execute()
                if (response.isSuccessful) {
                    val mealResponse = response.body()
                    val meals = mealResponse?.meals ?: emptyList()
                    withContext(Dispatchers.Main) {
                        adapter.setMeals(meals)
                        randomMeals = meals
                        // Load random image initially
                        loadRandomImage()
                    }
                } else {
                    // Handle unsuccessful response
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle exception
            }
        }
    }

    inner class MealAdapter : RecyclerView.Adapter<MealViewHolder>() {

        private var meals: List<Meal> = emptyList()

        fun setMeals(meals: List<Meal>) {
            this.meals = meals
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.category_cell, parent, false)
            return MealViewHolder(view)
        }

        override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
            val meal = meals[position]
            holder.bind(meal)
        }

        override fun getItemCount(): Int {
            return meals.size
        }
    }

    inner class MealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mealNameTextView: TextView = itemView.findViewById(R.id.mealNameTextView)
        private val mealImageView: ImageView = itemView.findViewById(R.id.mealImageView)
        private val mealIdTextView: TextView = itemView.findViewById(R.id.mealIdTextView)

        fun bind(meal: Meal) {
            mealNameTextView.text = meal.strMeal
            mealIdTextView.text = meal.idMeal

            // Load image using Picasso library
            Picasso.get().load(meal.strMealThumb).into(mealImageView)
        }
    }
}

