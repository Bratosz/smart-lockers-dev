package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.EmployeeDummy;
import pl.bratosz.smartlockers.repository.EmployeesDummyRepository;

@Service
public class EmployeeDummyService {

    private EmployeesDummyRepository employeesDummyRepository;

    public EmployeeDummyService(EmployeesDummyRepository employeesDummyRepository) {
        this.employeesDummyRepository = employeesDummyRepository;
    }


    public EmployeeDummy createDummy() {
        EmployeeDummy e = EmployeeDummy.create();
        return employeesDummyRepository.save(e);
    }
}
