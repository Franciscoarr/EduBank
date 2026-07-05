package com.example.edubank.core.di

import com.example.edubank.data.repository.AuthRepositoryImpl
import com.example.edubank.data.repository.StudentRepositoryImpl
import com.example.edubank.domain.repository.AuthRepository
import com.example.edubank.domain.repository.StudentRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindStudentRepository(
        studentRepositoryImpl: StudentRepositoryImpl
    ): StudentRepository
}