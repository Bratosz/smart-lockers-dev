package pl.bratosz.smartlockers.service.jgenderize.client;


import pl.bratosz.smartlockers.service.jgenderize.model.NameGender;

import java.util.List;
import java.util.Locale;

public interface Genderize {

    NameGender getGender(String name);

    List<NameGender> getGenders(String... names);

    NameGender getGender(String name, Locale locale);

    List<NameGender> getGenders(String[] names, Locale locale);

}
