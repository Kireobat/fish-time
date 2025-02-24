package eu.kireobat.fishtime

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FishTimeApplication

fun main(args: Array<String>) {
    runApplication<FishTimeApplication>(*args)
}
