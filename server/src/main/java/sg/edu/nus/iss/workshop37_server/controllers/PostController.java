package sg.edu.nus.iss.workshop37_server.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import sg.edu.nus.iss.workshop37_server.services.PostService;

@CrossOrigin(origins="*")
@RestController
@RequestMapping("/api/post")
public class PostController {

    @Autowired
    private PostService postSvc;

    // POST /api/post
    // Content-Type: multipart/form-data
    // Accept: application/json
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> post(@RequestPart MultipartFile fileOne, 
        @RequestPart MultipartFile fileTwo, @RequestPart String comment)
    {
        System.out.println(">>> in controller");
        Optional<String> postIdOpt = postSvc.postToMySQL(fileOne,comment);
        String objectId = postSvc.postToMongo(fileTwo,comment);

        System.out.println("back to controller");

        JsonObject payload = Json.createObjectBuilder()
                                    .add("postId", postIdOpt.get())
                                    .add("objectId", objectId)
                                    .build();

        return ResponseEntity.ok().body(payload.toString());
    }

    // GET/api/post/{sql}/{postId}
    // GET/api/post/{mongo}/{objectId}
    @GetMapping(path="/{db}/{id}")
    public ResponseEntity<String> getImage(@PathVariable String db, @PathVariable String id){
        
        String image;
        if(db.equals("sql")){
            image = postSvc.getImageSQL(id);
        }else{
            image = postSvc.getImageMongo(id);
        }

        JsonObject payload = Json.createObjectBuilder()
        .add("image", image)
        .build();

        return ResponseEntity.ok().body(payload.toString());
    }


    
}
