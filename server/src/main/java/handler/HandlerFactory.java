package handler;

import request.ParsedRequest;

public class HandlerFactory {
  // routes based on the path. Add your custom handlers here
  public static BaseHandler getHandler(ParsedRequest request) {
    switch (request.getPath()) {
      case "/createUser":
        return new CreateUserHandler();
      case "/login":
        return new LoginHandler();
      case "/getConversations":
        return new GetConversationsHandler();
      case "/getConversation":
        return new GetConversationHandler();
      case "/createMessage":
        return new CreateMessageHandler();
      case "/blocked":
        return new BlockedHandler();
      case "/handleRandom":
        return new RandomMessageHandler();
      case "/getUsers":
        return new GetUserSearchHandler();

      case"/deleteConversation":// added for the delete
        return new DeleteConversationHandler(); //just added for delte

      default:
        return new FallbackHandler();
    }
  }

}
