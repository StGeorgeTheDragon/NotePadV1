package msm.programming.notepadv1;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class FileOptions {
    //todo: add a way to list ALL text files found on a phone
    private Context context;

    public FileOptions(Context context) {
        this.context = context;
    }

    public void saveNote(String fileName, String content) throws IOException {
        File file = new File(context.getExternalFilesDir(null), fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(content.getBytes());
        }
    }

    public String loadNote(String fileName) throws IOException {
        File file = new File(context.getExternalFilesDir(null), fileName+".txt");
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        }
        return stringBuilder.toString();
    }
}
