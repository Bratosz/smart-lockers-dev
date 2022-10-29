package pl.bratosz.smartlockers.service.managers;

import pl.bratosz.smartlockers.model.Client;
import pl.bratosz.smartlockers.model.Department;

public class DepartmentManager {

    public static Department createDefaultDepartment(Client client) {
        Department d = new Department();
        d.setClient(client);
        d.setName("GŁÓWNY");
        d.setSurrogate(true);
        return d;
    }
}
