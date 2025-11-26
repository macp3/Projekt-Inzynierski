package study.snacktrack.ServiceTest;

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

class ReportedCommentServiceTest {

    private UserRepository userRepository;
    private CommentRepository commentRepository;
    private ReportedMealRepository reportedMealRepository;
    private ReportedCommentRepository reportedCommentRepository;
    private ReportedCommentService service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        commentRepository = mock(CommentRepository.class);
        reportedMealRepository = mock(ReportedMealRepository.class);
        reportedCommentRepository = mock(ReportedCommentRepository.class);

        service = new ReportedCommentService(userRepository, null,
                commentRepository, reportedMealRepository, reportedCommentRepository);
    }

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

    @Test
    void deleteReport_shouldDeleteWhenExists() {
        ReportedComment rc = new ReportedComment();
        rc.setId(5);

        when(reportedCommentRepository.findById(5)).thenReturn(Optional.of(rc));

        service.deleteReport(5);

        verify(reportedCommentRepository).delete(rc);
    }

    @Test
    void deleteReport_shouldThrowWhenNotFound() {
        when(reportedCommentRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> service.deleteReport(99));
    }
}