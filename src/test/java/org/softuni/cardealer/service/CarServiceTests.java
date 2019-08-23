package org.softuni.cardealer.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.softuni.cardealer.domain.models.service.CarServiceModel;
import org.softuni.cardealer.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@DataJpaTest
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class CarServiceTests {

    private CarServiceModel testCar;

    private ModelMapper modelMapper;
    private CarService carService;

    @Autowired
    private CarRepository carRepository;

    private String unmatchedParamFor(String param) {
        return String.format("%s doesn't match!", param);
    }

    @Before
    public void init() {
        modelMapper = new ModelMapper();
        carService = new CarServiceImpl(carRepository, modelMapper);

        testCar = new CarServiceModel();
        testCar.setMake("TestMake");
        testCar.setModel("TestModel");
        testCar.setTravelledDistance(100L);
    }

    @Test
    public void saveCar_whenSaveCorrectCar_expectSameOne() {
        CarServiceModel actual = carService.saveCar(testCar);

        CarServiceModel expected = modelMapper
                .map(carRepository.findAll().get(0), CarServiceModel.class);

        assertEquals(unmatchedParamFor("Id"), expected.getId(), actual.getId());
        assertEquals(unmatchedParamFor("Make"), expected.getMake(), actual.getMake());
        assertEquals(unmatchedParamFor("Model"), expected.getModel(), actual.getModel());
        assertEquals(unmatchedParamFor("Travelled Distance"),
                expected.getTravelledDistance(), actual.getTravelledDistance());
    }

    @Test(expected = Exception.class)
    public void saveCar_whenSaveNullCar_expectException(){
        carService.saveCar(new CarServiceModel());
    }

    @Test
    public void editCar_whenEditCar_expectEditedValues() {
        CarServiceModel car = carService.saveCar(testCar);

        String make = "DifferentTestMake";
        String model = "DifferentTestModel";
        Long traveledDistance = 22L;

        car.setMake(make);
        car.setModel(model);
        car.setTravelledDistance(traveledDistance);

        CarServiceModel expected = carService.editCar(car);

        assertEquals(unmatchedParamFor("Make"), expected.getMake(), make);
        assertEquals(unmatchedParamFor("Model"), expected.getModel(), model);
        assertEquals(unmatchedParamFor("Travelled Distance"),
                expected.getTravelledDistance(), traveledDistance);
    }

    @Test
    public void editCar_whenSameCar_expectSameValues() {
        CarServiceModel actual = carService.saveCar(testCar);

        CarServiceModel expected = carService.editCar(actual);

        assertEquals(unmatchedParamFor("Id"), expected.getId(), actual.getId());
        assertEquals(unmatchedParamFor("Make"), expected.getMake(), actual.getMake());
        assertEquals(unmatchedParamFor("Model"), expected.getModel(), actual.getModel());
        assertEquals(unmatchedParamFor("Travelled Distance"),
                expected.getTravelledDistance(), actual.getTravelledDistance());
    }

    @Test(expected = Exception.class)
    public void editCar_whenIdIsNull_expectException() {
        carService.saveCar(testCar);

        carService.editCar(testCar);
    }

    @Test
    public void deleteCar_whenDeletedCar_expectSameCar() {
        CarServiceModel actual = carService.saveCar(testCar);

        CarServiceModel expected = carService.deleteCar(actual.getId());

        assertEquals(unmatchedParamFor("Name"), expected.getId(), actual.getId());
        assertEquals(unmatchedParamFor("Make"), expected.getMake(), actual.getMake());
        assertEquals(unmatchedParamFor("Model"), expected.getModel(), actual.getModel());
        assertEquals(unmatchedParamFor("Travelled Distance"),
                expected.getTravelledDistance(), actual.getTravelledDistance());
    }

    @Test
    public void deleteCar_whenDeletedCar_expectNoCarInRepository() {
        CarServiceModel supplier = carService.saveCar(testCar);

        carService.deleteCar(supplier.getId());

        assertNull(carRepository.findById(supplier.getId()).orElse(null));
    }

    @Test(expected = Exception.class)
    public void deleteCar_whenNotExistCar_expectException() {
        carService.saveCar(testCar);

        testCar.setMake("DifferentTestMake");
        testCar.setModel("DifferentTestModel");
        testCar.setTravelledDistance(22L);
        CarServiceModel secondCar = carService.saveCar(testCar);

        carService.deleteCar(secondCar.getId());
        carService.deleteCar(secondCar.getId());
    }

    @Test(expected = Exception.class)
    public void deleteCar_whenIdIsNull_expectException() {
        carService.deleteCar(null);
    }

    @Test
    public void findCarById_whenFoundCar_expectSameCar() {
        CarServiceModel actual = carService.saveCar(testCar);

        CarServiceModel expected = carService.findCarById(actual.getId());

        assertEquals(unmatchedParamFor("Make"), expected.getMake(), actual.getMake());
        assertEquals(unmatchedParamFor("Model"), expected.getModel(), actual.getModel());
        assertEquals(unmatchedParamFor("Travelled Distance"),
                expected.getTravelledDistance(), actual.getTravelledDistance());
    }

    @Test(expected = Exception.class)
    public void findCarById_whenIdIsNull_expectException() {
        carService.findCarById(null);
    }
}
