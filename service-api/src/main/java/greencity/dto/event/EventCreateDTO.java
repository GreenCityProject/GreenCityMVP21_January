package greencity.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventCreateDTO {
//    @Size(min = 0, max = 5, message = "Max 5 images")
//    private List<MultipartFile> files;

    @NotBlank(message = "Event info can't be empty")
    private EventRequestDto event;

    private ImageRequestDto mainImage;

    private String containerName;

    ImageRequestDto chosenOfProposedImage;
}
