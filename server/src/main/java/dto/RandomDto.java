package dto;

import org.bson.Document;

public class RandomDto extends BaseDto {
    private UserDto randomUser;

    public void setRandomUser(UserDto randomUser) {
        this.randomUser = randomUser;
    }

    public UserDto getRandomUser() {
        return randomUser;
    }

    @Override
    public Document toDocument() {
        var doc = new Document();
        doc.append("randomUser", randomUser.toDocument());
        return doc;
    }

    public static RandomDto fromDocument(Document document) {
        var res = new RandomDto();
        res.setRandomUser(UserDto.fromDocument(document.get("randomUser", Document.class)));
        return res;
    }
}
