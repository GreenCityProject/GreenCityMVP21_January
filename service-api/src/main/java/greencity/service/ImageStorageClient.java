package greencity.service;

import greencity.dto.event.ImageRequestDto;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface ImageStorageClient {
    List<String> uploadImage(String containerName, List<MultipartFile> files, ImageRequestDto chosenOfProposedImage) throws IOException;
}
