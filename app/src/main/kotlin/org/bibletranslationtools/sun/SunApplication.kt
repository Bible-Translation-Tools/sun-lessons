package org.bibletranslationtools.sun

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.util.DebugLogger
import org.bibletranslationtools.sun.di.initKoin
import org.koin.android.ext.koin.androidContext

class SunApplication : Application(), SingletonImageLoader.Factory {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(applicationContext)
        }
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(this, 0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("images"))
                    .maxSizeBytes(512 * 1024 * 1024)
                    .build()
            }
            .logger(DebugLogger())
            .build()
    }
}