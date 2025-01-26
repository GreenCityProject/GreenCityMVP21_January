package greencity.mapping;

import greencity.dto.friendship.FriendCardDto;
import greencity.entity.Friendship;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class FriendCardDtoMapper extends AbstractConverter<Friendship, FriendCardDto> {
    @Override
    protected FriendCardDto convert(Friendship source) {
        return null;
    }
}
