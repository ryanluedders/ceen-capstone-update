package com.ryanluedders.capstone;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImagesourceEndpoint {
    
    private final Log log = LogFactory.getLog(getClass());
    
    private Map<String, List<File>> files = new HashMap<>();
    
    private Map<String, File> currentImage = new HashMap<>();
    
    @PostConstruct
    public void initialize() {
        File resources = new File("src/main/resources/lotimage/");
        
        if (resources.isDirectory()) {
            for (File collection : resources.listFiles()) {
                if (collection.isDirectory()) {
                    files.put(collection.getName(), new ArrayList<File>());
                    for (File image : collection.listFiles()) {
                        files.get(collection.getName()).add(image);
                    }
                }
            }
        }
        
        for (String s : files.keySet()) {
            currentImage.put(s, files.get(s).get(0));
        }
        
        log.info("loaded images: " + files.toString());
        log.info("current images: " + currentImage.toString());
        
    }
    
    @RequestMapping("/test")
    public String test() {
        return "HELLO WORLD";
    }
	
	@RequestMapping("/parkinglots/{lot}")
	public String currentImage(String lot) {
	    return lot;
	}
	
	
}
