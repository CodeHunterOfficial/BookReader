package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION =101 ;
    private RecyclerView rvFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvFiles = findViewById(R.id.rv_files);
        rvFiles.setLayoutManager(new LinearLayoutManager(this));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        } else {
            generateList();
        }
    }

    @TargetApi(Build.VERSION_CODES.M) private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            generateList();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, REQUEST_PERMISSION);
        }
    }

    private void generateList() {
       /* List<StorageBean> storageBeans = StorageUtils.getStorageData(this); // получение списка смонтированных карт памяти на устройстве
        List<String> paths = new ArrayList<>();
        if (storageBeans != null) {
            for (StorageBean storageBean : storageBeans) {
                paths.add(storageBean.getPath());
            }
        } else
            */
        List<String> paths = new ArrayList<>();
        {


            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            paths.add(path);
        }
        ListFilesTask listFilesTask = new ListFilesTask(paths);
        listFilesTask.setListener(new ListFilesTask.ListFilesListener() {
            @Override public void onTaskCompleted(List<File> files) {
                listFilesTask.setListener(new ListFilesTask.ListFilesListener() {
                    @Override public void onTaskCompleted(List<File> files) {
                        if (!isFinishing()) {
                            List<BookFile> bookFiles = new ArrayList<>();
                            for (File f : files) {
                                BookFile bookFile = new BookFile(f.getName(), f.getAbsolutePath());
                                if (!bookFiles.contains(bookFile)) bookFiles.add(bookFile);
                            }
                            Collections.sort(bookFiles, new Comparator<BookFile>() {
                                @Override public int compare(BookFile bookFile, BookFile t1) {
                                    return bookFile.getFilename().compareToIgnoreCase(t1.getFilename());
                                }
                            });
                            rvFiles.setAdapter(new BooksAdapter(bookFiles, new BooksAdapter.BookListener() {
                                @Override public void onBookOpen(BookFile bookFile) {

                                }
                            }));
                        }
                    }
                });
            }
        });
        listFilesTask.execute();
    }

    static class ListFilesTask extends AsyncTask<Void, Void, List<File>> {
        public interface ListFilesListener {
            void onTaskCompleted(List<File> files);
        }

        private ListFilesListener listener;
        private List<String> startPaths;
        private List<File> files;
        private boolean completed;

        public ListFilesTask(List<String> startPaths) {
            this.startPaths = new ArrayList<>(startPaths);
            this.files = new ArrayList<>();
            this.completed = false;
        }

        public void setListener(ListFilesListener listener) {
            this.listener = listener;
            if (completed && listener != null && files != null) {
                listener.onTaskCompleted(files);
            }
        }

        @Override protected List<File> doInBackground(Void... voids) {
            List<File> fileList = new ArrayList<>();
            for (String s : startPaths) {
                searchFiles(fileList, new File(s));
            }
            return fileList;
        }

        @Override protected void onPostExecute(List<File> files) {
            completed = true;
            if (listener != null) {
                listener.onTaskCompleted(files);
            } else {
                this.files = new ArrayList<>(files);
            }
        }

        private void searchFiles(List<File> list, File dir) {
            String epubPattern = ".epub";
            String fb2Pattern = ".fb2";

            File[] listFiles = dir.listFiles();
            if (listFiles != null) {
                for (File listFile : listFiles) {
                    if (listFile.isDirectory()) {
                        searchFiles(list, listFile);
                    } else {
                        if (listFile.getName().endsWith(epubPattern) || listFile.getName()
                                .endsWith(fb2Pattern)) {
                            list.add(listFile);
                        }
                    }
                }
            }
        }
    }
}