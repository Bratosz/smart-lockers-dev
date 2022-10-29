package pl.bratosz.smartlockers.resolvers;


import pl.bratosz.smartlockers.model.clothes.ClothType;

import java.util.HashMap;
import java.util.Map;

public class ClothTypeResolver {
    private Map<String, ClothType> clothTypes;

    public ClothTypeResolver() {
        clothTypes = new HashMap<>();
        clothTypes.put("SPODNIE", ClothType.TROUSERS);
        clothTypes.put("OGRODNICZKI", ClothType.TROUSERS);
        clothTypes.put("BLUZA", ClothType.SWEATSHIRT);
        clothTypes.put("FARTUSZEK", ClothType.APRON);
        clothTypes.put("FARTUCH", ClothType.APRON);
        clothTypes.put("T-SHIRT", ClothType.SHIRT);
        clothTypes.put("TSHIRT", ClothType.SHIRT);
        clothTypes.put("POLO", ClothType.SHIRT);
        clothTypes.put("KURTKA", ClothType.JACKET);
    }

    public ClothType resolve(String clothName) {
        for(Map.Entry<String, ClothType> clothType : clothTypes.entrySet()){
            if(clothName.contains(clothType.getKey())){
                return clothType.getValue();
            }
        }
        return ClothType.NOT_DEFINED;
    }
}
