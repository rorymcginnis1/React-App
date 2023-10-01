package handler;

import dao.UserDao;
import dto.UserDto;
import handler.AuthFilter.AuthResult;
import org.bson.Document;
import request.ParsedRequest;
import response.HttpResponseBuilder;
import response.RestApiAppResponse;

public class GetUserSearchHandler implements BaseHandler{

    public HttpResponseBuilder handleRequest(ParsedRequest request){
        UserDao userDao = UserDao.getInstance();
        AuthResult authResult = AuthFilter.doFilter(request);

        if (!authResult.isLoggedIn){
            return new HttpResponseBuilder().setStatus(StatusCodes.UNAUTHORIZED);
        }

        String name = request.getBody();

        var res = new RestApiAppResponse<>(true, userDao.searchUser(name), null);
        return new HttpResponseBuilder().setStatus("200 OK").setBody(res);
    }

}
