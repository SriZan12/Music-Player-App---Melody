package com.example.melody;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.melody.databinding.ActivityMainBinding;

import java.util.ArrayList;

import Adapter.ClickListener;
import Adapter.MusicListAdapter;
import MusicComponent.SongsDetails;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding activityMainBinding;
    private final int STORAGE_PERMISSION_CODE = 1;
    ArrayList<SongsDetails> musicList = new ArrayList<>();
    MusicListAdapter musicListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        checkPermission();

        activityMainBinding.searchMusic.setQueryHint("Search Music...");
        activityMainBinding.searchMusic.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                musicListAdapter.getFilter().filter(newText);
                return false;
            }
        });

    }

    public void checkPermission() {

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            updateRecyclerView();
        } else {
            requestPermission();
        }

    }


    private void updateRecyclerView() {
        musicList = new ArrayList<SongsDetails>();
        getMusicList();
        activityMainBinding.musicRecycler.setHasFixedSize(true);
        activityMainBinding.musicRecycler.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(activityMainBinding.musicRecycler.getContext(), DividerItemDecoration.VERTICAL);
        activityMainBinding.musicRecycler.addItemDecoration(dividerItemDecoration);
        musicListAdapter = new MusicListAdapter(musicList, this,clickListener);
        activityMainBinding.musicRecycler.setAdapter(musicListAdapter);

    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(MainActivity.this)
                    .setMessage("Get Music From your Device to Play in Here !")
                    .setTitle("Melody")
                    .setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                updateRecyclerView();
            } else {
                Toast.makeText(getApplicationContext(), "Permission denied ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getMusicList() {

        Uri Collection;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            Collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }

        Collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.ARTIST
        };
        String sortOrder = MediaStore.Audio.Media.DISPLAY_NAME + " ASC";

        try
                (Cursor songCursor = getApplicationContext().getContentResolver().query(
                        Collection,
                        projection,
                        null,
                        null,
                        sortOrder
                )) {
            int idColumn =
                    songCursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int nameColumn =
                    songCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
            int durationColumn =
                    songCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
            int sizeColumn =
                    songCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);

            int artistColumn =
                    songCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);

            while (songCursor.moveToNext()) {
                long id =
                        songCursor.getLong(idColumn);
                String name =
                        songCursor.getString(nameColumn);
                int duration =
                        songCursor.getInt(durationColumn);
                int size =
                        songCursor.getInt(sizeColumn);
                String artist =
                        songCursor.getString(artistColumn);

                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

                musicList.add(new SongsDetails(contentUri, name, artist, duration, size));

            }
        }
    }

    private final ClickListener clickListener = new ClickListener() {
        @Override
        public void onClick(int position) {
            Intent intent = new Intent(MainActivity.this,PlayMusicActivity.class);
            intent.putParcelableArrayListExtra("SongsList", musicList);
            intent.putExtra("pos",position);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    };


}