package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BookHolder> {
    public interface BookListener {
        void onBookOpen(BookFile bookFile);
    }

    private BookListener listener;
    private List<BookFile> books;

    public BooksAdapter(List<BookFile> books, BookListener listener) {
        this.books = new ArrayList<>(books);
        this.listener = listener;
    }

    @NonNull @Override public BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.file_list_item, parent, false);
        return new BookHolder(view);
    }

    @Override public void onBindViewHolder(@NonNull BookHolder holder, int position) {
        BookFile bookFile = books.get(position);
        holder.bind(bookFile);
    }

    @Override public int getItemCount() {
        return books.size();
    }

    class BookHolder extends RecyclerView.ViewHolder {
        private LinearLayout itemLayout;
        private TextView tvPath, tvName;

        public BookHolder(@NonNull View itemView) {
            super(itemView);
            itemLayout = itemView.findViewById(R.id.item_layout);
            tvName = itemView.findViewById(R.id.tv_filename);
            tvPath = itemView.findViewById(R.id.tv_path);
        }

        public void bind(BookFile bookFile) {
            tvPath.setText(bookFile.getPath());
            tvName.setText(bookFile.getFilename());
            itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    listener.onBookOpen(bookFile);
                }
            });
        }
    }
}