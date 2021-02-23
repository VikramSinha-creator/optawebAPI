package org.optaweb.vehiclerouting.plugin.websocket;

import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.RoutingProblem;
import org.optaweb.vehiclerouting.service.location.LocationService;
import org.optaweb.vehiclerouting.service.vehicle.VehicleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataRefreshController {
	
	 static final int MAX_TRIES = 10;
	
	private final LocationService locationService;
	private final VehicleService vehicleService;
    
	 private static final Logger logger = LoggerFactory.getLogger(DataRefreshController.class);
	
	@Autowired
	public DataRefreshController(LocationService locationService, VehicleService vehicleService) {
		super();
		this.locationService = locationService;
		this.vehicleService = vehicleService;
	}


	@RequestMapping (method=RequestMethod.POST, value = "/createroutingplan")
	public void createNewRoutePlan(@RequestBody RoutingProblem routingProblem ) {
		locationService.removeAll();
        vehicleService.removeAll();
        logger.info("removed earlier locations and vehicles");
        
        routingProblem.depot().ifPresent(depot -> addWithRetry(depot.coordinates(), depot.description()));
        routingProblem.visits().forEach(visit -> addWithRetry(visit.coordinates(), visit.description()));
        routingProblem.vehicles().forEach(vehicleService::createVehicle);
        
        logger.info("routing message post completed for " +routingProblem.vehicles().size() + " vehicle and " 
        + routingProblem.visits().size() + " visits");
    }

    private void addWithRetry(Coordinates coordinates, String description) {
        int tries = 0;
        while (tries < MAX_TRIES && !locationService.createLocation(coordinates, description)) {
            tries++;
        }
        if (tries == MAX_TRIES) {
            throw new RuntimeException(
                    "Impossible to create a new location near " + coordinates + " after " + tries + " attempts");
        }
    }
        
	

}
