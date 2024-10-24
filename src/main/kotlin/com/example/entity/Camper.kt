package com.reactive.heartMicroservice.model

import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity


@MappedEntity("kt_camper")
//@Table(name = "kt_camper")
data class Camper(
    @Id
    @GeneratedValue
    var id: Long? = null,
    var campSiteId: Int? = null,
    val name: String,
    val number:String?
)
