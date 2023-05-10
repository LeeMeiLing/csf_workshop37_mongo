package sg.edu.nus.iss.workshop37_server.services;

import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import sg.edu.nus.iss.workshop37_server.models.Post;
import sg.edu.nus.iss.workshop37_server.repositories.PostRepository;

@Service
public class PostService {

    private static final String BASE64_PREFIX = "data:image/png;base64,";

    @Autowired
    private PostRepository postRepo;
    
    public Optional<String> postToMySQL(MultipartFile fileOne, String comment){

        Optional<String> postIdOpt = postRepo.postToMySQL(fileOne, comment);

        if( postIdOpt.isPresent()){
            System.out.println(" successfully insert to Mysql: " + postIdOpt.get());
        }
        // return postId
        return postIdOpt;
    }

    public String postToMongo(MultipartFile fileTwo, String comment){

        String objectId = postRepo.postToMongo(fileTwo, comment);
        System.out.println("Object id : " + objectId);
        // return ObjectId
        return objectId;

    }

    public String getImageSQL(String postId){

        Post post = postRepo.getImageSQL(postId);

        String encodedString = BASE64_PREFIX +  Base64.getEncoder().encodeToString(post.getPicture());
        return encodedString;

    }

    public String getImageMongo(String objectId){

        return postRepo.getImageMongo(objectId);

    }
}
