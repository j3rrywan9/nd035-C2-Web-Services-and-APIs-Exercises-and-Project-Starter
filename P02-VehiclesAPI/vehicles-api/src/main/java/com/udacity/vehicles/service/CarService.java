package com.udacity.vehicles.service;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

/**
 * Implements the car service create, read, update or delete
 * information about vehicles, as well as gather related
 * location and price data when desired.
 */
@Service
public class CarService {

  private final CarRepository repository;
  private final WebClient webClientMaps;
  private final WebClient webClientPricing;
  private final ModelMapper modelMapper;

  @Autowired
  public CarService(CarRepository repository, WebClient maps, WebClient pricing, ModelMapper modelMapper) {
    this.repository = repository;
    this.webClientMaps = maps;
    this.webClientPricing = pricing;
    this.modelMapper = modelMapper;
  }

  /**
   * Gathers a list of all vehicles
   *
   * @return a list of all vehicles in the CarRepository
   */
  public List<Car> list() {
    final List<Car> allCars = repository.findAll();
    for (final Car car : allCars) {
      car.setPrice(new PriceClient(webClientPricing).getPrice(car.getId()));
      car.setLocation(new MapsClient(webClientMaps, modelMapper).getAddress(car.getLocation()));
    }
    return allCars;
  }

  /**
   * Gets car information by ID (or throws exception if non-existent)
   *
   * @param id the ID number of the car to gather information on
   * @return the requested car's information, including location and price
   */
  public Car findById(Long id) {
    final Optional<Car> optionalCar = repository.findById(id);
    final Car car = optionalCar.orElseThrow(CarNotFoundException::new);

    car.setPrice(new PriceClient(webClientPricing).getPrice(id));

    car.setLocation(new MapsClient(webClientMaps, modelMapper).getAddress(car.getLocation()));

    return car;
  }

  /**
   * Either creates or updates a vehicle, based on prior existence of car
   *
   * @param car A car object, which can be either new or existing
   * @return the new/updated car is stored in the repository
   */
  public Car save(Car car) {
    if (car.getId() != null) {
      return repository.findById(car.getId())
        .map(carToBeUpdated -> {
          carToBeUpdated.setDetails(car.getDetails());
          carToBeUpdated.setLocation(car.getLocation());
          carToBeUpdated.setCondition(car.getCondition());
          carToBeUpdated.setDetails(car.getDetails());
          return repository.save(carToBeUpdated);
        }).orElseThrow(CarNotFoundException::new);
    }

    return repository.save(car);
  }

  /**
   * Deletes a given car by ID
   *
   * @param id the ID number of the car to delete
   */
  public void delete(Long id) {
    final Car car = findById(id);
    repository.delete(car);
  }
}
