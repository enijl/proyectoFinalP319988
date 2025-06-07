package com.example.task.api.controller;

import com.example.task.api.service.TareaService;
import com.example.task.core.model.Tarea;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tareas")
@Tag(name = "Tareas", description = "API para gestionar tareas")
public class TareaController {

    private final TareaService tareaService;

    @Autowired
    public TareaController(TareaService tareaService) {
        this.tareaService = tareaService;
    }

    @PostMapping
    @Operation(summary = "Crear una nueva tarea")
    public ResponseEntity<Tarea> crearTarea(@RequestBody Tarea tarea) {
        return ResponseEntity.ok(tareaService.crearTarea(tarea));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una tarea existente")
    public ResponseEntity<Tarea> actualizarTarea(@PathVariable Long id, @RequestBody Tarea tarea) {
        return ResponseEntity.ok(tareaService.actualizarTarea(id, tarea));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una tarea existente")
    public ResponseEntity<String> eliminarTarea(@PathVariable Long id) {
        tareaService.eliminarTarea(id);
        return ResponseEntity.ok("Tarea eliminada correctamente");
    }

    @PostMapping("/Deshacer")
    @Operation(summary = "Deshacer la última acción")
    public ResponseEntity<String> deshacerAccion() {
        tareaService.deshacerAccion();
        return ResponseEntity.ok("Última acción deshecha");
    }
}