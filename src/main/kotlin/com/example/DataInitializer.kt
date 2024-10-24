package com.example

import com.example.news.*
import io.micronaut.context.annotation.Requires
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.runtime.server.event.ServerStartupEvent
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory


@Singleton
//@Requires(env = arrayOf("test"))
@Requires(notEnv = arrayOf("mock"))
class DataInitializer {

    @Inject
    lateinit var newsRepo: MyNewsRepo
    @Inject
    lateinit var monsterRepo: MyMonstersRepo
    @Inject
    lateinit var newsInitService: MyNewsInitService

    private val log = LoggerFactory.getLogger(DataInitializer::class.java)

    @EventListener
    fun onStart(event: ServerStartupEvent) {
        log.info("in initializer")
        val serviceFlow: Flow<MyNews> = newsInitService.initNews()
        val monsterFlow:Flow<MyMonster> = newsInitService.initMonsters()

        runBlocking {
            serviceFlow
                .toList()
                .forEach {
                    newsRepo.save(it)
                println(it.id)
                }
            var monsterNo = 0
            monsterFlow
                .toList()
                .forEach {
                    monsterNo++
                    monsterRepo.save(it)
                }
            println("monstersSaved $monsterNo")
        }
    }

}