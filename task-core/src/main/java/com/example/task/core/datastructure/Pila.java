package com.example.task.core.datastructure;

import com.example.task.core.model.Historial;

public class Pila {
    private Historial[] elementos;
    private int top;
    private int capacidad;

    public Pila(int capacidadInicial) {
        this.capacidad = capacidadInicial;
        this.elementos = new Historial[capacidadInicial];
        this.top = -1;
    }

    public void push(Historial item) {
        if (top == capacidad - 1) {
            // Redimensionar el arreglo
            Historial[] nuevoArreglo = new Historial[capacidad * 2];
            System.arraycopy(elementos, 0, nuevoArreglo, 0, capacidad);
            elementos = nuevoArreglo;
            capacidad *= 2;
        }
        elementos[++top] = item;
    }

    public Historial pop() {
        if (isEmpty()) {
            throw new IllegalStateException("La pila está vacía");
        }
        Historial item = elementos[top];
        elementos[top--] = null;
        return item;
    }

    public boolean isEmpty() {
        return top == -1;
    }

    public int size() {
        return top + 1;
    }
}