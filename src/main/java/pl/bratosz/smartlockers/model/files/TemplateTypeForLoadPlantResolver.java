package pl.bratosz.smartlockers.model.files;

import org.springframework.web.multipart.MultipartFile;
import pl.bratosz.smartlockers.exception.MyException;

import java.util.Arrays;
import java.util.Optional;

public class TemplateTypeForLoadPlantResolver {
    public static TemplateTypeForPlantLoad resolve(MultipartFile loadPlantFile) throws MyException {
        String fileName = loadPlantFile.getOriginalFilename();
        Optional<TemplateTypeForPlantLoad> templateType = Arrays.asList(TemplateTypeForPlantLoad.values()).stream()
                .filter(e -> fileName.contains(e.getName()))
                .findFirst();
        if(templateType.isPresent()) {
            return templateType.get();
        } else {
            throw new MyException("Nieprawidłowa nazwa pliku. Nazwa pobranego szablonu nie może być zmieniana! Można co najwyżej dodać coś na jej początku lub na końcu.");
        }
    }
}
