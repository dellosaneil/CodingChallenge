package com.example.codingchallenge.fragments.homePage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.codingchallenge.R
import com.example.codingchallenge.databinding.ListItemHomepageBinding
import com.example.codingchallenge.room.AppleEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async

class HomePageAdapter(private val homePageListener : HomePageAdapter.HomePageClickListener) : RecyclerView.Adapter<HomePageAdapter.HomePageViewHolder>() {

    private var movieData = listOf<AppleEntity>()

    fun insertMovieData(newMovies: List<AppleEntity>) {
        val oldMovies = movieData
        val diffResult = DiffUtil.calculateDiff(
            HomePageDiffUtil(oldMovies, newMovies)
        )
        movieData = newMovies
        diffResult.dispatchUpdatesTo(this)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePageViewHolder {
        val binding = ListItemHomepageBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return HomePageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomePageViewHolder, position: Int) {
        val appleData = movieData[position]
        holder.bind(appleData)
    }

    override fun getItemCount() = movieData.size

    private class HomePageDiffUtil(
        private val oldList: List<AppleEntity>,
        private val newList: List<AppleEntity>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition].timeInserted == newList[newItemPosition].timeInserted

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition] == newList[newItemPosition]
    }


    inner class HomePageViewHolder(private val binding: ListItemHomepageBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init{
            binding.homePageRVContainer.setOnClickListener(this)
        }

        fun bind(appleData: AppleEntity) {
            binding.homePageRVGenre.text = appleData.genre
            binding.homePageRVPrice.text = binding.root.resources.getString(
                R.string.homePageAdapter_price,
                appleData.price.toString()
            )
            binding.homePageRVTitle.text = appleData.trackName
            Glide.with(binding.root.context)
                .load(appleData.artWork)
                .into(binding.homePageRVImage)
        }

        override fun onClick(v: View?) {
            homePageListener.pageClicked(adapterPosition)
        }
    }

    interface HomePageClickListener{
        fun pageClicked(position : Int)
    }
}










