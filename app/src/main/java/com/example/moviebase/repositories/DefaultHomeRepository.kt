package com.example.moviebase.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.moviebase.db.MovieDao
import com.example.moviebase.db.MovieDatabase
import com.example.moviebase.model.Movie
import com.example.moviebase.model.MovieList
import com.example.moviebase.model.TrendingActorDetails
import com.example.moviebase.model.TrendingActorResults
import com.example.moviebase.retrofit.MovieAPI
import com.example.moviebase.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

class DefaultHomeRepository(private val dao: MovieDao, private val api: MovieAPI)
    :HomeRepository {

    var trendingMovieLiveData = MutableLiveData<Movie>()
    var trendingActorLiveData = MutableLiveData<TrendingActorDetails>()
    var popularMovieLiveData = MutableLiveData<List<Movie>>()
    var searchedMoviesLiveData = MutableLiveData<List<Movie>>()
    var savedMovieLiveData = dao.getAllSavedMovies()

    override suspend fun getTrendingMovie() {
        api.getTrendingMovie().enqueue(object: Callback<MovieList> {
            override fun onResponse(call: Call<MovieList>, response: Response<MovieList>) {
                if (response.body() != null) {
                    val listSize = response.body()!!.results.size
                    val randomIndex = Random.nextInt(0, listSize)
                    val randomTrendingMovie = response.body()!!.results[randomIndex]
                    trendingMovieLiveData.value = randomTrendingMovie
                } else {
                    Log.d("HomeViewModel","Response body is null")
                }
            }

            override fun onFailure(call: Call<MovieList>, t: Throwable) {
                Log.e("HomeViewModel: Trending Movie Error", t.message.toString())
            }
        })
    }

    override suspend fun searchMovie(query: String) {
        api.searchMovie(query).enqueue(object: Callback<MovieList> {
            override fun onResponse(call: Call<MovieList>, response: Response<MovieList>) {
                if (response.body() != null) {
                    searchedMoviesLiveData.value = response.body()!!.results
                } else {
                    Log.d("HomeViewModel","Response body is null")
                }
            }

            override fun onFailure(call: Call<MovieList>, t: Throwable) {
                Log.d("HomeViewModel: Searched Movies", t.message.toString())
            }

        })
    }

    override suspend fun getPopularMoviesByCategory(genre: String) {
        api.getPopularMovieByGenre(false, "en", 1, "popularity.desc", genre)
            .enqueue(object: Callback<MovieList> {
                override fun onResponse(call: Call<MovieList>, response: Response<MovieList>) {
                    if (response.body() != null) {
                        popularMovieLiveData.value = response.body()!!.results
                    } else {
                        Log.d("HomeViewModel","Response body is null")
                    }
                }

                override fun onFailure(call: Call<MovieList>, t: Throwable) {
                    Log.e("HomeViewModel: Popular Movie Error", t.message.toString())
                }
            })
    }

    override suspend fun getTrendingActor() {
        api.getTrendingActor().enqueue(object: Callback<TrendingActorResults> {
            override fun onResponse(call: Call<TrendingActorResults>, response: Response<TrendingActorResults>) {
                if (response.body() != null) {
                    val listSize = response.body()!!.results.size
                    val randomIndex = Random.nextInt(0, listSize)
                    val randomTrendingActor = response.body()!!.results[randomIndex]
                    trendingActorLiveData.value = randomTrendingActor
                } else {
                    Log.d("HomeViewModel","Response body is null")
                }
            }

            override fun onFailure(call: Call<TrendingActorResults>, t: Throwable) {
                Log.e("HomeViewModel: Trending Actor Error", t.message.toString())
            }
        })
    }

    override  suspend fun insertMovie(movie: Movie) {
        dao.upsertMovie(movie)
    }

    override  suspend fun deleteMovie(movie: Movie) {
        dao.deleteMovie(movie)
    }
}