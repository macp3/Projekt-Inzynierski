package com.example.demo.repositories;

import com.example.demo.entities.ReportedComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportedCommentRepository extends JpaRepository<ReportedComment, Integer>
{
    Optional<ReportedComment> findByCommentIdAndReportingId(int commentId, int reportingId);
    Optional<List<ReportedComment>> findAllReportsByReportingId(int reportingId);
    Optional<List<ReportedComment>> findAllReportsByCommentId(int commentId);
}
