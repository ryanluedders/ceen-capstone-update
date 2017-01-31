package com.ryanluedders.capstone;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImagesourceEndpoint {
    
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public class ImageNotFoundException extends RuntimeException {
        private static final long serialVersionUID = 1L; 
    }
    
    private final Log log = LogFactory.getLog(getClass());
    
    private Map<String, List<File>> files = new HashMap<>();
    
    private Map<String, Integer> currentImage = new HashMap<>();
    
    @Scheduled(fixedDelayString = "${com.ryanluedders.capstone.image.duration.ms}")
    public void periodicRefresh() {
        for (String k : currentImage.keySet()) {
            currentImage.put(k, ((currentImage.get(k) + 1) % files.get(k).size()));
        }
    }
    
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
            currentImage.put(s, 0);
        }
        
        log.info("loaded images: " + files.toString());
        log.info("current images: " + currentImage.toString());
    }
	
	@RequestMapping(value = "/parkinglots/{lot}", produces = MediaType.IMAGE_JPEG_VALUE)
	public byte[] currentImage(@PathVariable("lot") String lot) throws IOException {
	    if (currentImage.containsKey(lot)) {
	        InputStream is = new FileInputStream(files.get(lot).get(currentImage.get(lot)));
	        return IOUtils.toByteArray(is);
	    }
	    
	    throw new ImageNotFoundException();
	}
	
	
}
