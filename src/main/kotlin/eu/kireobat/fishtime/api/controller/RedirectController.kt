package eu.kireobat.fishtime.api.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView

@Tag(name="redirect")
@RestController
class RedirectController {
    @Operation(hidden = true)
    @GetMapping(path = ["/", "/swagger-ui"])
    fun redirect(): RedirectView {
        return RedirectView("/fish-time/swagger-ui/index.html")
    }
}