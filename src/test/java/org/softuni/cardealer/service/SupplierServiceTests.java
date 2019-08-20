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

    private SupplierServiceModel testSupplier;

    private ModelMapper modelMapper;
    private SupplierService supplierService;

    @Autowired
    private SupplierRepository supplierRepository;

    private String unmatchedParamFor(String param) {
        return String.format("%s doesn't match!", param);
    }

    @Before
    public void init() {
        modelMapper = new ModelMapper();
        supplierService = new SupplierServiceImpl(supplierRepository, modelMapper);
        testSupplier = new SupplierServiceModel();
        testSupplier.setName("FirstTestName");
    }

    @Test
    public void saveSupplier_whenSaveCorrectSupplier_expectSameOne() {
        SupplierServiceModel actual = supplierService.saveSupplier(testSupplier);

        SupplierServiceModel expected = modelMapper
                .map(supplierRepository.findAll().get(0), SupplierServiceModel.class);

        assertEquals(unmatchedParamFor("Id"), expected.getId(), actual.getId());
        assertEquals(unmatchedParamFor("Name"), expected.getName(), actual.getName());
        assertEquals(unmatchedParamFor("Importer"), expected.isImporter(), actual.isImporter());
    }

    @Test(expected = Exception.class)
    public void saveSupplier_whenSaveNullSupplier_expectException(){
        supplierService.saveSupplier(new SupplierServiceModel());
    }

    @Test
    public void editSupplier_whenEditSupplier_expectEditedValues() {
        SupplierServiceModel supplier = supplierService.saveSupplier(testSupplier);

        String name = "DifferentTestName";

        supplier.setName(name);
        supplier.setImporter(false);

        SupplierServiceModel expected = supplierService.editSupplier(supplier);

        assertEquals(unmatchedParamFor("Name"), expected.getName(), name);
        assertFalse(unmatchedParamFor("Importer"), expected.isImporter());
    }

    @Test
    public void editSupplier_whenSameSupplier_expectSameValues() {
        SupplierServiceModel actual = supplierService.saveSupplier(testSupplier);

        SupplierServiceModel expected = supplierService.editSupplier(actual);

        assertEquals(unmatchedParamFor("Name"), expected.getName(), actual.getName());
        assertEquals(unmatchedParamFor("Importer"), expected.isImporter(), actual.isImporter());
    }

    @Test(expected = Exception.class)
    public void editSupplier_whenIdIsNull_expectException() {
        supplierService.saveSupplier(testSupplier);

        supplierService.editSupplier(testSupplier);
    }

    @Test(expected = Exception.class)
    public void editSupplier_whenNameIsNull_expectException() {
        SupplierServiceModel supplier = supplierService.saveSupplier(testSupplier);

        supplier.setName(null);
        supplierService.editSupplier(supplier);
    }

    @Test
    public void deleteSupplier_whenDeletedSupplier_expectSameSupplier() {
        SupplierServiceModel actual = supplierService.saveSupplier(testSupplier);

        SupplierServiceModel expected = supplierService.deleteSupplier(actual.getId());

        assertEquals(unmatchedParamFor("Name"), expected.getName(), actual.getName());
        assertEquals(unmatchedParamFor("Importer"), expected.isImporter(), actual.isImporter());
    }

    @Test
    public void deleteSupplier_whenDeletedSupplier_expectNoSupplierInRepository() {
        SupplierServiceModel supplier = supplierService.saveSupplier(testSupplier);

        supplierService.deleteSupplier(supplier.getId());

        assertNull(supplierRepository.findById(supplier.getId()).orElse(null));
    }

    @Test(expected = Exception.class)
    public void deleteSupplier_whenNotExistSupplier_expectException() {
        SupplierServiceModel firstSupplier = supplierService.saveSupplier(testSupplier);
        testSupplier.setName("DifferentName");
        testSupplier.setImporter(false);
        SupplierServiceModel secondSupplier = supplierService.saveSupplier(testSupplier);

        supplierService.deleteSupplier(secondSupplier.getId());
        supplierService.deleteSupplier(secondSupplier.getId());
    }

    @Test(expected = Exception.class)
    public void deleteSupplier_whenIdIsNull_expectException() {
        supplierService.deleteSupplier(null);
    }

    @Test
    public void findSupplierById_whenFoundSupplier_expectSameSupplier() {
        SupplierServiceModel actual = supplierService.saveSupplier(testSupplier);

        SupplierServiceModel expected = supplierService.findSupplierById(actual.getId());

        assertEquals(unmatchedParamFor("Name"), expected.getName(), actual.getName());
        assertEquals(unmatchedParamFor("Importer"), expected.isImporter(), actual.isImporter());
    }

    @Test(expected = Exception.class)
    public void findSupplierById_whenIdIsNull_expectException() {
        supplierService.findSupplierById(null);
    }
}
