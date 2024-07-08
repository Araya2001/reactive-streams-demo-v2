package org.aaj.example.publisherwebapp.ui.form;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.aaj.example.publisherwebapp.backend.dto.UnprocessedTopicEventDTO;
import org.aaj.example.publisherwebapp.backend.service.UnprocessedEventTopicPublisherService;
import reactor.core.publisher.Sinks;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Route("publish-message-form")
public class PublishMessageForm extends FormLayout {


    public PublishMessageForm(UnprocessedEventTopicPublisherService unprocessedEventTopicPublisherService) {

        // Sink Definition
        Sinks.Many<UnprocessedTopicEventDTO> unprocessedEventSink = Sinks.many().multicast().onBackpressureBuffer();

        // Message List Items
        List<MessageListItem> messageListItems = new ArrayList<>();

        // Component Definition
        TextField messageBody = new TextField("Message Body");
        Button createMessageButton = new Button("Create Message!", new Icon(VaadinIcon.EDIT));
        Button sendMessagesButton = new Button("Send Messages!", new Icon(VaadinIcon.UPLOAD));
        MessageList messageList = new MessageList();

        // Add Button Theme Variant to change to a visible color
        createMessageButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        sendMessagesButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);


        // This is for the message creation section
        // Wrap the "create message" button in a div to prevent the button from stretching to full-width
        VerticalLayout createMessageLayout = new VerticalLayout(messageBody, new Div(createMessageButton));
        createMessageLayout.setAlignItems(FlexComponent.Alignment.STRETCH);


        // This is for multiple message publishing section
        VerticalLayout messagePublishingLayout = new VerticalLayout(messageList, sendMessagesButton);

        // This is the runnable action for adding a visual item as pending messages to emit events to the sink
        Runnable mapTextFieldToUnprocessedTopicEventDTO = () -> {
            // Create unprocessedTopicEvent DTO
            UnprocessedTopicEventDTO unprocessedTopicEventDTO = UnprocessedTopicEventDTO
                    .builder()
                    .body(messageBody.getValue())
                    .timestamp(Instant.now())
                    .build();

            // Add a message to the messageList
            messageListItems.add(createListMessageItem(unprocessedTopicEventDTO.getBody(), unprocessedTopicEventDTO.getTimestamp()));

            // Set items to messageList components
            messageList.setItems(messageListItems);

            // Emit the unprocessed Event to the sink
            unprocessedEventSink.tryEmitNext(unprocessedTopicEventDTO);

            // Clear Text Field of the message
            messageBody.setValue("");
        };

        // This is the runnable action for adding a visual item as pending messages to emit events to the sink
        Runnable publishMessagesInSinkToUnprocessedEventTopic = () -> {
            unprocessedEventTopicPublisherService
                    .publish(unprocessedEventSink.asFlux())
                    .subscribe(sendResult -> getUI().ifPresent(ui -> ui.access(
                            () -> {
                                Notification notification =
                                        Notification.show("Published message " + sendResult.getMessageId() + " successfully!");
                                notification.setPosition(Notification.Position.MIDDLE);
                                notification.setDuration(1500);
                            }
                    )));

            // Clear items list
            messageListItems.clear();
            messageList.setItems(messageListItems);
        };

        addClickEventListener(createMessageButton, mapTextFieldToUnprocessedTopicEventDTO);
        addClickEventListener(sendMessagesButton, publishMessagesInSinkToUnprocessedEventTopic);

        Html hr = new Html("<hr/>");
        hr.getStyle().set("height", "2px");

        VerticalLayout allMessagesLayout = new VerticalLayout(
                createMessageLayout,
                hr,
                messagePublishingLayout);

        allMessagesLayout.setSizeFull();

        add(allMessagesLayout);

    }

    private void addClickEventListener(Button button, Runnable runnable) {
        button.addClickListener(event -> runnable.run());
    }

    private MessageListItem createListMessageItem(String body, Instant timestamp) {
        return new MessageListItem(body, timestamp, "User Defined Message");
    }
}
