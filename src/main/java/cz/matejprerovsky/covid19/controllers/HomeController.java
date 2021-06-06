package cz.matejprerovsky.covid19.controllers;

import cz.matejprerovsky.covid19.services.CovidDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    CovidDataService cds;

    @GetMapping("/")
    public String home(Model model){
        Map<String, String> map = cds.getData();
        model.addAllAttributes(map);

        return "home";
    }
}
