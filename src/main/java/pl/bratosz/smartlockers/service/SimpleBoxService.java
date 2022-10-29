package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.Box;
import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.SimpleBox;
import pl.bratosz.smartlockers.repository.SimpleBoxesRepository;

@Service
public class SimpleBoxService {
    private SimpleBoxesRepository simpleBoxesRepository;

    public SimpleBoxService(SimpleBoxesRepository simpleBoxesRepository) {
        this.simpleBoxesRepository = simpleBoxesRepository;
    }

    public SimpleBox createSimpleBox(Box box, Employee employee) {
        int plantNumber = box.getLocker().getPlant().getPlantNumber();
        int lockeNumber = box.getLocker().getLockerNumber();
        int boxNumber = box.getBoxNumber();
        SimpleBox simpleBox =  new SimpleBox(
                plantNumber, lockeNumber, boxNumber, employee);
        return simpleBoxesRepository.save(simpleBox);
    }
}
