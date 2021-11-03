package com.example.myapplication;

public class BookFile {
    private String filename, path;

    public BookFile(String filename, String path) {
        this.filename = filename;
        this.path = path;
    }

    public String getFilename() {
        return filename;
    }

    public String getPath() {
        return path;
    }

    @Override public int hashCode() {
        return filename.hashCode() + path.hashCode();
    }

    @Override public boolean equals(Object obj) {
        if (obj instanceof BookFile) {
            BookFile bookFile = (BookFile) obj;
            return this.filename.equals(bookFile.getFilename()) && this.path.equals(bookFile.getPath());
        }
        return false;
    }
}