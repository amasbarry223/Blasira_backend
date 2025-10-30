package com.example.Blasira_Backend.repository;

import com.example.Blasira_Backend.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}
