package dao;

import com.mongodb.client.MongoCollection;
import dto.BlockedDto;
import dto.ConversationDto;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BlockedDao extends BaseDao<BlockedDto>{

    private static BlockedDao instance;
    private BlockedDao(MongoCollection<Document> collection) {
        super(collection);
    }

    public static BlockedDao getInstance(){
        if(instance != null){
            return instance;
        }
        instance = new BlockedDao(MongoConnection.getCollection("BlockedDao"));
        System.out.println(instance);
        return instance;
    }
    public void put(BlockedDto blockedDto) {
        collection.insertOne(blockedDto.toDocument());
    }


    public static BlockedDao getInstance(MongoCollection<Document> collection){
        instance = new BlockedDao(collection);
        return instance;
    }

    @Override
    public List<BlockedDto> query(Document filter) {
        return collection.find(filter)
                .into(new ArrayList<>())
                .stream()
                .map(BlockedDto::fromDocument)
                .collect(Collectors.toList());
    }
}
