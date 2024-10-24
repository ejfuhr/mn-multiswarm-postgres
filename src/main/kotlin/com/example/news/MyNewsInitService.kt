package com.example.news

import jakarta.inject.Singleton
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Singleton
class MyNewsInitService(
    var newsRepo: MyNewsRepo,
    var monsterRepo: MyMonstersRepo
) {

    val newsList = List(100) { MyNews(null,"Title $it", "Description $it", "ImageUrl $it", "Url $it") }
    var fetchNewsStartDelay = 0L
    var fetchNewsDelay = 0L
    val failWith = mutableListOf<Throwable>()

    val monsterList = List(5){
        MyMonster(null, "abominalable $it", "$it's ugly", MonsterSize.MEDIUM)

    }

    fun initNews(): Flow<MyNews> = flow {
        if (newsRepo.count()>1 ){
            dropAllNews()
        }
        if(monsterRepo.count()>1){
            dropAllMonsters()
        }
        delay(fetchNewsStartDelay)
        failWith.removeFirstOrNull()?.let { throw it }
        newsList.forEach {
            delay(fetchNewsDelay)
            emit(it)
        }
    }

    fun initMonsters():Flow<MyMonster> = flow {
        monsterList.forEach {
            emit(it)
        }
    }

    suspend fun dropAllNews(){
        newsRepo.deleteAll()
    }
    suspend fun dropAllMonsters(){
        monsterRepo.deleteAll()
    }
}