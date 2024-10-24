package com.example.repository

import com.reactive.heartMicroservice.model.Camper
import com.reactive.heartMicroservice.model.Campsite
import io.micronaut.data.annotation.Query
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository
import kotlinx.coroutines.flow.Flow

@R2dbcRepository(dialect = Dialect.POSTGRES)
interface CamperCoCrudRepository : CoroutineCrudRepository<Camper, Long> {
    //@Query("select p.* from kt_camper p where p.name = :name ")
    suspend fun findByName(name: String): Flow<Camper>

}
@R2dbcRepository(dialect = Dialect.POSTGRES)
interface CampsiteCoCrudRepository : CoroutineCrudRepository<Campsite, Long> {

}