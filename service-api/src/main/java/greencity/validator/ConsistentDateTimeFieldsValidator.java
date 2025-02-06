package greencity.validator;

import greencity.annotations.ConsistentDateTime;
import greencity.dto.event.EventDateInfoRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class ConsistentDateTimeFieldsValidator implements ConstraintValidator<ConsistentDateTime, EventDateInfoRequestDto> {

    @Override
    public boolean isValid(EventDateInfoRequestDto eventDateInfoRequestDto, ConstraintValidatorContext context) {
        LocalDateTime startTime = eventDateInfoRequestDto.getEventTimeStart();
        LocalDateTime endTime = eventDateInfoRequestDto.getEventTimeEnd();

        if (startTime == null || endTime == null) {
            return true;
        }

        return startTime.isBefore(endTime);
    }
}

