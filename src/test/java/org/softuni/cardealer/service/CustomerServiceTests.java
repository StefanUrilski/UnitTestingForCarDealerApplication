package org.softuni.cardealer.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.softuni.cardealer.domain.models.service.CustomerServiceModel;
import org.softuni.cardealer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.*;

@DataJpaTest
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class CustomerServiceTests {
    private CustomerServiceModel testCustomer;

    private ModelMapper modelMapper;
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    private String unmatchedParamFor(String param) {
        return String.format("%s doesn't match!", param);
    }

    @Before
    public void init() {
        modelMapper = new ModelMapper();
        customerService = new CustomerServiceImpl(customerRepository, modelMapper);

        testCustomer = new CustomerServiceModel();
        testCustomer.setName("TestName");
        testCustomer.setYoungDriver(true);
        testCustomer.setBirthDate(LocalDate.now());
    }

    @Test
    public void saveCustomer_whenSaveCorrectCustomer_expectSameOne() {
        CustomerServiceModel actual = customerService.saveCustomer(testCustomer);

        CustomerServiceModel expected = modelMapper
                .map(customerRepository.findAll().get(0), CustomerServiceModel.class);

        assertEquals(unmatchedParamFor("Id"), expected.getId(), actual.getId());
        assertEquals(unmatchedParamFor("Name"), expected.getName(), actual.getName());
        assertEquals(unmatchedParamFor("Birth Date"), expected.getBirthDate(), actual.getBirthDate());
        assertEquals(unmatchedParamFor("Young Driver"), expected.isYoungDriver(), actual.isYoungDriver());
    }

    @Test(expected = Exception.class)
    public void saveCustomer_whenSaveNullCustomer_expectException(){
        customerService.saveCustomer(new CustomerServiceModel());
    }

    @Test
    public void editCustomer_whenEditCustomer_expectEditedValues() {
        CustomerServiceModel customer = customerService.saveCustomer(testCustomer);

        String name = "DifferentTestName";
        LocalDate date = LocalDate.parse("2017-05-03",
                DateTimeFormatter.ISO_LOCAL_DATE);

        customer.setName(name);
        customer.setBirthDate(date);
        customer.setYoungDriver(false);

        CustomerServiceModel expected = customerService.editCustomer(customer);

        assertEquals(unmatchedParamFor("Name"), expected.getName(), name);
        assertEquals(unmatchedParamFor("Birth Date"), expected.getBirthDate(), date);
        assertFalse(unmatchedParamFor("Young Driver"), expected.isYoungDriver());
    }

    @Test
    public void editCustomer_whenSameCustomer_expectSameValues() {
        CustomerServiceModel actual = customerService.saveCustomer(testCustomer);

        CustomerServiceModel expected = customerService.editCustomer(actual);

        assertEquals(unmatchedParamFor("Id"), expected.getId(), actual.getId());
        assertEquals(unmatchedParamFor("Name"), expected.getName(), actual.getName());
        assertEquals(unmatchedParamFor("Birth Date"), expected.getBirthDate(), actual.getBirthDate());
        assertEquals(unmatchedParamFor("Young Driver"), expected.isYoungDriver(), actual.isYoungDriver());
    }

    @Test(expected = Exception.class)
    public void editCustomer_whenIdIsNull_expectException() {
        customerService.saveCustomer(testCustomer);

        customerService.editCustomer(testCustomer);
    }

    @Test(expected = Exception.class)
    public void editCustomer_whenNameIsNull_expectException() {
        CustomerServiceModel customer = customerService.saveCustomer(testCustomer);

        customer.setName(null);
        customerService.editCustomer(customer);
    }

    @Test
    public void deleteCustomer_whenDeletedCustomer_expectSameCustomer() {
        CustomerServiceModel actual = customerService.saveCustomer(testCustomer);

        CustomerServiceModel expected = customerService.deleteCustomer(actual.getId());

        assertEquals(unmatchedParamFor("Name"), expected.getId(), actual.getId());
        assertEquals(unmatchedParamFor("Name"), expected.getName(), actual.getName());
        assertEquals(unmatchedParamFor("Birth Date"), expected.getBirthDate(), actual.getBirthDate());
        assertEquals(unmatchedParamFor("Young Driver"), expected.isYoungDriver(), actual.isYoungDriver());
    }

    @Test
    public void deleteCustomer_whenDeletedCustomer_expectNoCustomerInRepository() {
        CustomerServiceModel supplier = customerService.saveCustomer(testCustomer);

        customerService.deleteCustomer(supplier.getId());

        assertNull(customerRepository.findById(supplier.getId()).orElse(null));
    }

    @Test(expected = Exception.class)
    public void deleteCustomer_whenNotExistCustomer_expectException() {
        customerService.saveCustomer(testCustomer);
        testCustomer.setName("DifferentName");
        testCustomer.setYoungDriver(true);
        CustomerServiceModel secondCustomer = customerService.saveCustomer(testCustomer);

        customerService.deleteCustomer(secondCustomer.getId());
        customerService.deleteCustomer(secondCustomer.getId());
    }

    @Test(expected = Exception.class)
    public void deleteCustomer_whenIdIsNull_expectException() {
        customerService.deleteCustomer(null);
    }

    @Test
    public void findCustomerById_whenFoundCustomer_expectSameCustomer() {
        CustomerServiceModel actual = customerService.saveCustomer(testCustomer);

        CustomerServiceModel expected = customerService.findCustomerById(actual.getId());

        assertEquals(unmatchedParamFor("Name"), expected.getName(), actual.getName());
        assertEquals(unmatchedParamFor("Birth Date"), expected.getBirthDate(), actual.getBirthDate());
        assertEquals(unmatchedParamFor("Young Driver"), expected.isYoungDriver(), actual.isYoungDriver());
    }

    @Test(expected = Exception.class)
    public void findCustomerById_whenIdIsNull_expectException() {
        customerService.findCustomerById(null);
    }
}
