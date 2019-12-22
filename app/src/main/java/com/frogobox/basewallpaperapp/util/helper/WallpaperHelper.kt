package com.frogobox.basewallpaperapp.util.helper

import android.app.WallpaperManager
import android.content.Context
import android.os.Build
import android.os.StrictMode
import androidx.annotation.RequiresApi
import java.io.IOException
import java.net.URL

/**
 * Created by Faisal Amir
 * FrogoBox Inc License
 * =========================================
 * BaseWallpaperApp
 * Copyright (C) 22/12/2019.
 * All rights reserved
 * -----------------------------------------
 * Name     : Muhammad Faisal Amir
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * LinkedIn : linkedin.com/in/faisalamircs
 * -----------------------------------------
 * FrogoBox Software Industries
 * com.frogobox.basewallpaperapp.util.helper
 *
 */
class WallpaperHelper {

    object Wallpaper {

        fun setHomeWallpaper(context: Context, linkImage: String) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            val wallpaperManager = WallpaperManager.getInstance(context)
            try {
                val ins = URL(linkImage).openStream()
                wallpaperManager.setStream(ins)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        @RequiresApi(Build.VERSION_CODES.N)
        fun setLockScreenWallpaper(context: Context, linkImage: String) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            val wallpaperManager = WallpaperManager.getInstance(context)
            try {
                val ins = URL(linkImage).openStream()
                wallpaperManager.setStream(ins, null, true, WallpaperManager.FLAG_LOCK)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    }

}