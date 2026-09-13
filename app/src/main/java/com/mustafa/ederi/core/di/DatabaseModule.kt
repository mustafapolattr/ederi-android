package com.mustafa.ederi.core.di

import android.content.Context
import androidx.room.Room
import com.mustafa.ederi.data.local.EderiDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideEderiDatabase(@ApplicationContext context: Context): EderiDatabase =
        Room.databaseBuilder(context, EderiDatabase::class.java, "ederi.db")
            .build()
}
