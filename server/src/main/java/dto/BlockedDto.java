package dto;

import org.bson.Document;

public class BlockedDto extends BaseDto {
    private String conversationId;

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }


    public String getConversationId() {
        return conversationId;
    }
    @Override
    public Document toDocument() {
        var doc = new Document();
        doc.append("blockedConversation", conversationId);
        return doc;
    }

    public static BlockedDto fromDocument(Document document) {
        var res = new BlockedDto();
        res.setConversationId(document.getString("blockedConversation"));
        return res;
    }
}
