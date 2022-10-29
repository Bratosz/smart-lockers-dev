package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.DepartmentAlias;
import pl.bratosz.smartlockers.repository.DepartmentAliasesRepository;

@Service
public class DepartmentAliasService {
    DepartmentAliasesRepository departmentAliasesRepository;

    public DepartmentAliasService(DepartmentAliasesRepository departmentAliasesRepository) {
        this.departmentAliasesRepository = departmentAliasesRepository;
    }


    public DepartmentAlias create(String alias) {
        DepartmentAlias departmentAlias = new DepartmentAlias(alias);
        return departmentAliasesRepository.save(departmentAlias);
    }
}
