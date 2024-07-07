package org.aaj.example.publisherwebapp.ui.view;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.aaj.example.publisherwebapp.backend.service.UnprocessedEventTopicPublisherService;
import org.aaj.example.publisherwebapp.ui.form.PublishMessageForm;
import org.aaj.example.publisherwebapp.ui.layout.MainAppLayout;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "publish-event-view", layout = MainAppLayout.class)
public class PublishEventView extends VerticalLayout {

    @Autowired
    public PublishEventView(UnprocessedEventTopicPublisherService unprocessedEventTopicPublisherService) {
        PublishMessageForm publishMessageForm = new PublishMessageForm(unprocessedEventTopicPublisherService);
        H2 title = new H2("Publish Event");
        add(title, publishMessageForm);
    }
}
