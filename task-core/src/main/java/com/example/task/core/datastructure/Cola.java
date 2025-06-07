package com.example.task.core.datastructure;

import com.example.task.core.model.Historial;

public class Cola {
    private class Nodo {
        Historial dato;
        Nodo siguiente;

        Nodo(Historial dato) {
            this.dato = dato;
            this.siguiente = null;
        }
    }

    private Nodo frente;
    private Nodo fin;

    public Cola() {
        this.frente = null;
        this.fin = null;
    }

    public void enqueue(Historial item) {
        Nodo nuevoNodo = new Nodo(item);
        if (isEmpty()) {
            frente = nuevoNodo;
            fin = nuevoNodo;
        } else {
            fin.siguiente = nuevoNodo;
            fin = nuevoNodo;
        }
    }

    public Historial dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("La cola está vacía");
        }
        Historial item = frente.dato;
        frente = frente.siguiente;
        if (frente == null) {
            fin = null;
        }
        return item;
    }

    public boolean isEmpty() {
        return frente == null;
    }
}