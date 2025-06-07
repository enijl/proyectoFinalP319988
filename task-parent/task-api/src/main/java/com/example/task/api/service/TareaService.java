package com.example.task.api.service;

import com.example.task.core.datastructure.Pila;
import com.example.task.core.model.Historial;
import com.example.task.core.model.Tarea;
import com.example.task.messaging.producer.HistorialProducer;
import com.example.task.repository.TareaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class TareaService {

    private final TareaRepository tareaRepository;
    private final HistorialProducer historialProducer;
    private final Pila historialPila;

    @Autowired
    public TareaService(TareaRepository tareaRepository, HistorialProducer historialProducer) {
        this.tareaRepository = tareaRepository;
        this.historialProducer = historialProducer;
        this.historialPila = new Pila(10); 
    }

    public Tarea crearTarea(Tarea tarea) {
        Tarea savedTarea = tareaRepository.save(tarea);
        Historial historial = new Historial();
        historial.setTareaId(savedTarea.getId());
        historial.setAccion("Tarea creada: " + savedTarea.getDescripcion());
        historial.setFecha(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        historialPila.push(historial); 
        historialProducer.enviarAccion(historial); 
        return savedTarea;
    }

    public Tarea actualizarTarea(Long id, Tarea tarea) {
        Tarea existingTarea = tareaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));
        existingTarea.setDescripcion(tarea.getDescripcion());
        existingTarea.setCompletada(tarea.isCompletada());
        Tarea updatedTarea = tareaRepository.save(existingTarea);
        Historial historial = new Historial();
        historial.setTareaId(updatedTarea.getId());
        historial.setAccion("Tarea actualizada: " + updatedTarea.getDescripcion());
        historial.setFecha(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        historialPila.push(historial); 
        historialProducer.enviarAccion(historial); 
        return updatedTarea;
    }
    
    public void eliminarTarea(Long id) {
        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));
        tareaRepository.deleteById(id);
        Historial historial = new Historial();
        historial.setTareaId(id);
        historial.setAccion("Tarea eliminada: " + tarea.getDescripcion());
        historial.setFecha(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        historialPila.push(historial); 
        historialProducer.enviarAccion(historial); 
    }

    public void deshacerAccion() {
        if (!historialPila.isEmpty()) {
            Historial historial = historialPila.pop();
            if (historial.getAccion().contains("Tarea creada")) {
                tareaRepository.deleteById(historial.getTareaId());
            }
            
        }
    }
}