package msm.programming.notepadv1;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import msm.programming.notepadv1.R.id;

public class MainActivity extends AppCompatActivity {

    //TODO: add an option to delete notes permanently
    //TODO: try and migrate as much of this as possible to separate classes
    //TODO: try and streamline the looks and operation

    private static final int MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private EditText editText;
    private Button saveButton;
    private Button loadButton;
    private Button clearButton;
    private FileOptions fileHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAppThemeBasedOnNightMode();
        setContentView(R.layout.activity_main);

        editText =  findViewById(id.editText);
        saveButton = findViewById(R.id.saveButton);
        loadButton = findViewById(R.id.loadButton);
        clearButton = findViewById(R.id.clearButton);
        fileHandler = new FileOptions(this);

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearEditText();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSaveDialog();
            }
        });

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoadDialog();
            }
        });
        // Check and request external storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, request it
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    private void setAppThemeBasedOnNightMode() {
        int nightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (nightMode) {
            case Configuration.UI_MODE_NIGHT_YES:
                // Dark theme
                setTheme(R.style.AppTheme_Dark);
                break;

            case Configuration.UI_MODE_NIGHT_NO:
            default:
                // Light theme
                setTheme(R.style.AppTheme);
                break;
        }
    }

    private void clearEditText() {
        editText.setText("");
    }

    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save Note As");

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String fileName = input.getText().toString();
                if (!fileName.isEmpty()) {
                    saveNoteAs(fileName);
                } else {
                    showToast("File name cannot be empty");
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

        private void saveNoteAs(String fileName) {
            String text = editText.getText().toString();
            try {
                fileHandler.saveNote(fileName + ".txt", text);
                showAlertDialog("Save Successful", "Note saved successfully!");
            } catch (IOException e) {
                e.printStackTrace();
                showAlertDialog("Save Failed", "Failed to save the note.");
            }
        }



    private void showLoadDialog() {
        final List<String> fileNames = getFileNames(); // Get the list of available file names

        if (fileNames.isEmpty()) {
            showToast("No documents available");
            return;
        }

        final CharSequence[] items = fileNames.toArray(new CharSequence[fileNames.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Document")
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedFileName = fileNames.get(which);
                        loadNoteAs(selectedFileName);
                    }
                });

        builder.show();
    }

    private List<String> getFileNames() {
        List<String> fileNames = new ArrayList<>();
        // Provide logic to fetch the list of available file names from your storage directory
        File directory = getExternalFilesDir(null);
        if (directory != null){
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".txt")) {
                        fileNames.add(file.getName().replace(".txt", ""));
                    }
                }
            }
        }
        return fileNames;
    }

    private void loadNoteAs(String fileName) {
        try {
            String text = fileHandler.loadNote(fileName);
            editText.setText(text);
            showAlertDialog("Load Successful", "Note loaded successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            showAlertDialog("Load Failed", "Failed to load the note.");
        }
    }
    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // You can handle the OK button click here
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE) {
            // Check if the permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can proceed with file operations
            } else {
                // Permission denied, handle accordingly (e.g., show a message or disable features)
            }
        }
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
