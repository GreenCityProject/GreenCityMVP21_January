package greencity.mapping;

import greencity.dto.friendship.FriendCardDto;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class FriendCardDtoMapper extends AbstractConverter<User, FriendCardDto> {

    public FriendCardDtoMapper() {}

    @Override
    protected FriendCardDto convert(User friend) {
        final int DEFAULT_AMOUNT_MUTUAL_FRIENDS = 0;
        return new FriendCardDto(
                friend.getId(),
                friend.getProfilePicturePath(),
                friend.getName(),
                friend.getRating(),
                friend.getCity(),
                DEFAULT_AMOUNT_MUTUAL_FRIENDS);
    }
}
