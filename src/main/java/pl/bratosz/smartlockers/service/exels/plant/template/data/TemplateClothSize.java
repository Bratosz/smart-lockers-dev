package pl.bratosz.smartlockers.service.exels.plant.template.data;

import pl.bratosz.smartlockers.model.clothes.ClothSize;
import pl.bratosz.smartlockers.utils.string.MyString;

import java.util.Objects;

public class TemplateClothSize {
    private ClothSize clothSize;

    public TemplateClothSize(String size) {
        size = MyString.create(size).get();
        clothSize = ClothSize.getSizeByName(size);
    }

    public ClothSize getClothSize() {
        return clothSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TemplateClothSize that = (TemplateClothSize) o;
        return getClothSize() == that.getClothSize();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClothSize());
    }
}
