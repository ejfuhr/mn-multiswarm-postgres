package com.example.elizarov.continuationsPlus

import com.example.elizarov.myNiceAsyncCode
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ContinuationsTest {
    private val log: Logger = LoggerFactory.getLogger(ContinuationsTest::class.java)
    @Test
    @Disabled
    @Order(1)
    fun `as coroutineDispathcher at 41_41`() = runTest {
            myNiceAsyncCode()
        }
    }


