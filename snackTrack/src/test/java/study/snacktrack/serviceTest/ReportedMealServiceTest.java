package study.snacktrack.serviceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import study.snacktrack.dto.ReportedMealResponse;
import study.snacktrack.repositories.*;
import study.snacktrack.entities.*;
import study.snacktrack.services.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the ReportedMealService, covering the core logic for submitting, retrieving, and managing meal reports.
 * This class ensures that the service enforces business rules, like preventing self-reporting, and correctly handles data persistence and retrieval.
 */
class ReportedMealServiceTest {

    private UserRepository userRepository;
    private MealRepository mealRepository;
    private ReportedMealRepository reportedMealRepository;
    private ReportedMealService service;

    /**
     * Sets up mock repositories and initializes the ReportedMealService instance before each test.
     * This isolates the service logic, enabling verification of correct repository interactions.
     */
    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        mealRepository = mock(MealRepository.class);
        reportedMealRepository = mock(ReportedMealRepository.class);

        service = new ReportedMealService(userRepository, mealRepository, reportedMealRepository);
    }

    /**
     * Tests the successful submission of a new report for a meal.
     * It verifies that the service saves the new {@code ReportedMeal} entity and returns the correctly mapped {@code ReportedMealResponse} DTO.
     */
    @Test
    void reportMeal_shouldSaveReport() {
        Meal meal = new Meal(2, "Pizza", "Cheese pizza");
        meal.setId(2);
        meal.setAuthorId(5);

        User user = new User();
        user.setId(3);

        when(mealRepository.findById(2)).thenReturn(Optional.of(meal));
        when(userRepository.findById(3)).thenReturn(Optional.of(user));
        when(reportedMealRepository.findByMealIdAndReportingId(2, 3)).thenReturn(Optional.empty());

        when(reportedMealRepository.save(any(ReportedMeal.class)))
                .thenAnswer(inv -> {
                    ReportedMeal rm = inv.getArgument(0);
                    rm.setId(10);
                    return rm;
                });

        ReportedMealResponse response = service.reportMeal(2, 3, "Spam");

        assertEquals(10, response.getId());
        assertEquals(3, response.getReportingId());
        assertEquals(2, response.getMealId());
        assertEquals("Spam", response.getContent());
        verify(reportedMealRepository).save(any(ReportedMeal.class));
    }

    /**
     * Tests that reporting a meal throws an exception if the reporting user is the author of the meal.
     * It verifies that the service enforces the business rule preventing users from reporting their own content.
     */
    @Test
    void reportMeal_shouldThrowWhenReportingOwnMeal() {
        Meal meal = new Meal(2, "Pizza", "Cheese pizza");
        meal.setId(2);
        meal.setAuthorId(3);

        User user = new User();
        user.setId(3);

        when(mealRepository.findById(2)).thenReturn(Optional.of(meal));
        when(userRepository.findById(3)).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class,
                () -> service.reportMeal(2, 3, "Spam"));
    }

    /**
     * Tests the retrieval of all reports submitted by a specific user ID.
     * It verifies that the service fetches the list of {@code ReportedMeal} entities associated with the reporting user.
     */
    @Test
    void getAllReportsByUser_shouldReturnReports() {
        User user = new User();
        user.setId(3);

        ReportedMeal rm = new ReportedMeal();
        rm.setId(1);
        rm.setReportingId(3);
        rm.setMealId(2);
        rm.setContent("Spam");

        when(userRepository.findById(3)).thenReturn(Optional.of(user));
        when(reportedMealRepository.findByReportingId(3)).thenReturn(Optional.of(List.of(rm)));

        List<ReportedMeal> result = service.getAllReportsByUser(3);

        assertEquals(1, result.size());
        assertEquals("Spam", result.get(0).getContent());
    }

    /**
     * Tests the retrieval of all reports submitted against a specific meal ID.
     * It verifies that the service fetches the list of {@code ReportedMeal} entities targeting the specified meal.
     */
    @Test
    void getAllReportsByMeal_shouldReturnReports() {
        ReportedMeal rm = new ReportedMeal();
        rm.setId(1);
        rm.setReportingId(3);
        rm.setMealId(2);
        rm.setContent("Spam");

        when(reportedMealRepository.findByMealId(2)).thenReturn(Optional.of(List.of(rm)));

        List<ReportedMeal> result = service.getAllReportsByMeal(2);

        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getMealId());
    }

    /**
     * Tests the retrieval of all reports existing in the database.
     * It verifies that the service delegates the request to retrieve the complete list of reports.
     */
    @Test
    void getAllReports_shouldReturnAll() {
        ReportedMeal rm = new ReportedMeal();
        rm.setId(1);
        rm.setReportingId(3);
        rm.setMealId(2);
        rm.setContent("Spam");

        when(reportedMealRepository.findAll()).thenReturn(List.of(rm));

        List<ReportedMeal> result = service.getAllReports();

        assertEquals(1, result.size());
        assertEquals("Spam", result.get(0).getContent());
    }

    /**
     * Tests the successful deletion of an existing report by its ID.
     * It verifies that the service finds the report and performs the delete operation.
     */
    @Test
    void deleteReport_shouldDeleteWhenExists() {
        ReportedMeal rm = new ReportedMeal();
        rm.setId(5);

        when(reportedMealRepository.findById(5)).thenReturn(Optional.of(rm));

        service.deleteReport(5);

        verify(reportedMealRepository).delete(rm);
    }

    /**
     * Tests that attempting to delete a non-existent report throws an exception.
     * It verifies that the service enforces data integrity by requiring the report ID to exist.
     */
    @Test
    void deleteReport_shouldThrowWhenNotFound() {
        when(reportedMealRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> service.deleteReport(99));
    }

    /**
     * Tests the functionality to delete all reports associated with a specific meal ID.
     * It verifies that the service retrieves all relevant reports and performs a batch deletion.
     */
    @Test
    void deleteReportsByMealId_shouldDeleteAll() {
        ReportedMeal rm = new ReportedMeal();
        rm.setId(1);
        rm.setMealId(2);

        when(reportedMealRepository.findByMealId(2)).thenReturn(Optional.of(List.of(rm)));

        service.deleteReportsByMealId(2);

        verify(reportedMealRepository).deleteAll(List.of(rm));
    }
}