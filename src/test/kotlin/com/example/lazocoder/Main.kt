package com.example.lazocoder

import java.util.*

/**
 * from https://github.com/LazoCoder/Particle-Swarm-Optimization/tree/master
 *
 */

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        if (args.size == 1 && args[0] == "-p") {
            menu(true)
        } else {
            print("Use the parameter '-p' to change the inertia, ")
            println("cognitive and social components.")
            println("Otherwise the default values will be: ")
            println("Inertia:             " + MySwarm.DEFAULT_INERTIA)
            println("Cognitive Component: " + MySwarm.DEFAULT_COGNITIVE)
            println("Social Component:    " + MySwarm.DEFAULT_SOCIAL)
            menu(false)
        }
    }

    private fun menu(flag: Boolean) {
        val swarm: MySwarm
        val inertia: Double
        val cognitive: Double
        val social: Double

        val function = function
        val particles = getUserInt("Particles: ")
        val epochs = getUserInt("Epochs:    ")

        if (flag) {
            inertia = getUserDouble("Inertia:   ")
            cognitive = getUserDouble("Cognitive: ")
            social = getUserDouble("Social:    ")
            swarm = MySwarm(function, particles, epochs, inertia, cognitive, social)
        } else {
            swarm = MySwarm(function, particles, epochs)
        }

        swarm.run()
    }

    private val function: MyParticle.FunctionType
        get() {
            var function: MyParticle.FunctionType? = null
            do {
                val sc: Scanner = Scanner(System.`in`)
                printMenu()

                if (sc.hasNextInt()) {
                    function = getFunction(sc.nextInt())
                } else {
                    println("Invalid input.")
                }
            } while (function == null)
            return function
        }

    private fun getUserInt(msg: String): Int {
        var input: Int
        while (true) {
            val sc: Scanner = Scanner(System.`in`)
            print(msg)

            if (sc.hasNextInt()) {
                input = sc.nextInt()

                if (input <= 0) {
                    println("Number must be positive.")
                } else {
                    break
                }
            } else {
                println("Invalid input.")
            }
        }
        return input
    }

    private fun getUserDouble(msg: String): Double {
        var input: Double
        while (true) {
            val sc: Scanner = Scanner(System.`in`)
            print(msg)

            if (sc.hasNextDouble()) {
                input = sc.nextDouble()

                if (input <= 0) {
                    println("Number must be positive.")
                } else {
                    break
                }
            } else {
                println("Invalid input.")
            }
        }
        return input
    }

    private fun printMenu() {
        println("----------------------------MENU----------------------------")
        println("Select a function:")
        println("1. (x^4)-2(x^3)")
        println("2. Ackley's Function")
        println("3. Booth's Function")
        println("4. Three Hump Camel Function")
        print("Function:  ")
    }

    private fun getFunction(input: Int): MyParticle.FunctionType? {
        if (input == 1) return MyParticle.FunctionType.FunctionA
        else if (input == 2) return MyParticle.FunctionType.Ackleys
        else if (input == 3) return MyParticle.FunctionType.Booths
        else if (input == 4) return MyParticle.FunctionType.ThreeHumpCamel
        println("Invalid Input.")
        return null
    }
}