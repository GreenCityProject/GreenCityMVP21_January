package greencity.service;

import greencity.dto.event.ImageRequestDto;
import greencity.dto.event.ImageResponseDto;

import java.util.List;

public interface ImageService {

    ImageResponseDto createImage(ImageRequestDto imageRequestDto);

    ImageResponseDto updateImage(Long imageId, ImageRequestDto imageRequestDto);

    void deleteImage(Long imageId);

    ImageResponseDto getImageById(Long id);

    List<ImageResponseDto> getImagesByEventId(Long eventId);

}
