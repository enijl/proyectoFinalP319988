package com.example.task.messaging.consumer;

import com.example.task.core.model.Historial;
import com.example.task.repository.HistorialRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HistorialConsumer {

    public static final String HISTORIAL_QUEUE = "historial.queue";

    @Autowired
    private HistorialRepository historialRepository;

  @RabbitListener(queues = HISTORIAL_QUEUE)
    public void receiveMessage(Historial historial) {
        historialRepository.save(historial);
    }
} 