package Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.melody.databinding.SongsListBinding;

import java.util.ArrayList;
import java.util.List;

import MusicComponent.SongsDetails;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.MusicListViewHolder> implements Filterable {

    ArrayList<SongsDetails> musicList = new ArrayList<SongsDetails>();
    Context context;
    ClickListener clickListener;
    ArrayList<SongsDetails> allSongs;


    public MusicListAdapter(ArrayList<SongsDetails> musicList, Context context,ClickListener clickListener) {

        this.musicList = musicList;
        this.context = context;
        this.clickListener = clickListener;
        this.allSongs = new ArrayList<>(musicList);

    }

    @NonNull
    @Override
    public MusicListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        SongsListBinding binding = SongsListBinding.inflate(inflater, parent, false);
        return new MusicListViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MusicListViewHolder holder, @SuppressLint("RecyclerView") int position) {

        SongsDetails songsDetails = musicList.get(position);
        holder.songsListBinding.musicName.setText(songsDetails.getSongName());
        holder.songsListBinding.musicName.setSelected(true);


       holder.songsListBinding.linear.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
             clickListener.onClick(position);
           }
       });

    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {

        //        run on a background thread
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<SongsDetails> filteredList = new ArrayList<>();

            if (charSequence.toString().isEmpty()) {
                filteredList.addAll(allSongs);
            } else {
                String filteredPattern = charSequence.toString().toLowerCase().trim();
                for (SongsDetails songsDetails : allSongs) {
                    if (songsDetails.getSongName().toLowerCase().contains(filteredPattern)) {
                        filteredList.add(songsDetails);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            musicList.clear();
            musicList.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public static class MusicListViewHolder extends RecyclerView.ViewHolder {

        SongsListBinding songsListBinding;

        public MusicListViewHolder(@NonNull SongsListBinding songsListBinding) {
            super(songsListBinding.getRoot());
            this.songsListBinding = songsListBinding;
        }
    }
}
