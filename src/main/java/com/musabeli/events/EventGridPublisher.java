package com.musabeli.events;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.BinaryData;
import com.azure.messaging.eventgrid.EventGridEvent;
import com.azure.messaging.eventgrid.EventGridPublisherClient;
import com.azure.messaging.eventgrid.EventGridPublisherClientBuilder;
import com.musabeli.entities.Prestamo;

import java.util.HashMap;
import java.util.Map;

public class EventGridPublisher {

    private final EventGridPublisherClient<EventGridEvent> client;

    public EventGridPublisher() {
        String endpoint = System.getenv("EVENT_GRID_TOPIC_ENDPOINT");
        String key = System.getenv("EVENT_GRID_TOPIC_KEY");
        this.client = new EventGridPublisherClientBuilder()
                .endpoint(endpoint)
                .credential(new AzureKeyCredential(key))
                .buildEventGridEventPublisherClient();
    }

    public void publicarPrestamoCreado(Prestamo prestamo) {
        Map<String, Object> data = new HashMap<>();
        data.put("idPrestamo", prestamo.getId());
        data.put("idUsuario", prestamo.getIdUsuario());
        data.put("idLibro", prestamo.getIdLibro());
        data.put("accion", "CREAR_PRESTAMO");

        EventGridEvent event = new EventGridEvent(
                "prestamos/" + prestamo.getId(),
                "prestamo-libros.PrestamoCreado",
                BinaryData.fromObject(data),
                "1.0"
        );

        client.sendEvent(event);
    }
}
