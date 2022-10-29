package pl.bratosz.smartlockers.exception;

import pl.bratosz.smartlockers.model.clothes.Cloth;

import java.util.List;

public class ClothException extends Exception {
    private List<Cloth> clothes;

    public ClothException(String message) {
        super(message);
    }

    public static ClothException createForMistakeInAssignedClothes(
            String message, List<Cloth> clothList) {
        ClothException clothException = new ClothException(message);
        clothException.setClothes(clothList);
        return clothException;
    }

    public List<Cloth> getClothes() {
        return clothes;
    }

    public void setClothes(List<Cloth> clothes) {
        this.clothes = clothes;
    }
}
