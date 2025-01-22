package greencity.filters;

import greencity.annotations.RatingCalculationEnum;
import greencity.entity.RatingStatistics;
import greencity.entity.RatingStatistics_;
import greencity.entity.User;
import greencity.entity.User_;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.criteria.*;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class RatingStatisticsSpecificationTest {

    private RatingStatisticsSpecification specification;

    @Mock
    private CriteriaBuilder criteriaBuilder;
    @Mock
    private Root<RatingStatistics> root;
    @Mock
    private CriteriaQuery<?> criteriaQuery;
    @Mock
    private Path<Object> pathMock;
    @Mock
    private Join<RatingStatistics, User> userJoinMock;
    @Mock
    private Predicate predicateMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        specification = new RatingStatisticsSpecification();
    }

    @Test
    void testToPredicate_IdCriteria() {

        SearchCriteria criteria = SearchCriteria.builder()
                .key("id")
                .type("id")
                .value(1L)
                .build();

        specification = new RatingStatisticsSpecification(List.of(criteria));
        when(criteriaBuilder.conjunction()).thenReturn(predicateMock);
        when(root.get("id")).thenReturn(pathMock);
        when(criteriaBuilder.equal(pathMock, 1L)).thenReturn(predicateMock);

        Predicate result = specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertNotNull(result);
        verify(criteriaBuilder).equal(pathMock, 1L);
    }

    @Test
    void testToPredicate_EnumCriteria() {

        SearchCriteria criteria = SearchCriteria.builder()
                .key("ratingCalculationEnum")
                .type("enum")
                .value("ADD")
                .build();

        specification = new RatingStatisticsSpecification(List.of(criteria));
        when(criteriaBuilder.conjunction()).thenReturn(predicateMock);
        when(criteriaBuilder.disjunction()).thenReturn(predicateMock);
        when(criteriaBuilder.equal(any(), any())).thenReturn(predicateMock);

        Predicate result = specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertNotNull(result);
        verify(criteriaBuilder, atLeastOnce()).equal(any(), any());
    }

    @Test
    void testToPredicate_UserMailCriteria() {

        SearchCriteria criteria = SearchCriteria.builder()
                .key("email")
                .type("userMail")
                .value("test@example.com")
                .build();

        specification = new RatingStatisticsSpecification(List.of(criteria));
        when(criteriaBuilder.conjunction()).thenReturn(predicateMock);

        when(root.join(RatingStatistics_.user)).thenReturn(userJoinMock);

        Path<String> emailPathMock = mock(Path.class);
        when(userJoinMock.get(User_.email)).thenReturn(emailPathMock);

        when(criteriaBuilder.like(emailPathMock, "%test@example.com%")).thenReturn(predicateMock);

        Predicate result = specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertNotNull(result);
        verify(criteriaBuilder).like(emailPathMock, "%test@example.com%");
    }


    @Test
    void testToPredicate_UserIdCriteria() {

        SearchCriteria criteria = SearchCriteria.builder()
                .key("id")
                .type("userId")
                .value(123L)
                .build();

        specification = new RatingStatisticsSpecification(List.of(criteria));
        when(criteriaBuilder.conjunction()).thenReturn(predicateMock);

        when(root.join(RatingStatistics_.user)).thenReturn(userJoinMock);
        Path<Long> idPathMock = mock(Path.class);  // Mock Path<Long>
        when(userJoinMock.get(User_.id)).thenReturn(idPathMock);

        when(criteriaBuilder.equal(idPathMock, 123L)).thenReturn(predicateMock);

        Predicate result = specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertNotNull(result);
        verify(criteriaBuilder).equal(idPathMock, 123L);
    }


    @Test
    void testToPredicate_DateRangeCriteria() {

        SearchCriteria criteria = SearchCriteria.builder()
                .key("createDate")
                .type("dateRange")
                .value("2024-01-01 to 2024-12-31")
                .build();

        specification = new RatingStatisticsSpecification(List.of(criteria));
        when(criteriaBuilder.conjunction()).thenReturn(predicateMock);

        Path<ZonedDateTime> pathDateMock = mock(Path.class);
        when(root.join(RatingStatistics_.createDate)).thenReturn((Join<RatingStatistics, ZonedDateTime>) pathDateMock);

        Expression<ZonedDateTime> startDateMock = mock(Expression.class);
        Expression<ZonedDateTime> endDateMock = mock(Expression.class);

        when(criteriaBuilder.between(pathDateMock, startDateMock, endDateMock)).thenReturn(predicateMock);

        Predicate result = specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertNotNull(result);
        verify(criteriaBuilder).between(pathDateMock, startDateMock, endDateMock);
    }

    @Test
    void testToPredicate_NumericCriteria() {

        SearchCriteria criteria = SearchCriteria.builder()
                .key("pointsChanged")
                .type("pointsChanged")
                .value(10)
                .build();

        specification = new RatingStatisticsSpecification(List.of(criteria));
        when(criteriaBuilder.conjunction()).thenReturn(predicateMock);
        when(root.get("pointsChanged")).thenReturn(pathMock);
        when(criteriaBuilder.equal(pathMock, 10)).thenReturn(predicateMock);

        Predicate result = specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertNotNull(result);
        verify(criteriaBuilder).equal(pathMock, 10);
    }

    @Test
    void testEmptySearchCriteriaList() {

        specification = new RatingStatisticsSpecification(List.of());
        when(criteriaBuilder.conjunction()).thenReturn(predicateMock);

        Predicate result = specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertNotNull(result);
        verify(criteriaBuilder).conjunction();
    }
}
