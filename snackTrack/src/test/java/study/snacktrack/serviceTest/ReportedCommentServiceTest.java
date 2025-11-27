package study.snacktrack.serviceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import study.snacktrack.dto.ReportedCommentResponse;
import study.snacktrack.repositories.*;
import study.snacktrack.entities.*;
import study.snacktrack.services.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the ReportedCommentService, covering the core logic for submitting, retrieving, and managing reported comments.
 * This class ensures that the service enforces rules such as preventing users from reporting their own comments and correctly maps data to DTOs.
 */
class ReportedCommentServiceTest {

    private UserRepository userRepository;
    private CommentRepository commentRepository;
    private ReportedCommentRepository reportedCommentRepository;
    private ReportedCommentService service;

    /**
     * Sets up mock repositories and initializes the ReportedCommentService instance before each test.
     * This isolates the service logic, allowing for verification of correct repository method calls and business rule enforcement.
     */
    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        commentRepository = mock(CommentRepository.class);
        reportedCommentRepository = mock(ReportedCommentRepository.class);

        service = new ReportedCommentService(userRepository, commentRepository, reportedCommentRepository);
    }

    /**
     * Tests the successful submission of a new report for a comment.
     * It verifies that the service saves the new {@code ReportedComment} entity and returns the correctly mapped {@code ReportedCommentResponse} DTO.
     */
    @Test
    void reportComment_shouldSaveReport() {
        Comment comment = new Comment();
        comment.setId(1);
        comment.setAuthorId(2);
        comment.setContent("Test comment");

        User user = new User();
        user.setId(3);

        when(commentRepository.findById(1)).thenReturn(Optional.of(comment));
        when(userRepository.findById(3)).thenReturn(Optional.of(user));
        when(reportedCommentRepository.findByCommentIdAndReportingId(1, 3)).thenReturn(Optional.empty());

        ReportedComment saved = new ReportedComment();
        saved.setId(10);
        saved.setCommentId(1);
        saved.setReportingId(3);
        saved.setContent("Spam");

        when(reportedCommentRepository.save(any(ReportedComment.class)))
                .thenAnswer(invocation -> {
                    ReportedComment rc = invocation.getArgument(0);
                    rc.setId(10);
                    return rc;
                });


        ReportedCommentResponse response = service.reportComment(1, 3, "Spam");

        assertEquals(10, response.getId());
        assertEquals(3, response.getReportingId());
        assertEquals(1, response.getCommentId());
        assertEquals("Spam", response.getContent());
        verify(reportedCommentRepository).save(any(ReportedComment.class));
    }

    /**
     * Tests that reporting a comment throws an exception if the reporting user is the author of the comment.
     * It verifies that the service enforces the business rule preventing users from reporting their own content.
     */
    @Test
    void reportComment_shouldThrowWhenReportingOwnComment() {
        Comment comment = new Comment();
        comment.setId(1);
        comment.setAuthorId(3);

        User user = new User();
        user.setId(3);

        when(commentRepository.findById(1)).thenReturn(Optional.of(comment));
        when(userRepository.findById(3)).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class,
                () -> service.reportComment(1, 3, "Spam"));
    }

    /**
     * Tests the retrieval of all reports submitted by a specific user ID.
     * It verifies that the service fetches the list of {@code ReportedComment} entities associated with the reporting user.
     */
    @Test
    void getAllReportsByUser_shouldReturnReports() {
        User user = new User();
        user.setId(3);

        ReportedComment rc = new ReportedComment();
        rc.setId(1);
        rc.setReportingId(3);
        rc.setCommentId(2);
        rc.setContent("Spam");

        when(userRepository.findById(3)).thenReturn(Optional.of(user));
        when(reportedCommentRepository.findAllByReportingId(3)).thenReturn(Optional.of(List.of(rc)));

        List<ReportedComment> result = service.getAllReportsByUser(3);

        assertEquals(1, result.size());
        assertEquals("Spam", result.get(0).getContent());
    }

    /**
     * Tests the retrieval of all reports submitted against a specific comment ID.
     * It verifies that the service fetches the list of {@code ReportedComment} entities targeting the specified comment.
     */
    @Test
    void getAllReportsByComment_shouldReturnReports() {
        Comment comment = new Comment();
        comment.setId(2);
        comment.setAuthorId(1);

        ReportedComment rc = new ReportedComment();
        rc.setId(1);
        rc.setReportingId(3);
        rc.setCommentId(2);
        rc.setContent("Spam");

        when(commentRepository.findById(2)).thenReturn(Optional.of(comment));
        when(reportedCommentRepository.findAllByCommentId(2)).thenReturn(Optional.of(List.of(rc)));

        List<ReportedComment> result = service.getAllReportsByComment(2);

        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getCommentId());
    }

    /**
     * Tests the retrieval of all reports existing in the database.
     * It verifies that the service delegates the request to retrieve the complete list of reports.
     */
    @Test
    void getAllReports_shouldReturnAll() {
        ReportedComment rc = new ReportedComment();
        rc.setId(1);
        rc.setReportingId(3);
        rc.setCommentId(2);
        rc.setContent("Spam");

        when(reportedCommentRepository.findAll()).thenReturn(List.of(rc));

        List<ReportedComment> result = service.getAllReports();

        assertEquals(1, result.size());
        assertEquals("Spam", result.get(0).getContent());
    }

    /**
     * Tests the successful deletion of an existing report by its ID.
     * It verifies that the service finds the report and performs the delete operation.
     */
    @Test
    void deleteReport_shouldDeleteWhenExists() {
        ReportedComment rc = new ReportedComment();
        rc.setId(5);

        when(reportedCommentRepository.findById(5)).thenReturn(Optional.of(rc));

        service.deleteReport(5);

        verify(reportedCommentRepository).delete(rc);
    }

    /**
     * Tests that attempting to delete a non-existent report throws an exception.
     * It verifies that the service enforces data integrity by requiring the report ID to exist.
     */
    @Test
    void deleteReport_shouldThrowWhenNotFound() {
        when(reportedCommentRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> service.deleteReport(99));
    }
}