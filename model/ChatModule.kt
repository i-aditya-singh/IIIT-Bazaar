package com.example.iiitbazaar.model

import com.example.iiitbazaar.ChatRepo.ChatRepository
import com.example.iiitbazaar.ChatRepo.ChatRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ChatModule {

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        impl: ChatRepositoryImpl
    ): ChatRepository
}
