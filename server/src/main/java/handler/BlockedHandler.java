package handler;

import dao.UserDao;
import dto.UserDto;
import dto.MessageDto;
import dto.BlockedDto;
import dao.MessageDao;
import dao.BlockedDao;

import org.apache.commons.codec.digest.DigestUtils;
import org.bson.Document;
import request.ParsedRequest;
import response.HttpResponseBuilder;
import response.RestApiAppResponse;

import handler.AuthFilter.AuthResult;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class BlockedHandler implements BaseHandler{

    @Override

    public HttpResponseBuilder handleRequest(ParsedRequest request) {

        BlockedDto blockedDto = GsonTool.gson.fromJson(request.getBody(), BlockedDto.class);
        BlockedDao blockedDao = BlockedDao.getInstance();

        UserDao userDao = UserDao.getInstance();


        MessageDto messageDto = GsonTool.gson.fromJson(request.getBody(), dto.MessageDto.class);


        AuthResult authResult = AuthFilter.doFilter(request);
        if(!authResult.isLoggedIn){
            return new HttpResponseBuilder().setStatus(StatusCodes.UNAUTHORIZED);
        }

        if (userDao.query(new Document("userName", messageDto.getToId())).size() == 0) {
            var res = new RestApiAppResponse<>(false, null,
                    "Trying to block an unknown user");
            return new HttpResponseBuilder().setStatus("200 OK").setBody(res);
        }

        String conversationId = makeConvoId(messageDto.getFromId(), messageDto.getToId());

        blockedDto.setConversationId(conversationId);

        var userQuery = new Document()
                .append("blockedConversation", conversationId);

        var result = blockedDao.query(userQuery);

        if(messageDto.getFromId().equals(messageDto.getToId())){
            var res = new RestApiAppResponse<>(false, null,
                    "Cannot block yourself");
            return new HttpResponseBuilder().setStatus("200 OK").setBody(res);

        }


        if(result.size()!=0){
            var res = new RestApiAppResponse<>(false, null,
                    "This user has already been blocked");
            return new HttpResponseBuilder().setStatus("200 OK").setBody(res);

        }



        var filter = new Document("blockedConversation", conversationId);

        blockedDao.put(blockedDto);


        var res = new RestApiAppResponse<>(true, blockedDao.query(filter), "Blocked Users");
        return new HttpResponseBuilder().setStatus("200 OK").setBody(res);
    }
    public static String makeConvoId(String a, String b){
        return List.of(a,b).stream()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.joining("_"));
    }
}