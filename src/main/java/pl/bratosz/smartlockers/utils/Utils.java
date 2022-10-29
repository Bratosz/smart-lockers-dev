package pl.bratosz.smartlockers.utils;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import pl.bratosz.smartlockers.model.MyEntity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Utils {

    public static <T> List<T> getDuplicates(Collection<T> collection) {
        return collection.stream().collect(Collectors.groupingBy(
                Function.identity()))
                .entrySet()
                .stream()
                .filter(e -> e.getValue().size() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public static <T> List<T> getOnlyUniqueValues(Collection<T> collection) {
        return collection.stream().collect(Collectors.groupingBy(
                Function.identity()))
                .entrySet()
                .stream()
                .filter(e -> e.getValue().size() == 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public static List<Integer> findMissingNumbersFromRange(
            List<Integer> sortedList,
            int firstNumber,
            int lastNumber) {
        sortedList.add(lastNumber +1);
        int[] register = new int[lastNumber + 2];
        for(int i : sortedList) {
            register[i] = 1;
        }
        List<Integer> missingNumbers = new LinkedList<>();
        for(int i = firstNumber; i < register.length; i++) {
            if(register[i] == 0) {
                missingNumbers.add(i);
            }
        }
        return missingNumbers;
    }

    public static int getHighestNumber(List<Integer> numbers) {
        Integer[] integers = numbers.toArray(new Integer[numbers.size()]);
        int max = integers[0];
        for(int counter = 1; counter < integers.length; counter++) {
            if(integers[counter] > max) {
                max = integers[counter];
            }
        }
        return max;
    }

    public static LocalDate convert(Date date)  {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static String substringBetween(String s, String before, String behind) {
        int from =  s.indexOf(before) + before.length();
        int to = s.lastIndexOf(behind);
        return s.substring(from, to);
    }

    public static Resource getTempFileBy(String fileName) throws FileNotFoundException {
        String fileTempPath = System.getProperty("java.io.tmpdir");
        Path filePathAsPath = Paths.get(fileTempPath);
        Path filePath = filePathAsPath.resolve(fileName).normalize();
        try {
            Resource resource = new UrlResource(filePath.toUri());
            resource.getInputStream();
            return resource;
        } catch (MalformedURLException e) {
            throw new FileNotFoundException();
        } catch (IOException e) {
            throw new FileNotFoundException();
        }
    }

    public static <T> Map<String, T> toMapWithToStringKey(Collection<T> c) {
        return c.stream().collect(Collectors.toMap(
                T::toString,
                Function.identity()));
    }

    public static <T extends MyEntity> Map<Long, T> mapToMapWithIdKey(Collection<T> c) {
        return c.stream().collect(Collectors.toMap(
             T::getId,
             Function.identity()));
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

//    public static <T extends SameClient> boolean haveSameClient(
//            Collection<? extends T> col, T... args) {
//        List<T> a1 = new LinkedList<>(col);
//        List<T> a2 = Arrays.asList(args);
//        a1.addAll(a2);
//        long clientId = 0;
//        for(T e : a1) {
//            if(clientId == 0) clientId = e.getClient().getId();
//            else {
//                if(clientId != e.getClient().getId()) return false;
//            }
//        }
//        return true;
//    }

    public static boolean haveSameClient(
            Collection<? extends SameClient> col, SameClient... args) {
        List<SameClient> a1 = new LinkedList<>(col);
        List<SameClient> a2 = Arrays.asList(args);
        a1.addAll(a2);
        return haveSameClient(a1);
    }

    public static boolean haveSameClient(SameClient... args) {
        List<SameClient> l = Arrays.asList(args);
        return haveSameClient(l);
    }

    public static boolean netIsAvailable() {
        try {
            final URL url = new URL("http://www.google.com");
            final URLConnection conn = url.openConnection();
            conn.connect();
            conn.getInputStream().close();
            return true;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return false;
        }
    }

    private static boolean haveSameClient(List<SameClient> l) {
        long clientId = 0;
        for(SameClient e : l) {
            if(clientId == 0) clientId = e.getClient().getId();
            else {
                if(clientId != e.getClient().getId()) return false;
            }
        }
        return true;
    }
}
