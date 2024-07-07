package org.aaj.example.publisherwebapp.ui.view;


import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.aaj.example.publisherwebapp.ui.layout.MainAppLayout;


@Route(value = "", layout = MainAppLayout.class)
public class MainView extends VerticalLayout {


    public MainView() {
        H2 title = new H2("Publisher Web App");

        addClassName("main-view");
    }


}
