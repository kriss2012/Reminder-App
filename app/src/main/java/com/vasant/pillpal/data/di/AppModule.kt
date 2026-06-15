package com.vasant.pillpal.data.di

import android.content.Context
import androidx.room.Room
import com.vasant.pillpal.data.db.MedicineDatabase
import com.vasant.pillpal.data.db.dao.MedicineDao
import com.vasant.pillpal.repository.Auth
import com.vasant.pillpal.repository.AuthImplementation
import com.vasant.pillpal.repository.MedicineRepo
import com.vasant.pillpal.repository.MedicineRepoImplementation
import com.vasant.pillpal.data.api.DoseFlowApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideMedicineDataBase(@ApplicationContext context: Context): MedicineDatabase {
        return Room.databaseBuilder(
            context = context, MedicineDatabase::class.java,
            name = "medicine_db"
        ).fallbackToDestructiveMigration(true).build()
    }

    @Singleton
    @Provides
    fun provideMedicineDao(db: MedicineDatabase): MedicineDao {
        return db.medicineDao()
    }

    @Singleton
    @Provides
    fun provideMedicineRepo(
        dao: MedicineDao,
        apiService: DoseFlowApiService,
        @ApplicationContext context: Context
    ): MedicineRepo {
        return MedicineRepoImplementation(
            dao = dao,
            apiService = apiService,
            context = context
        )
    }

    @Singleton
    @Provides
    fun provideDoseFlowApiService(): DoseFlowApiService {
        return Retrofit.Builder()
            .baseUrl(com.vasant.pillpal.APPCONSTANTS.BACKEND_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DoseFlowApiService::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object BackendAuthModule {
    @Singleton
    @Provides
    fun provideAuthImpl(
        apiService: DoseFlowApiService,
        @ApplicationContext context: Context
    ): Auth {
        return AuthImplementation(apiService, context)
    }
}