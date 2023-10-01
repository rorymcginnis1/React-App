package handler;

import dao.UserDao;
import dto.RandomDto;
import dto.UserDto;
import handler.AuthFilter.AuthResult;
import request.ParsedRequest;
import response.HttpResponseBuilder;
import response.RestApiAppResponse;

import java.util.List;
import java.util.Random;

public class RandomMessageHandler implements BaseHandler {

    @Override
    public HttpResponseBuilder handleRequest(ParsedRequest request) {
        UserDao userDao = UserDao.getInstance();

        AuthResult authResult = AuthFilter.doFilter(request);
        if (!authResult.isLoggedIn) {
            return new HttpResponseBuilder().setStatus(StatusCodes.UNAUTHORIZED);
        }

        List<UserDto> existingUsers = userDao.searchUser("");
        existingUsers.remove(authResult.authenticatedUser);

        if (existingUsers.isEmpty()) {
            return new HttpResponseBuilder().setStatus(StatusCodes.NOT_FOUND);
        }

        Random random = new Random();
        UserDto randomUser = existingUsers.get(random.nextInt(existingUsers.size()));

        RandomDto randomDto = new RandomDto();
        randomDto.setRandomUser(randomUser);

        var res = new RestApiAppResponse<>(true, List.of(randomDto), "Random User");

        return new HttpResponseBuilder().setStatus("200 OK").setBody(res);
    }
}
