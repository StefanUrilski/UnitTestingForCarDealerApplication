package org.softuni.cardealer.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.softuni.cardealer.domain.models.service.PartServiceModel;
import org.softuni.cardealer.repository.PartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@DataJpaTest
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class PartServiceTests {

    private PartServiceModel testPart;

    private ModelMapper modelMapper;
    private PartService partService;

    @Autowired
    private PartRepository partRepository;

    private String unmatchedParamFor(String param) {
        return String.format("%s doesn't match!", param);
    }

    @Before
    public void init() {
        modelMapper = new ModelMapper();
        partService = new PartServiceImpl(partRepository, modelMapper);

        testPart = new PartServiceModel();
        testPart.setName("TestName");
        testPart.setPrice(BigDecimal.TEN);
    }

    @Test
    public void savePart_whenSaveCorrectPart_expectSameOne() {
        PartServiceModel actual = partService.savePart(testPart);

        PartServiceModel expected = modelMapper
                .map(partRepository.findAll().get(0), PartServiceModel.class);

        assertEquals(unmatchedParamFor("Id"), expected.getId(), actual.getId());
        assertEquals(unmatchedParamFor("Name"), expected.getName(), actual.getName());
        assertEquals(unmatchedParamFor("Price"), expected.getPrice(), actual.getPrice());
    }

    @Test(expected = Exception.class)
    public void savePart_whenSaveNullPart_expectException(){
        partService.savePart(new PartServiceModel());
    }

    @Test
    public void editPart_whenEditPart_expectEditedValues() {
        PartServiceModel part = partService.savePart(testPart);

        String name = "DifferentTestName";
        BigDecimal price = BigDecimal.ONE;

        part.setName(name);
        part.setPrice(price);

        PartServiceModel expected = partService.editPart(part);

        assertEquals(unmatchedParamFor("Name"), expected.getName(), name);
        assertEquals(unmatchedParamFor("Price"), expected.getPrice(), price);
    }

    @Test
    public void editPart_whenSamePart_expectSameValues() {
        PartServiceModel actual = partService.savePart(testPart);

        PartServiceModel expected = partService.editPart(actual);

        assertEquals(unmatchedParamFor("Id"), expected.getId(), actual.getId());
        assertEquals(unmatchedParamFor("Name"), expected.getName(), actual.getName());
        assertEquals(unmatchedParamFor("Price"), expected.getPrice(), actual.getPrice());
    }

    @Test(expected = Exception.class)
    public void editPart_whenIdIsNull_expectException() {
        partService.savePart(testPart);

        partService.editPart(testPart);
    }

    @Test
    public void deletePart_whenDeletedPart_expectSamePart() {
        PartServiceModel actual = partService.savePart(testPart);

        PartServiceModel expected = partService.deletePart(actual.getId());

        assertEquals(unmatchedParamFor("Name"), expected.getId(), actual.getId());
        assertEquals(unmatchedParamFor("Name"), expected.getName(), actual.getName());
        assertEquals(unmatchedParamFor("Price"), expected.getPrice(), actual.getPrice());
    }

    @Test
    public void deletePart_whenDeletedPart_expectNoPartInRepository() {
        PartServiceModel supplier = partService.savePart(testPart);

        partService.deletePart(supplier.getId());

        assertNull(partRepository.findById(supplier.getId()).orElse(null));
    }

    @Test(expected = Exception.class)
    public void deletePart_whenNotExistPart_expectException() {
        partService.savePart(testPart);
        testPart.setName("DifferentName");
        testPart.setPrice(BigDecimal.ZERO);
        PartServiceModel secondPart = partService.savePart(testPart);

        partService.deletePart(secondPart.getId());
        partService.deletePart(secondPart.getId());
    }

    @Test(expected = Exception.class)
    public void deletePart_whenIdIsNull_expectException() {
        partService.deletePart(null);
    }

    @Test
    public void findPartById_whenFoundPart_expectSamePart() {
        PartServiceModel actual = partService.savePart(testPart);

        PartServiceModel expected = partService.findPartById(actual.getId());

        assertEquals(unmatchedParamFor("Name"), expected.getName(), actual.getName());
        assertEquals(unmatchedParamFor("Price"), expected.getPrice(), actual.getPrice());
    }

    @Test(expected = Exception.class)
    public void findPartById_whenIdIsNull_expectException() {
        partService.findPartById(null);
    }
}
