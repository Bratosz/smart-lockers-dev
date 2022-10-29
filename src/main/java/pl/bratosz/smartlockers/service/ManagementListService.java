package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.users.ManagementList;
import pl.bratosz.smartlockers.repository.ManagementListRepository;

import java.util.HashSet;
import java.util.LinkedList;

@Service
public class ManagementListService {

    private ManagementListRepository managementListRepository;

    public ManagementListService(ManagementListRepository managementListRepository) {
        this.managementListRepository = managementListRepository;
    }

    public ManagementList create() {
        ManagementList managementList = new ManagementList();
        managementList.setEmployees(new HashSet<>());
        managementList.setMissedEmployees(new LinkedList<>());
        return managementListRepository.save(managementList);
    }
}
