package com.example.multiswarm

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * Test for [Multiswarm].
 *
 * @author Donato Rimenti
 */
class MultiswarmUnitTest {
    /**
     * Rule for handling expected failures. We use this since this test may
     * actually fail due to bad luck in the random generation.
     */
/*    @Rule
    var mayFailRule: MayFailRule = MayFailRule()*/

    /**
     * Tests the multiswarm algorithm with a generic problem. The problem is the
     * following: <br></br>
     * <br></br>
     * In League of Legends, a player's Effective Health when defending against
     * physical damage is given by E=H(100+A)/100, where H is health and A is
     * armor. Health costs 2.5 gold per unit, and Armor costs 18 gold per unit.
     * You have 3600 gold, and you need to optimize the effectiveness E of your
     * health and armor to survive as long as possible against the enemy team's
     * attacks. How much of each should you buy? <br></br>
     * <br></br>
     * The solution is H = 1080, A = 50 for a total fitness of 1620. Tested with
     * 50 swarms each with 1000 particles.
     */
    @Test
    fun givenMultiswarm_whenThousandIteration_thenSolutionFound() {
        val multiswarm = Multiswarm(50, 1000, LolFitnessFunction())

        // Iterates 1000 times through the main loop and prints the result.
        for (i in 0..999) {
            multiswarm.mainLoop()
            if(i <= 10  || i > 995){
                fitnessFound(multiswarm)
            }
        }

        println(
            ("Best fitness found: " + multiswarm.bestFitness + "[" + multiswarm.bestPosition[0]
                    + "," + multiswarm.bestPosition[1] + "]")
        )
        assertEquals(1080, multiswarm.bestPosition[0])
        assertEquals(50, multiswarm.bestPosition[1])
        assertEquals(1620, multiswarm.bestFitness.toInt())
    }

    private fun fitnessFound(multiswarm: Multiswarm) {
        println(
            ("current fitness found: " + multiswarm.bestFitness + "[" + multiswarm.bestPosition[0]
                    + "," + multiswarm.bestPosition[1] + "]")
        )
    }
}