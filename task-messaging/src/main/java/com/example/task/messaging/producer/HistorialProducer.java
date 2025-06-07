package com.example.task.messaging.producer;

import com.example.task.core.model.Historial;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HistorialProducer {

    public static final String HISTORIAL_QUEUE = "historial.queue";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void enviarAccion(Historial historial) {
        rabbitTemplate.convertAndSend(HISTORIAL_QUEUE, historial);
    }
}