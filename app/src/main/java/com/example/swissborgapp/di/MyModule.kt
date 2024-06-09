package com.example.swissborgapp.di

import com.example.swissborgapp.data.NetworkConnectivityObserver
import com.example.swissborgapp.data.repository.BitfinexRepositoryImpl
import com.example.swissborgapp.domain.ConnectivityObserver
import com.example.swissborgapp.domain.repository.BitfinexRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface MyModule {

    @Binds
    fun bindBitfinexRepository(impl: BitfinexRepositoryImpl): BitfinexRepository

    @Binds
    fun bindConnectivityObserver(impl: NetworkConnectivityObserver): ConnectivityObserver
}