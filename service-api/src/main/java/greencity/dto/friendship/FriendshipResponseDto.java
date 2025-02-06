package greencity.dto.friendship;

public class FriendshipResponseDto {

    private final boolean isSuccessful;
    private final String message;

    public FriendshipResponseDto(boolean isSuccessful, String message) {
        this.isSuccessful = isSuccessful;
        this.message = message;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public String getMessage() {
        return message;
    }

}
