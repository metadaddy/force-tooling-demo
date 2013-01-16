package com.example.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.ryanbrainard.richsobjects.RichSObjectsService;
import com.github.ryanbrainard.richsobjects.RichSObjectsServiceImpl;


@Controller
@RequestMapping("/sobjects")
public class SObjectsController {

    private RichSObjectsService salesforceService = new RichSObjectsServiceImpl();

    @RequestMapping("")
    public String listSObjects(Map<String, Object> map) {
     	map.put("types", salesforceService.types());
    	return "sobjects";
    }

    @RequestMapping("/{type}")
    public String getSObjectDetail(@PathVariable("type") String type, Map<String, Object> map) {
        map.put("detail", salesforceService.describe(type));
        return "sobjectDetail";
    }  
}
