package handler;

import dao.ConversationDao;
import handler.AuthFilter.AuthResult;
import dao.MessageDao;
import dto.ConversationDto;
import org.bson.Document;
import request.ParsedRequest;
import response.HttpResponseBuilder;
import response.RestApiAppResponse;


public class DeleteConversationHandler implements BaseHandler {

    @Override
    public HttpResponseBuilder handleRequest(ParsedRequest request) {

        ConversationDto conversationDto = GsonTool.gson.fromJson(request.getBody(), ConversationDto.class);
        ConversationDao conversationDao = ConversationDao.getInstance();
        MessageDao messageDao = MessageDao.getInstance();

        AuthResult authResult = AuthFilter.doFilter(request);
        if (!authResult.isLoggedIn) {
            return new HttpResponseBuilder().setStatus(StatusCodes.UNAUTHORIZED);
        }

        // Delete conversation
        Document conversationFilter = new Document("conversationId", conversationDto.getConversationId());
        conversationDao.delete(conversationFilter);

        // Delete messages associated with the conversation
        Document messageFilter = new Document("conversationId", conversationDto.getConversationId());
        messageDao.delete(messageFilter);

        var res = new RestApiAppResponse<>(true, null, "Conversation and associated messages deleted");
        return new HttpResponseBuilder().setStatus("200 OK").setBody(res);
    }
}