package com.ainigma100.departmentapi.service.impl;

import com.ainigma100.departmentapi.dto.EmployeeAndDepartmentDTO;
import com.ainigma100.departmentapi.dto.EmployeeDTO;
import com.ainigma100.departmentapi.dto.EmployeeSearchCriteriaDTO;
import com.ainigma100.departmentapi.entity.Department;
import com.ainigma100.departmentapi.entity.Employee;
import com.ainigma100.departmentapi.exception.BusinessLogicException;
import com.ainigma100.departmentapi.exception.ResourceAlreadyExistException;
import com.ainigma100.departmentapi.exception.ResourceNotFoundException;
import com.ainigma100.departmentapi.mapper.EmployeeMapper;
import com.ainigma100.departmentapi.repository.DepartmentRepository;
import com.ainigma100.departmentapi.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/*
 * @ExtendWith(MockitoExtension.class) informs Mockito that we are using
 * mockito annotations to mock the dependencies
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    // @InjectMocks creates the mock object of the class and injects the mocks
    // that are marked with the annotations @Mock into it.
    @InjectMocks
    private EmployeeServiceImpl employeeService;


    private Employee employee;
    private EmployeeDTO employeeDTO;
    private Employee employee2;
    private EmployeeDTO employeeDTO2;
    private Department department;
    private Department department2;
    private EmployeeSearchCriteriaDTO employeeSearchCriteria;

    private Employee updatedEmployee;
    private EmployeeDTO updatedEmployeeDTO;

    private EmployeeAndDepartmentDTO employeeAndDepartmentDTO;


    @BeforeEach
    void setUp() {

        department = new Department();
        department.setId(1L);
        department.setDepartmentCode("ABC");
        department.setDepartmentName("Department 1");
        department.setDepartmentDescription("Description 1");
        department.setEmployees(new HashSet<>(Collections.singleton(employee)));

        employee = new Employee();
        employee.setId("emp01");
        employee.setFirstName("John");
        employee.setLastName("Wick");
        employee.setEmail("jwick@gmail.com");
        employee.setSalary(BigDecimal.valueOf(40_000_000));
        employee.setDepartment(department);

        employee2 = new Employee();
        employee2.setId("emp02");
        employee2.setFirstName("Luffy");
        employee2.setLastName("Monkey D.");
        employee2.setEmail("mluffy@gmail.com");
        employee2.setSalary(BigDecimal.valueOf(50_000_000));
        employee2.setDepartment(department);

        employeeDTO = new EmployeeDTO();
        employeeDTO.setId("emp01");
        employeeDTO.setFirstName("John");
        employeeDTO.setLastName("Wick");
        employeeDTO.setEmail("jwick@gmail.com");
        employeeDTO.setSalary(BigDecimal.valueOf(40_000_000));

        employeeDTO2 = new EmployeeDTO();
        employeeDTO2.setId("emp02");
        employeeDTO2.setFirstName("Luffy");
        employeeDTO2.setLastName("Monkey D.");
        employeeDTO2.setEmail("mluffy@gmail.com");
        employeeDTO2.setSalary(BigDecimal.valueOf(50_000_000));

        department2 = new Department();
        department2.setId(2L);
        department2.setDepartmentCode("ABC");
        department2.setDepartmentName("Department 1");
        department2.setDepartmentDescription("Description 1");
        department2.setEmployees(new HashSet<>(Collections.singleton(employee2)));

        employeeSearchCriteria = new EmployeeSearchCriteriaDTO();
        employeeSearchCriteria.setPage(0);
        employeeSearchCriteria.setSize(10);
        employeeSearchCriteria.setFirstName("John");

        updatedEmployee = new Employee();
        updatedEmployee.setId("emp01");
        updatedEmployee.setFirstName("Marco");
        updatedEmployee.setLastName("Polo");
        updatedEmployee.setEmail("mpolo@gmail.com");
        updatedEmployee.setSalary(BigDecimal.valueOf(8_000_000));

        updatedEmployeeDTO = new EmployeeDTO();
        updatedEmployeeDTO.setId("emp01");
        updatedEmployeeDTO.setFirstName("Marco");
        updatedEmployeeDTO.setLastName("Polo");
        updatedEmployeeDTO.setEmail("mpolo@gmail.com");
        updatedEmployeeDTO.setSalary(BigDecimal.valueOf(8_000_000));


        employeeAndDepartmentDTO = new EmployeeAndDepartmentDTO();
        employeeAndDepartmentDTO.setId("emp01");
        employeeAndDepartmentDTO.setFirstName("John");
        employeeAndDepartmentDTO.setLastName("Wick");
        employeeAndDepartmentDTO.setEmail("jwick@gmail.com");
        employeeAndDepartmentDTO.setSalary(BigDecimal.valueOf(40_000_000));
        employeeAndDepartmentDTO.setDepartment(new EmployeeAndDepartmentDTO.DepartmentDTO(
                2L,
                "DEP_ABC",
                "Department Name ABC",
                "Department Description ABC"
        ));

    }

    @Test
    @DisplayName("Given a valid department ID and employee DTO, when creating an employee, then return the employee DTO")
    void givenValidDepartmentIdAndEmployeeDTO_whenCreateEmployee_thenReturnEmployeeDTO() {

        // given - precondition or setup
        given(employeeRepository.findByEmail(employeeDTO.getEmail())).willReturn(null);
        given(employeeMapper.employeeDtoToEmployee(employeeDTO)).willReturn(employee);
        given(departmentRepository.findById(department.getId())).willReturn(Optional.of(department));
        given(employeeRepository.save(employee)).willReturn(employee);
        given(employeeMapper.employeeToEmployeeDto(employee)).willReturn(employeeDTO);

        // when - action or behavior that we are going to test
        EmployeeDTO result = employeeService.createEmployee(department.getId(), employeeDTO);

        // then - verify the output
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo(employeeDTO.getFirstName());
        assertThat(result.getLastName()).isEqualTo(employeeDTO.getLastName());
        assertThat(result.getEmail()).isEqualTo(employeeDTO.getEmail());
        assertThat(result.getSalary()).isEqualByComparingTo(employeeDTO.getSalary());

        verify(employeeRepository, times(1)).findByEmail(employeeDTO.getEmail());
        verify(employeeMapper, times(1)).employeeDtoToEmployee(employeeDTO);
        verify(departmentRepository, times(1)).findById(department.getId());
        verify(employeeRepository, times(1)).save(employee);
        verify(employeeMapper, times(1)).employeeToEmployeeDto(employee);
    }

    @Test
    @DisplayName("Given an existing employee email, when creating an employee, then throw ResourceAlreadyExistException")
    void givenExistingEmployeeEmail_whenCreateEmployee_thenThrowResourceAlreadyExistException() {

        // given - precondition or setup
        given(employeeRepository.findByEmail(employeeDTO.getEmail())).willReturn(employee);

        // when/then - verify that the ResourceAlreadyExistException is thrown
        assertThatExceptionOfType(ResourceAlreadyExistException.class)
                .isThrownBy(() -> employeeService.createEmployee(department.getId(), employeeDTO))
                .withMessage("Resource Employee with email : '" + employeeDTO.getEmail() + "' already exist");

        verify(employeeRepository, times(1)).findByEmail(employeeDTO.getEmail());
        verify(employeeMapper, never()).employeeDtoToEmployee(any(EmployeeDTO.class));
        verify(departmentRepository, never()).findById(any(Long.class));
        verify(employeeRepository, never()).save(any(Employee.class));
        verify(employeeMapper, never()).employeeToEmployeeDto(any(Employee.class));
    }


    @Test
    @DisplayName("Given an employee search criteria DTO, when getting all employees using pagination, then return employee DTO page")
    void givenEmployeeSearchCriteriaDTO_whenGetAllEmployeesUsingPagination_thenReturnEmployeeDTOPage() {

        // given - precondition or setup
        List<Employee> employeeList = Arrays.asList(employee, employee2);
        List<EmployeeDTO> employeeDTOList = Arrays.asList(employeeDTO, employeeDTO2);
        Page<Employee> pageFromDb = new PageImpl<>(employeeList);

        PageRequest pageRequest = PageRequest.of(employeeSearchCriteria.getPage(), employeeSearchCriteria.getSize());

        given(employeeRepository.getAllEmployeesUsingPagination(employeeSearchCriteria, pageRequest))
                .willReturn(pageFromDb);

        given(employeeMapper.employeeToEmployeeDto(pageFromDb.getContent()))
                .willReturn(employeeDTOList);


        // when - action or behaviour that we are going to test
        Page<EmployeeDTO> result = employeeService.getAllEmployeesUsingPagination(employeeSearchCriteria);

        // then - verify the output
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getId()).isEqualTo("emp01");
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("John");
        assertThat(result.getContent().get(1).getId()).isEqualTo("emp02");
        assertThat(result.getContent().get(1).getFirstName()).isEqualTo("Luffy");

        verify(employeeRepository, times(1)).getAllEmployeesUsingPagination(employeeSearchCriteria, pageRequest);
        verify(employeeMapper, times(1)).employeeToEmployeeDto(employeeList);
    }

    @Test
    @DisplayName("Given an employee search criteria DTO that does not match, when getting all employees using pagination, then return empty page")
    void givenEmployeeSearchCriteriaDTOThatDoesNotMatch_whenGetAllEmployeesUsingPagination_thenReturnEmptyPage() {

        // given - precondition or setup
        Page<Employee> pageFromDb = new PageImpl<>(Collections.emptyList());

        PageRequest pageRequest = PageRequest.of(employeeSearchCriteria.getPage(), employeeSearchCriteria.getSize());

        employeeSearchCriteria.setLastName("Non existent value");

        given(employeeRepository.getAllEmployeesUsingPagination(employeeSearchCriteria, pageRequest))
                .willReturn(pageFromDb);

        given(employeeMapper.employeeToEmployeeDto(pageFromDb.getContent()))
                .willReturn(Collections.emptyList());


        // when - action or behaviour that we are going to test
        Page<EmployeeDTO> result = employeeService.getAllEmployeesUsingPagination(employeeSearchCriteria);

        // then - verify the output
        assertThat(result.getContent()).isEmpty();

        verify(employeeRepository, times(1)).getAllEmployeesUsingPagination(employeeSearchCriteria, pageRequest);
        verify(employeeMapper, times(1)).employeeToEmployeeDto(Collections.emptyList());
    }

    @Test
    @DisplayName("Given an existing department id, when getting employees by department id, then return list of employee DTO")
    void givenDepartmentId_whenGetEmployeesByDepartmentId_thenReturnEmployeeList() {

        // given - precondition or setup
        List<Employee> employeeList = Arrays.asList(employee, employee2);
        List<EmployeeDTO> employeeDTOList = Arrays.asList(employeeDTO, employeeDTO2);

        given(employeeRepository.findByDepartmentId(department.getId())).willReturn(employeeList);
        given(employeeMapper.employeeToEmployeeDto(employeeList)).willReturn(employeeDTOList);

        // when - action or behaviour that we are going to test
        List<EmployeeDTO> result = employeeService.getEmployeesByDepartmentId(department.getId());

        // then - verify the output
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getFirstName()).isEqualTo(employee.getFirstName());
        assertThat(result.get(0).getLastName()).isEqualTo(employee.getLastName());
        assertThat(result.get(0).getEmail()).isEqualTo(employee.getEmail());
        assertThat(result.get(0).getSalary()).isEqualByComparingTo(employee.getSalary());
        assertThat(result.get(1).getFirstName()).isEqualTo(employee2.getFirstName());
        assertThat(result.get(1).getLastName()).isEqualTo(employee2.getLastName());
        assertThat(result.get(1).getEmail()).isEqualTo(employee2.getEmail());
        assertThat(result.get(1).getSalary()).isEqualByComparingTo(employee2.getSalary());

        verify(employeeRepository, times(1)).findByDepartmentId(department.getId());
        verify(employeeMapper, times(1)).employeeToEmployeeDto(employeeList);
    }


    @Test
    @DisplayName("Given valid department id and employee id, when get employee by id, then return an employee DTO")
    void givenDepartmentIdAndEmployeeId_whenGetEmployeeById_thenReturnEmployeeDTO() {

        // given - precondition or setup
        given(departmentRepository.findById(department.getId())).willReturn(Optional.of(department));
        given(employeeRepository.findById(employee.getId())).willReturn(Optional.of(employee));
        given(employeeMapper.employeeToEmployeeDto(employee)).willReturn(employeeDTO);

        // when - action or behaviour that we are going to test
        EmployeeDTO result = employeeService.getEmployeeById(department.getId(), employee.getId());

        // then - verify the output
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(employee.getId());
        assertThat(result.getFirstName()).isEqualTo(employee.getFirstName());
        assertThat(result.getSalary()).isEqualByComparingTo(employee.getSalary());

        verify(departmentRepository, times(1)).findById(department.getId());
        verify(employeeRepository, times(1)).findById(employee.getId());
        verify(employeeMapper, times(1)).employeeToEmployeeDto(employee);
    }

    @Test
    @DisplayName("Given an invalid department id, when get employee by id, then throw ResourceNotFoundException")
    void givenInvalidDepartmentId_whenGetEmployeeById_thenThrowResourceNotFoundException() {

        // given - precondition or setup
        Long departmentId = 123L;
        given(departmentRepository.findById(departmentId)).willReturn(Optional.empty());

        // when / then - action or behavior that we are going to test
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> employeeService.getEmployeeById(departmentId, employee.getId()))
                .withMessage("Department with id : '" + departmentId + "' not found");


        // then - verify the output
        verify(departmentRepository, times(1)).findById(departmentId);
        verify(employeeRepository, never()).findById(any(String.class));
        verify(employeeMapper, never()).employeeToEmployeeDto(any(Employee.class));
    }

    @Test
    @DisplayName("Given valid department id and invalid employee id, when get employee by id, then throw ResourceNotFoundException")
    void givenValidDepartmentIdAndInvalidEmployeeId_whenGetEmployeeById_thenThrowResourceNotFoundException() {

        // given - precondition or setup
        String employeeId = "invalid id";
        given(departmentRepository.findById(department.getId())).willReturn(Optional.of(department));
        given(employeeRepository.findById(employeeId)).willReturn(Optional.empty());

        // when - action or behaviour that we are going to test
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> employeeService.getEmployeeById(department.getId(), employeeId))
                .withMessage("Employee with id : '" + employeeId + "' not found");

        // then - verify the output
        verify(departmentRepository, times(1)).findById(department.getId());
        verify(employeeRepository, times(1)).findById(employeeId);
        verify(employeeMapper, never()).employeeToEmployeeDto(any(Employee.class));
    }

    @Test
    @DisplayName("Given department id and employee id which do not have an association, when get employee by id, then throw BusinessLogicException")
    void givenDepartmentIdAndEmployeeIdNotAssociated_whenGetEmployeeById_thenThrowBusinessLogicException() {

        // given - precondition or setup
        given(departmentRepository.findById(department2.getId())).willReturn(Optional.of(department2));
        given(employeeRepository.findById(employee.getId())).willReturn(Optional.of(employee));

        // when - action or behaviour that we are going to test
        assertThatExceptionOfType(BusinessLogicException.class)
                .isThrownBy(() -> employeeService.getEmployeeById(department2.getId(), employee.getId()))
                .withMessage("Employee does not belong to Department");

        verify(departmentRepository, times(1)).findById(department2.getId());
        verify(employeeRepository, times(1)).findById(employee.getId());
        verify(employeeMapper, never()).employeeToEmployeeDto(any(Employee.class));
    }


    @Test
    @DisplayName("Given valid inputs, when updating employee by ID, then return updated EmployeeDTO")
    void givenValidInputs_whenUpdateEmployeeById_thenThenReturnUpdatedEmployeeDTO() {

        // given - precondition or setup
        given(departmentRepository.findById(department.getId())).willReturn(Optional.of(department));
        given(employeeRepository.findById(employee.getId())).willReturn(Optional.of(employee));
        given(employeeMapper.employeeDtoToEmployee(updatedEmployeeDTO)).willReturn(updatedEmployee);
        given(employeeRepository.save(updatedEmployee)).willReturn(updatedEmployee);
        given(employeeMapper.employeeToEmployeeDto(updatedEmployee)).willReturn(updatedEmployeeDTO);

        // when - action or behaviour that we are going to test
        EmployeeDTO result = employeeService.updateEmployeeById(department.getId(), employee.getId(), updatedEmployeeDTO);

        // then - verify the output
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(employee.getId());

        verify(departmentRepository, times(1)).findById(department.getId());
        verify(employeeRepository, times(1)).findById(employee.getId());
        verify(employeeMapper, times(1)).employeeDtoToEmployee(updatedEmployeeDTO);
        verify(employeeRepository, times(1)).save(updatedEmployee);
        verify(employeeMapper, times(1)).employeeToEmployeeDto(updatedEmployee);
    }

    @Test
    @DisplayName("Given invalid department id, when updating employee by ID, then throw ResourceNotFoundException")
    void givenInvalidDepartmentId_whenUpdateEmployeeById_thenThenThrowResourceNotFoundException() {

        // given - precondition or setup
        Long departmentId = 123L;
        given(departmentRepository.findById(departmentId)).willReturn(Optional.empty());


        // when/then - verify that ResourceNotFoundException is thrown
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> employeeService.updateEmployeeById(departmentId, employee.getId(), updatedEmployeeDTO))
                .withMessage("Department with id : '" + departmentId + "' not found");

        verify(departmentRepository, times(1)).findById(departmentId);
        verify(employeeRepository, never()).findById(any(String.class));
        verify(employeeMapper, never()).employeeDtoToEmployee(any(EmployeeDTO.class));
        verify(employeeRepository, never()).save(any(Employee.class));
        verify(employeeMapper, never()).employeeToEmployeeDto(any(Employee.class));
    }

    @Test
    @DisplayName("Given invalid employee id, when updating employee by ID, then throw ResourceNotFoundException")
    void givenInvalidEmployeeId_whenUpdateEmployeeById_thenThenThrowResourceNotFoundException() {

        // given - precondition or setup
        String employeeId = "invalid emp id";
        given(departmentRepository.findById(department.getId())).willReturn(Optional.of(department));
        given(employeeRepository.findById(employeeId)).willReturn(Optional.empty());


        // when/then - verify that ResourceNotFoundException is thrown
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> employeeService.updateEmployeeById(department.getId(), employeeId, updatedEmployeeDTO))
                .withMessage("Employee with id : '" + employeeId + "' not found");

        verify(departmentRepository, times(1)).findById(department.getId());
        verify(employeeRepository, times(1)).findById(employeeId);
        verify(employeeMapper, never()).employeeDtoToEmployee(any(EmployeeDTO.class));
        verify(employeeRepository, never()).save(any(Employee.class));
        verify(employeeMapper, never()).employeeToEmployeeDto(any(Employee.class));
    }

    @Test
    @DisplayName("Given valid department id and employee id which are not associated, when updating employee by ID, then throw BusinessLogicException")
    void givenValidInputsWhichAreNotAssociated_whenUpdateEmployeeById_thenThenThrowBusinessLogicException() {

        // given - precondition or setup
        given(departmentRepository.findById(department2.getId())).willReturn(Optional.of(department2));
        given(employeeRepository.findById(employee.getId())).willReturn(Optional.of(employee));


        // when/then - verify that ResourceNotFoundException is thrown
        assertThatExceptionOfType(BusinessLogicException.class)
                .isThrownBy(() -> employeeService.updateEmployeeById(department2.getId(), employee.getId(), updatedEmployeeDTO))
                .withMessage("Employee does not belong to Department");

        verify(departmentRepository, times(1)).findById(department2.getId());
        verify(employeeRepository, times(1)).findById(employee.getId());
        verify(employeeMapper, never()).employeeDtoToEmployee(any(EmployeeDTO.class));
        verify(employeeRepository, never()).save(any(Employee.class));
        verify(employeeMapper, never()).employeeToEmployeeDto(any(Employee.class));
    }


    @Test
    @DisplayName("Given a valid department ID and employee ID, when deleting an employee, then the employee is deleted")
    void givenValidDepartmentIdAndEmployeeId_whenDeleteEmployee_thenEmployeeIsDeleted() {

        // given - precondition or setup
        given(departmentRepository.findById(department.getId())).willReturn(Optional.of(department));
        given(employeeRepository.findById(employee.getId())).willReturn(Optional.of(employee));

        // when - action or behavior that we are going to test
        employeeService.deleteEmployee(department.getId(), employee.getId());

        // then - verify that employeeRepository.delete is called
        verify(employeeRepository, times(1)).delete(employee);
    }

    @Test
    @DisplayName("Given an invalid department ID, when deleting an employee, then throw ResourceNotFoundException")
    void givenInvalidDepartmentId_whenDeleteEmployee_thenThrowResourceNotFoundException() {

        // given - precondition or setup
        Long departmentId = 123L;

        given(departmentRepository.findById(departmentId)).willReturn(Optional.empty());

        // when/then - verify that ResourceNotFoundException is thrown
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> employeeService.deleteEmployee(departmentId, employee.getId()))
                .withMessage("Department with id : '" + departmentId + "' not found");

        verify(employeeRepository, never()).findById(anyString());
        verify(employeeRepository, never()).delete(any(Employee.class));
    }

    @Test
    @DisplayName("Given an invalid employee ID, when deleting an employee, then throw ResourceNotFoundException")
    void givenInvalidEmployeeId_whenDeleteEmployee_thenThrowResourceNotFoundException() {

        // given - precondition or setup
        String employeeId = "456";

        given(departmentRepository.findById(department.getId())).willReturn(Optional.of(department));
        given(employeeRepository.findById(employeeId)).willReturn(Optional.empty());

        // when/then - verify that ResourceNotFoundException is thrown
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> employeeService.deleteEmployee(department.getId(), employeeId))
                .withMessage("Employee with id : '" + employeeId + "' not found");

        verify(employeeRepository, times(1)).findById(employeeId);
        verify(employeeRepository, never()).delete(any(Employee.class));
    }

    @Test
    @DisplayName("Given an employee not belonging to the department, when deleting the employee, then throw BusinessLogicException")
    void givenEmployeeNotBelongingToDepartment_whenDeleteEmployee_thenThrowBusinessLogicException() {

        // given
        given(departmentRepository.findById(department2.getId())).willReturn(Optional.of(department2));
        given(employeeRepository.findById(employee.getId())).willReturn(Optional.of(employee));

        // when/then
        assertThatExceptionOfType(BusinessLogicException.class)
                .isThrownBy(() -> employeeService.deleteEmployee(department2.getId(), employee.getId()))
                .withMessage("Employee does not belong to Department");


        verify(employeeRepository, never()).delete(any(Employee.class));
    }


    @Test
    @DisplayName("Given null department or employee department, when checking if employee belongs to department, then return false")
    void givenNullDepartmentOrEmployeeDepartment_whenEmployeeBelongsToDepartment_thenReturnFalse() {

        // given - precondition or setup
        employee.setDepartment(null);

        given(departmentRepository.findById(department.getId())).willReturn(Optional.of(department));
        given(employeeRepository.findById(employee.getId())).willReturn(Optional.of(employee));

        // when/then - verify that BusinessLogicException is thrown
        assertThatExceptionOfType(BusinessLogicException.class)
                .isThrownBy(() -> employeeService.deleteEmployee(department.getId(), employee.getId()))
                .withMessage("Employee does not belong to Department");

        verify(employeeRepository, never()).delete(any(Employee.class));
    }


    @Test
    @DisplayName("Given valid employee email, when get employee and department by employee email, then return employee and department")
    void givenEmployeeEmail_whenGetEmployeeAndDepartmentByEmployeeEmail_thenReturnEmployeeAndDepartment() {

        // given - precondition or setup
        given(employeeRepository.getEmployeeAndDepartmentByEmployeeEmail(employee.getEmail())).willReturn(employee);
        given(employeeMapper.employeeToEmployeeAndDepartmentDto(employee)).willReturn(employeeAndDepartmentDTO);

        // when - action or behaviour that we are going to test
        EmployeeAndDepartmentDTO result = employeeService.getEmployeeAndDepartmentByEmployeeEmail(employee.getEmail());

        // then - verify the output
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(employeeAndDepartmentDTO.getId());
        assertThat(result.getFirstName()).isEqualTo(employeeAndDepartmentDTO.getFirstName());
        assertThat(result.getSalary()).isEqualByComparingTo(employeeAndDepartmentDTO.getSalary());

        verify(employeeRepository, times(1)).getEmployeeAndDepartmentByEmployeeEmail(employee.getEmail());
        verify(employeeMapper, times(1)).employeeToEmployeeAndDepartmentDto(employee);
    }

    @Test
    @DisplayName("Given invalid employee email, when get employee and department by employee email, then throw ResourceNotFoundException")
    void givenInvalidEmployeeEmail_whenGetEmployeeAndDepartmentByEmployeeEmail_thenThrowResourceNotFoundException() {

        // given - precondition or setup
        given(employeeRepository.getEmployeeAndDepartmentByEmployeeEmail(employee.getEmail())).willReturn(null);

        // when - action or behaviour that we are going to test
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> employeeService.getEmployeeAndDepartmentByEmployeeEmail(employee.getEmail()))
                .withMessage("Employee with email : '" + employee.getEmail() + "' not found");

        // then - verify the output
        verify(employeeRepository, times(1)).getEmployeeAndDepartmentByEmployeeEmail(employee.getEmail());
        verify(employeeMapper, never()).employeeToEmployeeAndDepartmentDto(any(Employee.class));
    }

/*LLM GENERATED TESTS */
@Test
@DisplayName("Given null department ID when creating an employee, then throw IllegalArgumentException")
void givenNullDepartmentId_whenCreateEmployee_thenThrowIllegalArgumentException() {
    EmployeeDTO employeeDTO = new EmployeeDTO();
    employeeDTO.setFirstName("John");
    employeeDTO.setEmail("john.doe@email.com");

    assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> employeeService.createEmployee(null, employeeDTO))
            .withMessage("Department ID cannot be null");
}


@Test
@DisplayName("Given invalid salary when creating an employee, then throw IllegalArgumentException")
void givenInvalidSalary_whenCreateEmployee_thenThrowIllegalArgumentException() {
    EmployeeDTO employeeDTO = new EmployeeDTO();
    employeeDTO.setFirstName("John");
    employeeDTO.setEmail("john.doe@email.com");

    // Test negative salary
    employeeDTO.setSalary(BigDecimal.valueOf(-5000));
    assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> employeeService.createEmployee(1L, employeeDTO))
            .withMessage("Salary must be greater than zero");

    // Test zero salary
    employeeDTO.setSalary(BigDecimal.ZERO);
    assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> employeeService.createEmployee(1L, employeeDTO))
            .withMessage("Salary must be greater than zero");
}


@Test
@DisplayName("Given invalid email format when creating an employee, then throw IllegalArgumentException")
void givenInvalidEmailFormat_whenCreateEmployee_thenThrowIllegalArgumentException() {
    EmployeeDTO employeeDTO = new EmployeeDTO();
    employeeDTO.setFirstName("John");
    employeeDTO.setEmail("invalid-email"); // No @ symbol

    assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> employeeService.createEmployee(1L, employeeDTO))
            .withMessage("Invalid email format");
}


    @Test
    @DisplayName("Given department ID and employee ID that do not match, when updating employee, then throw BusinessLogicException")
    void givenDepartmentIdAndEmployeeIdNotMatching_whenUpdateEmployee_thenThrowBusinessLogicException() {
        Department department = new Department();
        department.setId(2L);

        Employee employee = new Employee();
        employee.setId("emp01");
        employee.setDepartment(new Department()); // Different department

        given(departmentRepository.findById(2L)).willReturn(Optional.of(department));
        given(employeeRepository.findById("emp01")).willReturn(Optional.of(employee));

        EmployeeDTO updatedEmployeeDTO = new EmployeeDTO();
        updatedEmployeeDTO.setFirstName("Updated Name");

        assertThatExceptionOfType(BusinessLogicException.class)
                .isThrownBy(() -> employeeService.updateEmployeeById(2L, "emp01", updatedEmployeeDTO))
                .withMessage("Employee does not belong to Department");
    }
/* manual test improvements */
@Test
@DisplayName("Given null EmployeeDTO when creating an employee, then throw IllegalArgumentException")
void givenNullEmployeeDTO_whenCreateEmployee_thenThrowIllegalArgumentException() {
    // IMPROVEMENT: Previously, null DTOs were not tested
    assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> employeeService.createEmployee(1L, null))
            .withMessage("Employee data must not be null");
}

@Test
@DisplayName("Given EmployeeDTO with null email when creating an employee, then throw IllegalArgumentException")
void givenEmployeeDTONullEmail_whenCreateEmployee_thenThrowIllegalArgumentException() {
    // ✅ IMPROVEMENT: Covers missing validation for null email
    employeeDTO.setEmail(null);
    
    assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> employeeService.createEmployee(1L, employeeDTO))
            .withMessage("Invalid email format");
}
@Test
@DisplayName("Given null department ID when deleting an employee, then throw IllegalArgumentException")
void givenNullDepartmentId_whenDeleteEmployee_thenThrowIllegalArgumentException() {
// IMPROVEMENT: Tests null department ID handling
assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> employeeService.deleteEmployee(null, "emp01"))
        .withMessage("Department ID must not be null");
}

@Test
@DisplayName("Given non-existing employee ID when deleting, then throw ResourceNotFoundException")
void givenNonExistingEmployeeId_whenDeleteEmployee_thenThrowResourceNotFoundException() {
// Ensure department exists before checking for employee
given(departmentRepository.findById(1L)).willReturn(Optional.of(department));

// Mock employee repository to return empty result
given(employeeRepository.findById("invalid-emp")).willReturn(Optional.empty());

assertThatExceptionOfType(ResourceNotFoundException.class)
        .isThrownBy(() -> employeeService.deleteEmployee(1L, "invalid-emp"))
        .withMessage("Employee with id : 'invalid-emp' not found");
}



@Test
@DisplayName("Given employee already deleted when deleting an employee, then throw BusinessLogicException")
void givenEmployeeAlreadyDeleted_whenDeleteEmployee_thenThrowBusinessLogicException() {
// Ensure department exists
given(departmentRepository.findById(1L)).willReturn(Optional.of(department));

// Simulate deleted employee (removed from department)
employee.setDepartment(null); 

given(employeeRepository.findById(employee.getId())).willReturn(Optional.of(employee));

assertThatExceptionOfType(BusinessLogicException.class)
        .isThrownBy(() -> employeeService.deleteEmployee(1L, employee.getId()))
        .withMessage("Employee does not belong to Department");
}
@Test
@DisplayName("Given valid department ID and employee ID when deleting, then delete successfully")
void givenValidDepartmentAndEmployee_whenDeleteEmployee_thenDeleteSuccessfully() {
// Covers successful deletion scenario
Department department = new Department();
department.setId(1L);

Employee employee = new Employee();
employee.setId("EMP001");
employee.setDepartment(department); // Belongs to same department!

given(departmentRepository.findById(1L)).willReturn(Optional.of(department));
given(employeeRepository.findById("EMP001")).willReturn(Optional.of(employee));

// Act
employeeService.deleteEmployee(1L, "EMP001");

// Assert
verify(employeeRepository, times(1)).delete(employee);
}


@Test
@DisplayName("Given null Employee ID when deleting, then throw IllegalArgumentException")
void givenNullEmployeeId_whenDeleteEmployee_thenThrowIllegalArgumentException() {
    // Covers scenario where employee ID is null
    Exception exception = assertThrows(IllegalArgumentException.class, 
        () -> employeeService.deleteEmployee(1L, null));

    assertEquals("Employee ID must not be null or empty", exception.getMessage());

    verify(employeeRepository, never()).delete(any(Employee.class));
}

@Test
@DisplayName("Given empty Employee ID when deleting, then throw IllegalArgumentException")
void givenEmptyEmployeeId_whenDeleteEmployee_thenThrowIllegalArgumentException() {
    // Covers scenario where employee ID is empty
    Exception exception = assertThrows(IllegalArgumentException.class, 
        () -> employeeService.deleteEmployee(1L, "  "));

    assertEquals("Employee ID must not be null or empty", exception.getMessage());

    verify(employeeRepository, never()).delete(any(Employee.class));
}

@Test
@DisplayName("Given non-existent Department ID when deleting Employee, then throw ResourceNotFoundException")
void givenNonExistentDepartmentId_whenDeleteEmployee_thenThrowResourceNotFoundException() {
    // Covers scenario where department does not exist
    given(departmentRepository.findById(1L)).willReturn(Optional.empty());

    Exception exception = assertThrows(ResourceNotFoundException.class, 
        () -> employeeService.deleteEmployee(1L, "EMP001"));

    assertEquals("Department with id : '1' not found", exception.getMessage());

    verify(departmentRepository, times(1)).findById(1L);
    verify(employeeRepository, never()).delete(any(Employee.class));
}

@Test
@DisplayName("Given Employee not in Department when deleting, then throw BusinessLogicException")
void givenEmployeeNotInDepartment_whenDeleteEmployee_thenThrowBusinessLogicException() {
    // Covers scenario where employee is in a different department
    Department department = new Department();
    department.setId(1L);

    Department otherDepartment = new Department();
    otherDepartment.setId(2L);

    Employee employee = new Employee();
    employee.setId("EMP001");
    employee.setDepartment(otherDepartment); // Employee belongs to a different department

    given(departmentRepository.findById(1L)).willReturn(Optional.of(department));
    given(employeeRepository.findById("EMP001")).willReturn(Optional.of(employee));

    Exception exception = assertThrows(BusinessLogicException.class, 
        () -> employeeService.deleteEmployee(1L, "EMP001"));

    assertEquals("Employee does not belong to Department", exception.getMessage());

    verify(departmentRepository, times(1)).findById(1L);
    verify(employeeRepository, times(1)).findById("EMP001");
    verify(employeeRepository, never()).delete(any(Employee.class));
}

@Test
@DisplayName("Given EmployeeDTO with null first name when creating an employee, then throw IllegalArgumentException")
void givenEmployeeDTONullFirstName_whenCreateEmployee_thenThrowIllegalArgumentException() {
    // Covers missing validation for null first name
    employeeDTO.setFirstName(null);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
        () -> employeeService.createEmployee(1L, employeeDTO));

    assertEquals("First name is required", exception.getMessage());
}

@Test
@DisplayName("Given EmployeeDTO with empty first name when creating an employee, then throw IllegalArgumentException")
void givenEmployeeDTOEmptyFirstName_whenCreateEmployee_thenThrowIllegalArgumentException() {
    // Covers missing validation for empty first name
    employeeDTO.setFirstName("  ");

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
        () -> employeeService.createEmployee(1L, employeeDTO));

    assertEquals("First name is required", exception.getMessage());
}

@Test
@DisplayName("Given EmployeeDTO with invalid email format when creating an employee, then throw IllegalArgumentException")
void givenEmployeeDTOInvalidEmail_whenCreateEmployee_thenThrowIllegalArgumentException() {
    // Covers invalid email format
    employeeDTO.setEmail("invalid-email");

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
        () -> employeeService.createEmployee(1L, employeeDTO));

    assertEquals("Invalid email format", exception.getMessage());
}

@Test
@DisplayName("Given EmployeeDTO with null salary when creating an employee, then throw IllegalArgumentException")
void givenEmployeeDTONullSalary_whenCreateEmployee_thenThrowIllegalArgumentException() {
    // Covers missing salary validation
    employeeDTO.setSalary(null);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
        () -> employeeService.createEmployee(1L, employeeDTO));

    assertEquals("Salary must be greater than zero", exception.getMessage());
}

@Test
@DisplayName("Given EmployeeDTO with zero salary when creating an employee, then throw IllegalArgumentException")
void givenEmployeeDTOZeroSalary_whenCreateEmployee_thenThrowIllegalArgumentException() {
    // Covers invalid zero salary case
    employeeDTO.setSalary(BigDecimal.ZERO);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
        () -> employeeService.createEmployee(1L, employeeDTO));

    assertEquals("Salary must be greater than zero", exception.getMessage());
}

@Test
@DisplayName("Given EmployeeDTO with negative salary when creating an employee, then throw IllegalArgumentException")
void givenEmployeeDTONegativeSalary_whenCreateEmployee_thenThrowIllegalArgumentException() {
    // Covers invalid negative salary case
    employeeDTO.setSalary(new BigDecimal("-1000"));

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
        () -> employeeService.createEmployee(1L, employeeDTO));

    assertEquals("Salary must be greater than zero", exception.getMessage());
}

@Test
@DisplayName("Given EmployeeDTO with duplicate email when creating an employee, then throw ResourceAlreadyExistException")
void givenDuplicateEmail_whenCreateEmployee_thenThrowResourceAlreadyExistException() {
    // Covers case when email already exists in DB
    given(employeeRepository.findByEmail(employeeDTO.getEmail())).willReturn(new Employee());

    ResourceAlreadyExistException exception = assertThrows(ResourceAlreadyExistException.class, 
        () -> employeeService.createEmployee(1L, employeeDTO));

    assertEquals("Resource Employee with email : '" + employeeDTO.getEmail() + "' already exist", exception.getMessage());
}


@Test
@DisplayName("Given non-existent department ID when creating an employee, then throw ResourceNotFoundException")
void givenNonExistentDepartmentId_whenCreateEmployee_thenThrowResourceNotFoundException() {
    // Covers scenario where department does not exist
    given(departmentRepository.findById(1L)).willReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
        () -> employeeService.createEmployee(1L, employeeDTO));

    assertEquals("Department with id : '1' not found", exception.getMessage());
}

@Test
@DisplayName("Given valid EmployeeDTO when creating an employee, then return created EmployeeDTO")
void givenValidEmployeeDTO_whenCreateEmployee_thenReturnEmployeeDTO() {
    // Covers successful employee creation case
    given(departmentRepository.findById(1L)).willReturn(Optional.of(department));
    given(employeeRepository.findByEmail(employeeDTO.getEmail())).willReturn(null);
    given(employeeMapper.employeeDtoToEmployee(employeeDTO)).willReturn(employee);
    given(employeeRepository.save(employee)).willReturn(employee);
    given(employeeMapper.employeeToEmployeeDto(employee)).willReturn(employeeDTO);

    EmployeeDTO result = employeeService.createEmployee(1L, employeeDTO);

    assertNotNull(result);
    assertEquals(employeeDTO.getEmail(), result.getEmail());
    assertEquals(employeeDTO.getFirstName(), result.getFirstName());

    verify(employeeRepository, times(1)).save(employee);
}
@Test
@DisplayName("Given employee in correct department when fetching by ID, then return EmployeeDTO")
void givenEmployeeInCorrectDepartment_whenGetEmployeeById_thenReturnEmployeeDTO() {
    // Arrange
    department.setId(1L);
    employee.setDepartment(department); // Ensure employee belongs to department
    given(departmentRepository.findById(1L)).willReturn(Optional.of(department));
    given(employeeRepository.findById("EMP001")).willReturn(Optional.of(employee));
    given(employeeMapper.employeeToEmployeeDto(employee)).willReturn(employeeDTO);

    // Act
    EmployeeDTO result = employeeService.getEmployeeById(1L, "EMP001");

    // Assert
    assertNotNull(result);
    verify(employeeRepository, times(1)).findById("EMP001");
}
@Test
@DisplayName("Given employee not in department when fetching by ID, then throw BusinessLogicException")
void givenEmployeeNotInDepartment_whenGetEmployeeById_thenThrowException() {
    // Arrange
    department.setId(1L);
    Department anotherDept = new Department();
    anotherDept.setId(2L); // Different department
    employee.setDepartment(anotherDept); // Employee belongs to another dept

    given(departmentRepository.findById(1L)).willReturn(Optional.of(department));
    given(employeeRepository.findById("EMP001")).willReturn(Optional.of(employee));

    // Act & Assert
    Exception exception = assertThrows(BusinessLogicException.class, 
() -> employeeService.getEmployeeById(1L, "EMP001"));

assertEquals("Employee does not belong to Department", exception.getMessage());

}
@Test
@DisplayName("Given employee with null department when fetching by ID, then throw BusinessLogicException")
void givenEmployeeWithNullDepartment_whenGetEmployeeById_thenThrowException() {
    // Arrange
    employee.setDepartment(null); // Employee has no department

    given(departmentRepository.findById(1L)).willReturn(Optional.of(department));
    given(employeeRepository.findById("EMP001")).willReturn(Optional.of(employee));

    // Act & Assert
    Exception exception = assertThrows(BusinessLogicException.class, 
    () -> employeeService.getEmployeeById(1L, "EMP001"));

assertEquals("Employee does not belong to Department", exception.getMessage());

}

@Test
@DisplayName("Given null department but employee has a department, then return false")
void givenNullDepartmentButEmployeeHasDepartment_whenEmployeeBelongsToDepartment_thenReturnFalse() {
    // Arrange
    Employee employee = new Employee();
    employee.setDepartment(new Department()); // Employee has a department

    // Act & Assert
    assertFalse(employeeService.employeeBelongsToDepartment(null, employee));
}
      
}
