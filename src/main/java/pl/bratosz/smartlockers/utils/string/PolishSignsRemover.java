package pl.bratosz.smartlockers.utils.string;

import java.text.Normalizer;

public class PolishSignsRemover {

    public static String[] removePolishSigns(String[] split) {
        for(int i = 0; i < split.length; i++){
            split[i] = removePolishSigns(split[i]);
        }
        return split;
    }

    public static String removePolishSigns(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll(
                "\\p{IsM}+", "");
        if(s.contains("Ł")) {
            s= s.replace('Ł', 'L');
        } else if(s.contains("ł")) {
            s = s.replace('ł', 'l');
        }
        return s;
    }
}
