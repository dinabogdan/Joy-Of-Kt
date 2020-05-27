package com.freesoft.joykt.chapter12

import com.freesoft.joykt.chapter5.List
import com.freesoft.joykt.chapter7.Result
import com.freesoft.joykt.chapter9.Stream

class ScriptReader : Input {

    private val commands: List<String>

    constructor(commands: List<String>) : super() {
        this.commands = commands
    }

    constructor(vararg commands: String) : super() {
        this.commands = List(*commands)
    }

    override fun close() {}

    override fun readString(): Result<Pair<String, Input>> = when {
        commands.isEmpty() -> Result.failure("Not enough entries in script")
        else -> Result(Pair(commands.headSafe().getOrElse(""), ScriptReader(commands.drop(1))))
    }

    override fun readInt(): Result<Pair<Int, Input>> = try {
        when {
            commands.isEmpty() -> Result.failure("Not enough entries in script")
            Integer.parseInt(commands.headSafe().getOrElse("")) >= 0 ->
                Result(Pair(Integer.parseInt(commands.headSafe().getOrElse("")), ScriptReader(commands.drop(1))))
            else -> Result()
        }
    } catch (ex: Exception) {
        Result.failure(ex)
    }
}

fun readPersonsFromScript(vararg commands: String): List<Person> =
        Stream.unfold(ScriptReader(*commands), ::person).toList()

fun main() {
    readPersonsFromScript(
            "1", "Mickey", "Mouse",
            "2", "Minnie", "Mouse",
            "3", "Donald", "Duck"
    ).forEach(::println)
}