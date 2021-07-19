package  com.frogobox.wallpaper.mvvm.detail

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.frogobox.wallpaper.R
import com.frogobox.wallpaper.core.BaseActivity
import com.frogobox.wallpaper.databinding.ActivityFanartDetailBinding
import com.frogobox.wallpaper.model.Favorite
import com.frogobox.wallpaper.model.Wallpaper
import com.frogobox.wallpaper.source.callback.DeleteViewCallback
import com.frogobox.wallpaper.source.callback.SaveViewCallback
import com.frogobox.wallpaper.util.helper.ConstHelper.Extra.EXTRA_FANART
import com.frogobox.wallpaper.util.helper.ConstHelper.Extra.EXTRA_FAV_FANART
import com.frogobox.wallpaper.util.helper.WallpaperHelper.Wallpaper.setHomeLockWallpaper

class FanartDetailActivity : BaseActivity<ActivityFanartDetailBinding>(), SaveViewCallback,
    DeleteViewCallback {

    private lateinit var mViewModel: DetailViewModel
    private lateinit var extraFavorite: Favorite
    private lateinit var extraWallpaper: Wallpaper

    private var isFav = false
    private var menuItem: Menu? = null

    override fun setupViewBinding(): ActivityFanartDetailBinding {
        return ActivityFanartDetailBinding.inflate(layoutInflater)
    }

    override fun setupViewModel() {
        mViewModel = obtainDetailMovieViewModel().apply {

            favorite.observe(this@FanartDetailActivity, Observer {

            })

            eventIsFavorite.observe(this@FanartDetailActivity, Observer {
                setFavorite(it)
                isFav = it
            })

        }
    }

    override fun setupUI(savedInstanceState: Bundle?) {
        setupDetailActivity("")
        setupShowAdsBanner(binding.ads.adsPhoneTabSpecialSmartBanner)
        setupExtraData()
    }

    private fun obtainDetailMovieViewModel(): DetailViewModel =
        obtainViewModel(DetailViewModel::class.java)

    private fun stateExtra(listenerMovie: () -> Unit, listenerFavMovie: () -> Unit) {
        if (checkExtra(EXTRA_FANART)) {
            listenerMovie()
        } else if (checkExtra(EXTRA_FAV_FANART)) {
            listenerFavMovie()
        }
    }

    private fun setupButtonWallpaper(linkImage: String) {
        binding.apply {
            btnSetWallpaper.setOnClickListener {
                progressCircular.visibility = View.VISIBLE
                if (setHomeLockWallpaper(this@FanartDetailActivity, linkImage)) {
                    progressCircular.visibility = View.GONE
                    showToast(resources.getString(R.string.text_succes_applied_home_screen))
                } else {
                    progressCircular.visibility = View.GONE
                    showToast(resources.getString(R.string.text_failed_applied_home_screen))
                }
            }
        }
    }


    private fun setupExtraData() {
        stateExtra({
            extraWallpaper = baseGetExtraData(EXTRA_FANART)
            extraWallpaper.linkImage?.let { setupImageView(it) }
            extraWallpaper.linkImage?.let { setupButtonWallpaper(it) }
            extraWallpaper.id.let { mViewModel.getFavorite(it) }
        }) {
            extraFavorite = baseGetExtraData(EXTRA_FAV_FANART)
            extraFavorite.linkImage?.let { setupImageView(it) }
            extraFavorite.linkImage?.let { setupButtonWallpaper(it) }
            extraFavorite.id.let { mViewModel.getFavorite(it) }
        }
    }

    private fun setFavorite(state: Boolean) {
        if (state)
            menuItem?.getItem(1)?.icon = getDrawable(R.drawable.ic_toolbar_favorite)
        else
            menuItem?.getItem(1)?.icon = getDrawable(R.drawable.ic_toolbar_un_favorite)
    }

    private fun setupImageView(linkImage: String) {
        Glide.with(this).load(linkImage).into(binding.ivPoster)
    }

    private fun setupIntentData() {
        val image = intent.getStringExtra(EXTRA_FANART)
        startActivity(
            Intent(
                this,
                FanartSourceActivity::class.java
            ).putExtra(EXTRA_FANART, image)
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_detail, menu)
        menuItem = menu
        setFavorite(isFav)
        return true
    }

    private fun handleFavorite() {
        if (isFav) {
            stateExtra({
                extraWallpaper.id.let { mViewModel.deleteFavorite(it, this) }
                extraWallpaper.id.let { mViewModel.getFavorite(it) }
            }) {
                extraFavorite.id.let { mViewModel.deleteFavorite(it, this) }
                extraFavorite.id.let { mViewModel.getFavorite(it) }
            }
        } else {
            stateExtra({
                mViewModel.saveFavorite(
                    Favorite(
                        id = extraWallpaper.id,
                        linkImage = extraWallpaper.linkImage
                    ), this
                )
                extraWallpaper.id.let { mViewModel.getFavorite(it) }
            }) {
                mViewModel.saveFavorite(
                    Favorite(
                        id = extraFavorite.id,
                        linkImage = extraFavorite.linkImage
                    ), this
                )
                extraFavorite.id.let { mViewModel.getFavorite(it) }
            }

        }
        setFavorite(isFav)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.toolbar_menu_copyright -> {
                setupIntentData()
                true
            }
            R.id.toolbar_menu_favorite -> {
                handleFavorite()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onShowProgress() {}

    override fun onHideProgress() {}

    override fun onSuccesInsert() {
        showToast(getString(R.string.text_succes_add_favorite))
    }

    override fun onSuccesDelete() {
        showToast(getString(R.string.text_succes_delete_favorite))
    }

    override fun onFailed(message: String) {}


}
