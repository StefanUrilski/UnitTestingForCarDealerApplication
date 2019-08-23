package org.softuni.cardealer.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.softuni.cardealer.domain.models.service.CarSaleServiceModel;
import org.softuni.cardealer.domain.models.service.PartSaleServiceModel;
import org.softuni.cardealer.repository.CarSaleRepository;
import org.softuni.cardealer.repository.PartSaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@DataJpaTest
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class SaleServiceTests {
    private CarSaleServiceModel testCarSale;
    private PartSaleServiceModel testPartSale;

    private ModelMapper modelMapper;
    private SaleService saleService;

    @Autowired
    private CarSaleRepository carSaleRepository;

    @Autowired
    private PartSaleRepository partSaleRepository;

    private String unmatchedParamFor(String param) {
        return String.format("%s doesn't match!", param);
    }

    @Before
    public void init() {
        modelMapper = new ModelMapper();
        saleService = new SaleServiceImpl(carSaleRepository, partSaleRepository, modelMapper);

        testCarSale = new CarSaleServiceModel();
        testCarSale.setDiscount(22.2);

        testPartSale = new PartSaleServiceModel();
        testPartSale.setQuantity(22);
        testPartSale.setDiscount(22.2);
    }

    @Test
    public void saleCar_whenSaleCorrectData_expectSameOne() {
        CarSaleServiceModel actual = saleService.saleCar(testCarSale);

        CarSaleServiceModel expected = modelMapper
                .map(carSaleRepository.findAll().get(0), CarSaleServiceModel.class);

        assertEquals(unmatchedParamFor("Id"), expected.getId(), actual.getId());
        assertEquals(unmatchedParamFor("Discount"), expected.getDiscount(), actual.getDiscount());
    }

    @Test(expected = Exception.class)
    public void saleCar_whenSaleNullCar_expectException(){
        saleService.saleCar(new CarSaleServiceModel());
    }

    @Test
    public void salePart_whenSaleCorrectData_expectSameOne() {
        PartSaleServiceModel actual = saleService.salePart(testPartSale);

        PartSaleServiceModel expected = modelMapper
                .map(partSaleRepository.findAll().get(0), PartSaleServiceModel.class);

        assertEquals(unmatchedParamFor("Id"), expected.getId(), actual.getId());
        assertEquals(unmatchedParamFor("Quantity"), expected.getQuantity(), actual.getQuantity());
        assertEquals(unmatchedParamFor("Discount"), expected.getDiscount(), actual.getDiscount());
    }

    @Test(expected = Exception.class)
    public void salePart_whenSaleNullPart_expectException(){
        saleService.salePart(new PartSaleServiceModel());
    }

}
