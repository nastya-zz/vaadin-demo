package com.example.application.views.list;

import com.example.application.data.entity.Contact;
import com.example.application.data.service.CrmService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Collections;

@PageTitle("Contacts | Vaadin CRM")
@Route(value = "")
public class ListView extends VerticalLayout {
    Grid<Contact> grid = new Grid<>(Contact.class);
    TextField filterText = new TextField();
    ContactForm form;
    private final CrmService service;

    public ListView(CrmService service) {
        this.service = service;
        addClassName("list-view");
        setSizeFull();

        configureGrid();
        configuredForm();

        add(
            getToolbar(),
            getContent()
        );

        updateList();
        closeEditor();
    }

    private void closeEditor() {
        form.setContact(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void updateList() {
        grid.setItems(service.findAllContact(filterText.getValue()));
    }

    private Component getContent() {
       HorizontalLayout content = new HorizontalLayout(grid, form);
       content.setFlexGrow(2, grid);
       content.setFlexGrow(1, form);
       content.addClassName("content");
       content.setSizeFull();

       return content;
    }

    private void configuredForm() {
        form = new ContactForm(service.findAllCompanies(), service.findAllStatuses());
        form.setWidth("25em");

        form.addListener(ContactForm.SaveEvent.class, this::saveContact);
    }

    private <T extends ComponentEvent<?>> void saveContact(T t) {
    }

    private Component getToolbar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addContactBtn = new Button("Add contact");
        addContactBtn.addClickListener((e -> addContact()));

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addContactBtn);
        toolbar.addClassName("toolbar");

        return toolbar;
    }

    private void addContact() {
        grid.asSingleSelect().clear();
        editContact(new Contact());
    }

    private void configureGrid() {
        grid.addClassName("contact-grid");
        grid.setSizeFull();
        grid.setColumns("firstName", "lastName", "email");
        grid.addColumn(contact -> contact.getStatus().getName()).setHeader("Status");
        grid.addColumn(contact -> contact.getCompany().getName()).setHeader("Company");
        grid.getColumns().forEach(column -> column.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(e -> editContact(e.getValue()));
    }

    private void editContact(Contact contact) {
        if (contact == null) {
            closeEditor();
        } else {
            form.setContact(contact);
            form.setVisible(true);
            addClassName("editing");
        }
    }

}
