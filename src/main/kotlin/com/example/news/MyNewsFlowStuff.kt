package com.example.news

import io.micronaut.data.annotation.*
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*

open class MyNewsViewModelFlow(
    newsRepository: MyNewsRepo
) {
    private val _progressVisible = MutableStateFlow(false)
    val progressVisible = _progressVisible.asStateFlow()

    private val _newsToShow = MutableSharedFlow<MyNews>()
    //val newsToShow = _newsToShow.asSharedFlow()


    private val _errors = MutableSharedFlow<Throwable>()

    protected val viewModelScope = CoroutineScope(
        Dispatchers.Main.immediate + SupervisorJob()
    )
    val errors = _errors.asSharedFlow()

    val newsToShow = newsRepository.findAll()
        .retry { error -> error is MyNo2ApiException }
        .catch { error -> _errors.emit(error) }
        .onStart { _progressVisible.value = true }
        .onCompletion { _progressVisible.value = false }
        .shareIn(viewModelScope, SharingStarted.Eagerly)

    val totalMonsters = newsRepository.findByTitleContains("o")

}

class MyNo2ApiException : Exception()

//@Repository
@R2dbcRepository(dialect = Dialect.POSTGRES)
interface MyNewsRepo : CoroutineCrudRepository<MyNews, Long> {
    fun findByTitle(title:String): Flow<MyNews>
    fun findByTitleContains(title:String):Flow<MyNews>
    @Join("featuredMonster")
    fun findByFeaturedMonsterName(feturedMonster:String):Flow<MyNews>

    fun countByFeaturedMonsterName(name:String):Flow<Long>
}

@R2dbcRepository(dialect = Dialect.POSTGRES)
interface MyMonstersRepo:CoroutineCrudRepository<MyMonster, Long>{


}

@MappedEntity
data class MyNews(
    @Id
    @GeneratedValue
    var id: Long? = null,
    val title: String,
    val description: String,
    val imageUrl: String?=null,
    val url: String? = "https://xyz.com",
    val featuredMonster:MyMonster?=null,
)

@MappedEntity
data class MyMonster(
    @Id
    @GeneratedValue
    var id: Long? = null,
    val name: String,
    val description: String,
    val size: MonsterSize? = MonsterSize.MEDIUM,

)

enum class MonsterSize{
    SMALL, MEDIUM, LARGE
}
