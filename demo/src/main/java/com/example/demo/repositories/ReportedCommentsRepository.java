package com.example.demo.repositories;

import com.example.demo.entities.ReportedComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportedCommentsRepository extends JpaRepository<ReportedComment, Integer>
{

}
