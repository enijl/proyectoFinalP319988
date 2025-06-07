package com.example.task.repository;

import com.example.task.core.model.Historial;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistorialRepository extends JpaRepository<Historial, Long> {
}