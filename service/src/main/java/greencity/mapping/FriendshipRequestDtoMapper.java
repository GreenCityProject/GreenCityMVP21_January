package greencity.mapping;

import greencity.dto.friendship.FriendshipRequestDto;
import greencity.entity.Friendship;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class FriendshipRequestDtoMapper extends AbstractConverter<Friendship, FriendshipRequestDto> {

    @Override
    protected FriendshipRequestDto convert(Friendship source) {
        return new FriendshipRequestDto(source.getUser().getId(), source.getFriend().getId());
    }
}
