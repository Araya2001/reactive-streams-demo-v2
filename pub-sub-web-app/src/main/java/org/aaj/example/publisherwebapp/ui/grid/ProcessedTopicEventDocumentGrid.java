package org.aaj.example.publisherwebapp.ui.grid;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import org.aaj.example.publisherwebapp.backend.dto.ProcessedTopicEventDTO;
import org.aaj.example.publisherwebapp.backend.model.ProcessedTopicEventDocument;
import org.aaj.example.publisherwebapp.backend.service.ProcessedTopicEventDocumentService;

import java.util.List;


public class ProcessedTopicEventDocumentGrid extends Div {

    public ProcessedTopicEventDocumentGrid(ProcessedTopicEventDocumentService processedTopicEventDocumentService) {
        // Grid Definition
        Grid<ProcessedTopicEventDocument> grid = new Grid<>(ProcessedTopicEventDocument.class);
        grid.addColumn(ProcessedTopicEventDocument::getId).setHeader("ID");
        grid.addColumn(ProcessedTopicEventDocument::getDateCreated).setHeader("Date Created");
        grid.addColumn(processedTopicEventDocument -> processedTopicEventDocument.getProcessedTopicEvent().getProcessUUID())
                .setHeader("Processed Topic Event UUID");
        grid.addColumn(processedTopicEventDocument -> processedTopicEventDocument.getProcessedTopicEvent().getBody())
                .setHeader("Processed Topic Event Body");
        grid.addColumn(processedTopicEventDocument -> processedTopicEventDocument.getProcessedTopicEvent().getPreparationMessage())
                .setHeader("Preparation Message");

        List<ProcessedTopicEventDocument> foundDocuments = processedTopicEventDocumentService.findAll().collectList().block();
        grid.setItems(foundDocuments);

        add(grid);

    }
}
