package softuni.library.util;

import java.io.*;

public class FileUtilImpl implements FileUtil {

    @Override
    public String readFile(String path) throws IOException {
        File file = new File(path);

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

        StringBuilder sb = new StringBuilder();

        String line;

        while ((line = reader.readLine()) != null) {
            sb.append(line).append(System.lineSeparator());
        }

        return sb.toString().trim();
    }
}
