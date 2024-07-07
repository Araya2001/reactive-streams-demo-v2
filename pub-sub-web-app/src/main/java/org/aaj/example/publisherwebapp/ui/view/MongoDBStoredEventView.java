package org.aaj.example.publisherwebapp.ui.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.aaj.example.publisherwebapp.backend.service.ProcessedTopicEventDocumentService;
import org.aaj.example.publisherwebapp.ui.grid.ProcessedTopicEventDocumentGrid;
import org.aaj.example.publisherwebapp.ui.layout.MainAppLayout;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "mongo-db-stored-event-view", layout = MainAppLayout.class)
public class MongoDBStoredEventView extends VerticalLayout {

    @Autowired
    public MongoDBStoredEventView(ProcessedTopicEventDocumentService processedTopicEventDocumentService) {
        ProcessedTopicEventDocumentGrid processedTopicEventDocumentGrid = new ProcessedTopicEventDocumentGrid(processedTopicEventDocumentService);
        add(processedTopicEventDocumentGrid);
    }
}
