package br.com.fiap.fiapvideos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class ConfigController {

    @GetMapping("/swagger")
    public RedirectView redirectToSwagger() {
        return new RedirectView("/swagger-ui.html");
    }

    @GetMapping("/redoc")
    public RedirectView redirectRedoc() {
        return new RedirectView("/redoc.html");
    }

    @GetMapping("/health")
    public RedirectView health() {
        return new RedirectView("/actuator/health");
    }

}