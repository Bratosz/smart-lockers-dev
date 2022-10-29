package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.Client;
import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.MeasurementList;
import pl.bratosz.smartlockers.repository.ClientRepository;
import pl.bratosz.smartlockers.repository.MeasurementListRepository;
import pl.bratosz.smartlockers.repository.UsersRepository;
import pl.bratosz.smartlockers.response.StandardResponse;

import java.util.*;

@Service
public class MeasurementListService {
    private MeasurementListRepository measurementListRepository;
    private ClientRepository clientRepository;
    private UsersRepository usersRepository;


    public MeasurementListService(MeasurementListRepository measurementListRepository, ClientRepository clientRepository, UsersRepository usersRepository) {
        this.measurementListRepository = measurementListRepository;
        this.clientRepository = clientRepository;
        this.usersRepository = usersRepository;
    }

    public void add(Employee employee, Client client) {
        MeasurementList list = client.getMeasurementList();
        if (list == null) {
            list = new MeasurementList();
            list.setClient(client);
            list = measurementListRepository.save(list);
        }
        list.addEmployee(employee);
        measurementListRepository.save(list);
    }

    public List<Employee> getEmployeesToMeasure(long clientId) {
        List<Employee> employees = new ArrayList<>(
                measurementListRepository.getByClientId(clientId).getEmployeesToMeasure());
        Collections.sort(employees);
        return employees;
    }

    public Set<Employee> getEmployeesToAssign(long clientId) {
        return measurementListRepository.getByClientId(clientId).getEmployeesToAssign();
    }

    public Set<Employee> getEmployeesToRelease(long clientId) {
        return measurementListRepository.getByClientId(clientId).getEmployeesToRelease();
    }

    public StandardResponse remove(Employee employee) {
        MeasurementList measurementList = employee.getDepartment().getClient().getMeasurementList();
        if(measurementList.getEmployeesToAssign().contains(employee)) {
            measurementList.getEmployeesToAssign().remove(employee);
            measurementListRepository.save(measurementList);
            return StandardResponse.createForSucceed("Usunięto pracownika z listy pomierzonych.", employee);
        } else if(measurementList.getEmployeesToMeasure().contains(employee)) {
            measurementList.getEmployeesToMeasure().remove(employee);
            measurementListRepository.save(measurementList);
            return StandardResponse.createForSucceed("Usunięto pracownika z listy do pomiaru.", employee);
        } else {
            return StandardResponse.createForFailure("Nie znaleziono pracownika na żadnej z list.", employee);
        }
    }

    public MeasurementList create() {
        MeasurementList measurementList = new MeasurementList();
        return measurementListRepository.save(measurementList);
    }

    public void updateMeasurementListWithMeasuredEmployee(Employee employee, long clientId) {
        Client client = clientRepository.getById(clientId);
            MeasurementList measurementList = client.getMeasurementList();
            Set<Employee> employeesToMeasure = measurementList.getEmployeesToMeasure();
            Set<Employee> employeesToRelease = measurementList.getEmployeesToAssign();
            employeesToMeasure.remove(employee);
            employeesToRelease.add(employee);
            measurementList.setEmployeesToMeasure(employeesToMeasure);
            measurementList.setEmployeesToAssign(employeesToRelease);
            client.setMeasurementList(measurementList);
            clientRepository.save(client);
        }

    public Set<Employee> setMeasuredEmployeesAsAssigned(long userId) {
        long clientId = usersRepository.getActualClientId(userId);
        Client client = clientRepository.getById(clientId);
        MeasurementList measurementList = client.getMeasurementList();
        Set<Employee> employeesToAssign = measurementList.getEmployeesToAssign();
        Set<Employee> employeesToRelease = measurementList.getEmployeesToRelease();
        Set<Employee> updatedEmployees = new HashSet<>();
        for(Employee e : employeesToAssign) {
            employeesToRelease.add(e);
            updatedEmployees.add(e);
        }
        employeesToAssign.clear();
        measurementListRepository.save(measurementList);
        return updatedEmployees;
    }


}