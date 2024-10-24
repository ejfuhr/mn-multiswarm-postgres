package com.reactive.heartMicroservice.model

import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity


@MappedEntity("kt_camp_site")
//@Table(name = "kt_camp_site")
data class Campsite(
    @Id
    @GeneratedValue
    var id: Long? = null,
    //var camperId: Long,
    val name: String,
    val highway: String?
)
//, var campers: List<Camper>)
