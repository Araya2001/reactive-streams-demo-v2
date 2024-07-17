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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Route("publish-message-form")
public class PublishMessageForm extends FormLayout {


    public PublishMessageForm(UnprocessedEventTopicPublisherService unprocessedEventTopicPublisherService) {

        // Unprocessed Topic Event List Definition
        List<UnprocessedTopicEventDTO> unprocessedEventList = new ArrayList<>();

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

        // This action is for adding a visual item as pending messages to add events to the unprocessedEventList
        createMessageButton.addClickListener(buttonClickEvent -> {

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

            // Add the unprocessed Event to the List
            unprocessedEventList.add(unprocessedTopicEventDTO);

            // Clear Text Field of the message
            messageBody.setValue("");
        });

        // This action is for sending events to the publisher as a flux from the unprocessedEventList stream
        sendMessagesButton.addClickListener(buttonClickEvent -> {
            unprocessedEventTopicPublisherService
                    .publish(Flux.fromStream(unprocessedEventList.stream()))
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

            // Clear list to prevent emitting old messages from the list
            unprocessedEventList.clear();
        });


        // Styling Purposes
        Html hr = new Html("<hr/>");
        hr.getStyle().set("height", "2px");

        VerticalLayout allMessagesLayout = new VerticalLayout(
                createMessageLayout,
                hr,
                messagePublishingLayout);

        allMessagesLayout.setSizeFull();

        add(allMessagesLayout);

    }

    private MessageListItem createListMessageItem(String body, Instant timestamp) {
        return new MessageListItem(body, timestamp, "User Defined Message");
    }



}
