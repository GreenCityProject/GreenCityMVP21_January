package greencity.validator;

import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.exception.exceptions.WrongCountOfTagsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class EcoNewsDtoRequestValidatorTest {
    @InjectMocks
    private EcoNewsDtoRequestValidator validator;

    @Test
    void isValidTrueTest() {
        AddEcoNewsDtoRequest request = new AddEcoNewsDtoRequest();
        request.setSource("https://valid-url.com");
        request.setTags(Arrays.asList("tag1", "tag2"));
        assertTrue(validator.isValid(request, null));
    }

    @Test
    void isValidWithEmptyTags() {
        AddEcoNewsDtoRequest request = new AddEcoNewsDtoRequest();
        request.setTags(Collections.emptyList());
        assertThrows(WrongCountOfTagsException.class, () -> validator.isValid(request, null));
    }

    @Test
    void isValidWithTooManyTags() {
        AddEcoNewsDtoRequest request = new AddEcoNewsDtoRequest();
        request.setTags(Arrays.asList("tag1", "tag2", "tag3", "tag4"));
        assertThrows(WrongCountOfTagsException.class, () -> validator.isValid(request, null));
    }

    @Test
    void isValidWithInvalidUrl() {
        AddEcoNewsDtoRequest request = new AddEcoNewsDtoRequest();
        request.setSource("invalid_url");
        request.setTags(Arrays.asList("tag1", "tag2"));
        assertThrows(Exception.class, () -> validator.isValid(request, null));
    }

    @Test
    void isValidWithNullSource() {
        AddEcoNewsDtoRequest request = new AddEcoNewsDtoRequest();
        request.setSource(null);
        request.setTags(Arrays.asList("tag1", "tag2"));
        assertTrue(validator.isValid(request, null));
    }

    @Test
    void isValidWithEmptySource() {
        AddEcoNewsDtoRequest request = new AddEcoNewsDtoRequest();
        request.setSource("");
        request.setTags(Arrays.asList("tag1", "tag2"));
        assertTrue(validator.isValid(request, null));
    }
}
