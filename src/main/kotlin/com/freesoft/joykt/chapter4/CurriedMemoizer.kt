package com.freesoft.joykt.chapter4

import kotlin.system.measureTimeMillis

val mhc = Memoizer.memoize { x: Int ->
    Memoizer.memoize { y: Int ->
        x + y
    }
}

val f3 = { x: Int ->
    { y: Int ->
        { z: Int ->
            x + y + z
        }
    }
}

val f3m = Memoizer.memoize { x: Int ->
    Memoizer.memoize { y: Int ->
        Memoizer.memoize { z: Int -> x + y - z }
    }
}

val f3mLongComputation = Memoizer.memoize { x: Int ->
    Memoizer.memoize { y: Int ->
        Memoizer.memoize { z: Int ->
            longComputation(z) - (longComputation(y) + longComputation(x))
        }
    }
}

fun main() {
    var result1 = 0
    var result2 = 0
    val time1 = measureTimeMillis {
        result1 = f3mLongComputation(41)(42)(43)
    }
    val time2 = measureTimeMillis {
        result2 = f3mLongComputation(41)(42)(43)
    }

    println("First call to memoized function: result = $result1, time = $time1")
    println("Second call to memoized function: result = $result2, time = $time2")


}