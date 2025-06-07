package com.example.task.core.datastructure;

import com.example.task.core.model.Tarea;

public class Arbol {
    private class Nodo {
        Tarea tarea;
        Nodo izquierda;
        Nodo derecha;

        Nodo(Tarea tarea) {
            this.tarea = tarea;
            this.izquierda = null;
            this.derecha = null;
        }
    }

    private Nodo raiz;

    public Arbol() {
        this.raiz = null;
    }

    public void insertar(Tarea tarea) {
        raiz = insertarRec(raiz, tarea);
    }

    private Nodo insertarRec(Nodo nodo, Tarea tarea) {
        if (nodo == null) {
            return new Nodo(tarea);
        }
        if (tarea.getId() < nodo.tarea.getId()) {
            nodo.izquierda = insertarRec(nodo.izquierda, tarea);
        } else if (tarea.getId() > nodo.tarea.getId()) {
            nodo.derecha = insertarRec(nodo.derecha, tarea);
        }
        return nodo;
    }

    public Tarea buscar(Long id) {
        Nodo nodo = buscarRec(raiz, id);
        return nodo != null ? nodo.tarea : null;
    }

    private Nodo buscarRec(Nodo nodo, Long id) {
        if (nodo == null || nodo.tarea.getId().equals(id)) {
            return nodo;
        }
        if (id < nodo.tarea.getId()) {
            return buscarRec(nodo.izquierda, id);
        }
        return buscarRec(nodo.derecha, id);
    }
}