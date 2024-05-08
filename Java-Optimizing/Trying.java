package optjava;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Trying {

    //tag::TRYING_1[]
    public void readFirstLineOld(File file) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String firstLine = reader.readLine();
            System.out.println(firstLine);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
    //end::TRYING_1[]

    //tag::TRYING_2[]
    public void readFirstLineNew(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String firstLine = reader.readLine();
            System.out.println(firstLine);
        }
    }
    //end::TRYING_2[]

}
