package pl.bratosz.smartlockers.model.clothes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions.*;
import pl.bratosz.smartlockers.exception.WrongLengthModificationException;


import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
import static pl.bratosz.smartlockers.model.clothes.LengthModification.*;
import static pl.bratosz.smartlockers.model.clothes.StringsForClothExtractor.*;

class StringsForClothExtractorTest {

    @Test
    void returnSizeForCustomStringSize() {
        //given
        String s1 = "NTP";
        String s2 = "N110";
        String s3 = "";
        String s4 = "0";
        String s5 = "NS";
        //when
        ClothSize size1 = getSizeFromString(s1);
        ClothSize size2 = getSizeFromString(s2);
        ClothSize size3 = getSizeFromString(s3);
        ClothSize size4 = getSizeFromString(s4);
        ClothSize size5 = getSizeFromString(s5);
        //then
        assertThat(size1, is(equalTo(ClothSize.SIZE_NTP)));
        assertThat(size2, is(equalTo(ClothSize.SIZE_0)));
        assertThat(size3, is(equalTo(ClothSize.SIZE_0)));
        assertThat(size4, is(equalTo(ClothSize.SIZE_0)));
        assertThat(size5, is(equalTo(ClothSize.SIZE_NTP)));
    }

    @Test
    void returnSizeForStringSizeWithLengthModification() {
        //given
        String s1 = "xlN90";
        String s2 = "40N110";
        String s3 = "56R105";
        String s4 = "XXL";
        String s5 = "2XL";
        //when
        ClothSize size1 = getSizeFromString(s1);
        ClothSize size2 = getSizeFromString(s2);
        ClothSize size3 = getSizeFromString(s3);
        ClothSize size4 = getSizeFromString(s4);
        ClothSize size5 = getSizeFromString(s5);
        //then
        assertThat(size1, is(equalTo(ClothSize.SIZE_XL)));
        assertThat(size2, is(equalTo(ClothSize.SIZE_40)));
        assertThat(size3, is(equalTo(ClothSize.SIZE_56)));
        assertThat(size4, is(equalTo(ClothSize.SIZE_2XL)));
        assertThat(size5, is(equalTo(ClothSize.SIZE_2XL)));
    }

    @Test
    void returnLengthModificationForStringWithSizeAndLengthModification() {
        //given
        String s1 = "xlN90";
        String s2 = "40 N110";
        String s3 = "56R105";
        String s4 = "2XL";
        String s5 = "NS";
        String s6 = "NTP";
        //when
        LengthModification lengthMod1 = getModificationFromString(s1);
        LengthModification lengthMod2 = getModificationFromString(s2);
        LengthModification lengthMod3 = getModificationFromString(s3);
        LengthModification lengthMod4 = getModificationFromString(s4);
        LengthModification lengthMod5 = getModificationFromString(s5);
        LengthModification lengthMod6 = getModificationFromString(s6);
        //then
        assertThat(lengthMod1, is(equalTo(N90)));
        assertThat(lengthMod2, is(equalTo(N110)));
        assertThat(lengthMod3, is(equalTo(R105)));
        assertThat(lengthMod4, is(equalTo(NONE)));
        assertThat(lengthMod5, is(equalTo(NONE)));
        assertThat(lengthMod6, is(equalTo(NONE)));
    }

    @Test
    void returnNTPWhenRangeOfModificationIsExceeded() {
        //given
        String s1 = "XXLN135";
        String s2 = "50N65";
        String s3 = "50R65";
        String s4 = "50R134";
        //when
        LengthModification lengthMod1 = getModificationFromString(s1);
        LengthModification lengthMod2 = getModificationFromString(s2);
        LengthModification lengthMod3 = getModificationFromString(s3);
        LengthModification lengthMod4 = getModificationFromString(s4);
        //then
        assertThat(lengthMod1, is(equalTo(NTP)));
        assertThat(lengthMod2, is(equalTo(NTP)));
        assertThat(lengthMod3, is(equalTo(NTP)));
        assertThat(lengthMod4, is(equalTo(NTP)));

    }

}