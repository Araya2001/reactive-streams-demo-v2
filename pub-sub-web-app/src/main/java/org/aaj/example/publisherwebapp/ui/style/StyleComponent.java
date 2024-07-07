package org.aaj.example.publisherwebapp.ui.style;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;

public interface StyleComponent {

    // This default method should be available for every class when implementing this interface.
    default Div addStyleToComponentInDiv(Component component, String justifyContent, String padding, String width) {
        Div div = new Div(component);
        div.getStyle().set("display", "flex");
        div.getStyle().set("justify-content", justifyContent);
        div.getStyle().set("align-items", justifyContent);
        div.getStyle().set("padding", padding);
        div.getStyle().set("width", width);
        return div;
    }
}
