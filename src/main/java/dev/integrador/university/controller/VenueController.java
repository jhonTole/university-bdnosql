package dev.integrador.university.controller;

import dev.integrador.university.model.VenueModel;
import dev.integrador.university.repository.VenueRepository;
import dev.integrador.university.service.VenueService;
import dev.integrador.university.view.venue.VenueView;
import javafx.scene.layout.Region;

public class VenueController {
    private final VenueView view;
    private final VenueService service;

    public VenueController(VenueRepository repository) {
        VenueModel model = new VenueModel();
        this.service = new VenueService(repository, model);
        this.view = new VenueView(model,service);
    }
    public Region getView(){
        service.findAllVenues();
        return view.build();
    }
}
