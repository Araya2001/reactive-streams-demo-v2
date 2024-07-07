package org.aaj.example.publisherwebapp.ui.layout;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import org.aaj.example.publisherwebapp.backend.service.StoredEventTopicEventNotifierService;
import org.aaj.example.publisherwebapp.ui.view.MainView;
import org.aaj.example.publisherwebapp.ui.view.MongoDBStoredEventView;
import org.aaj.example.publisherwebapp.ui.view.PublishEventView;
import org.springframework.beans.factory.annotation.Autowired;

@Route("main-app-layout")
public class MainAppLayout extends AppLayout {

    private final DrawerToggle drawerToggle = new DrawerToggle();

    @Autowired
    public MainAppLayout(StoredEventTopicEventNotifierService eventNotifierService) {

        // Add this subscription to get notifications from event streams
        eventNotifierService
                .getPublisher()
                .subscribe(
                        event -> {
                            Notification notification =
                                    Notification.show("New notification received: " + event);
                            notification.setPosition(Notification.Position.TOP_CENTER);
                            notification.setDuration(1500);
                        }
                        );
        createHeader();
    }

    private void createHeader() {
        // Set the logo for the web page
        H1 logo = new H1("Reactive Streams Demo V2");
        logo.getStyle().set("font-size", "var(--lumo-font-size-l)").set("margin", "0");
        logo.addClassName("logo");

        // Add links to drawer
        addToDrawer(createRouterLinkVerticalLayout());

        // Add Components to the Navigation Bar
        addToNavbar(drawerToggle, logo);
    }

    private VerticalLayout createRouterLinkVerticalLayout() {
        // Create Router Links for the drawer
        RouterLink homeLink = new RouterLink("Home", MainView.class);
        RouterLink mongoDBStoredEventsLink = new RouterLink("Check MongoDB stored events!", MongoDBStoredEventView.class);
        RouterLink messagePublisherLink = new RouterLink("Publish a message!", PublishEventView.class);


        // Add Router Links to Vertical Layout
        return new VerticalLayout(
                homeLink,
                mongoDBStoredEventsLink,
                messagePublisherLink
        );
    }
}
