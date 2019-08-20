package org.softuni.cardealer.service;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.softuni.cardealer.domain.models.service.SupplierServiceModel;
import org.softuni.cardealer.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@DataJpaTest
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class SupplierServiceTests {

    private static final String UNMACHED_PARAM = "%s doesn't match!";

    private ModelMapper modelMapper;
    private SupplierService supplierService;

    @Autowired
    private SupplierRepository supplierRepository;

    @Before
    public void init() {
        modelMapper = new ModelMapper();
        supplierService = new SupplierServiceImpl(supplierRepository, modelMapper);
    }

    @Test
    public void SupplierService_SaveCorrectSupplier_ReturnsSameOne() {
        SupplierServiceModel prep = new SupplierServiceModel();
        prep.setName("TestName");
        prep.setImporter(true);

        SupplierServiceModel actual = supplierService.saveSupplier(prep);

        SupplierServiceModel expected = modelMapper
                .map(supplierRepository.findAll().get(0), SupplierServiceModel.class);

        assertEquals(String.format(UNMACHED_PARAM, "Id"), expected.getId(), actual.getId());
        assertEquals(String.format(UNMACHED_PARAM, "Name"), expected.getName(), actual.getName());
        assertEquals(String.format(UNMACHED_PARAM, "Importer"), expected.isImporter(), actual.isImporter());
    }


}
