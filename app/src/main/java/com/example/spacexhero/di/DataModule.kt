package com.example.spacexhero.di

import android.content.Context
import com.example.spacexhero.data.network.SpaceXApiService
import com.example.spacexhero.data.repository.LaunchRepositoryImpl
import com.example.spacexhero.data.source.RemoteLaunchDataSource
import com.example.spacexhero.data.source.cache.LaunchCacheManager
import com.example.spacexhero.domain.repository.LaunchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun provideLaunchCacheManager(
        @ApplicationContext context: Context
    ): LaunchCacheManager {
        return LaunchCacheManager(context)
    }

    @Singleton
    @Provides
    fun provideLaunchRepository(
        remoteDataSource: RemoteLaunchDataSource,
        cacheManager: LaunchCacheManager
    ): LaunchRepository {
        return LaunchRepositoryImpl(remoteDataSource, cacheManager)
    }

    // --- Network providers ---
    @Singleton
    @Provides
    fun provideJson(): Json = Json { ignoreUnknownKeys = true }

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(json: Json, okHttpClient: OkHttpClient): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl("https://ll.thespacedevs.com/") // todo extract to config
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Singleton
    @Provides
    fun provideSpaceXApiService(retrofit: Retrofit): SpaceXApiService {
        return retrofit.create(SpaceXApiService::class.java)
    }
}

