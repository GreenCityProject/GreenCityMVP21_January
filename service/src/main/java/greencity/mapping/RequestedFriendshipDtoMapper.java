package greencity.mapping;

import greencity.dto.friendship.RequestedFriendshipDto;
import greencity.entity.Friendship;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class RequestedFriendshipDtoMapper extends AbstractConverter<Friendship, RequestedFriendshipDto> {

    @Override
    protected RequestedFriendshipDto convert(Friendship source) {
        return new RequestedFriendshipDto(source.getUser().getId(), source.getFriend().getId());
    }
}
