package sg.edu.nus.iss.workshop37_server.repositories;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import sg.edu.nus.iss.workshop37_server.models.Post;

@Repository
public class PostRepository {

    private static final String BASE64_PREFIX = "data:image/png;base64,";
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    private final static String INSERT_FILE_SQL = "insert into posts (post_id, comments, picture) values( ? , ? , ?)";
    private final static String GET_FILE_SQL = "select post_id, comments, picture from posts where post_id = ?";


    public Optional<String> postToMySQL(MultipartFile file, String comment){

        String postId = UUID.randomUUID().toString().substring(0, 8);

        try{
            int row = jdbcTemplate.update(INSERT_FILE_SQL, new PreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement ps) throws SQLException {
                    ps.setString(1, postId);
                    ps.setString(2, comment);

                    try {
                        ps.setBinaryStream(3, file.getInputStream(), file.getSize());
                    } catch (IOException e) {
                        System.out.println(">> catch IOException");
                        e.printStackTrace();
                    }
        
                }
                
            });

            if (row > 0){
                return Optional.of(postId);
            }else{
                System.out.println(" >> in else, row not > 0");
                return Optional.empty();
            }

        }catch(Exception ex){
            
            System.out.println(">> catch Exception");
            ex.printStackTrace();
        }

        return Optional.empty();
        
    }

    public String postToMongo(MultipartFile file, String comment){

        Document toInsert = new Document();
        toInsert.put("comment", comment);
        try {
            toInsert.put("picture", file.getBytes());
        } catch (IOException e) {
            
            e.printStackTrace();
        }

        Document doc = mongoTemplate.insert(toInsert, "posts");

        return doc.getObjectId("_id").toString();
    }

    public Post getImageSQL(String postId){

        Post post = jdbcTemplate.query(GET_FILE_SQL, new PreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, postId);
            }
            
        } , new ResultSetExtractor<Post>() {

            @Override
            public Post extractData(ResultSet rs) throws SQLException, DataAccessException {

                Post result = new Post();

                while(rs.next()){
                    
                    result.setId(rs.getString("post_id"));
                    result.setComment(rs.getString("comments"));
                    result.setPicture(rs.getBytes("picture"));
                }

                return result;
               
            }
            
        });

        return post;
        
    }

    public String getImageMongo(String objectId){

        ObjectId objId = new ObjectId(objectId);
        Query query = Query.query(Criteria.where("_id").is(objId));
        List<Document> docs =mongoTemplate.find(query, Document.class, "posts");

        //  extract mongo BinData into bson Binary type, binaryObj.getData() to get byte[]
        Binary pic = (Binary) docs.get(0).get("picture");
        byte[] bytePic = pic.getData();
        String encodedString = BASE64_PREFIX +  Base64.getEncoder().encodeToString(bytePic);

        return encodedString;
    }
}
