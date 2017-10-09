package crud.unlocked.musicaudio.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import crud.unlocked.learnvolley.R;
import crud.unlocked.musicaudio.model.Playlist;

/**
 * Created by Irelia on 12/25/2016.
 */

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private Context mContext;
    private List<Playlist> mPlayLists;
    private RecyclerItemClickListener mListener;

    public PlaylistAdapter(Context context, List<Playlist> playlists, RecyclerItemClickListener listener) {
        this.mContext = context;
        this.mPlayLists = playlists;
        this.mListener = listener;
    }

    @Override
    public PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_playlist, parent, false);
        return new PlaylistAdapter.PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaylistViewHolder holder, int position) {
        Playlist playlist = mPlayLists.get(position);
        holder.title.setText(playlist.getTitle());
        holder.artist.setText(playlist.getArtist());
        String track = "track";
        if (playlist.getTrackCount() > 1){
            track = "tracks";
        }
        holder.trackCount.setText(String.valueOf(playlist.getTrackCount()) + " " + track);
        holder.likesCount.setText(String.valueOf(playlist.getLikesCount()));
        Picasso.with(mContext)
                .load(playlist.getArtworkUrl())
                .placeholder(R.drawable.music_placeholder)
                .into(holder.artwork);
        holder.bind(playlist, mListener);
    }

    @Override
    public int getItemCount() {
        return mPlayLists.size();
    }

    public class PlaylistViewHolder extends RecyclerView.ViewHolder {

        private TextView title, artist, trackCount, likesCount;
        private ImageView artwork, imgLikesCount, imgMoreDot;

        public PlaylistViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.item_playlist_title);
            artist = (TextView) itemView.findViewById(R.id.item_playlist_artist);
            trackCount = (TextView) itemView.findViewById(R.id.item_playlist_track_count);
            likesCount = (TextView) itemView.findViewById(R.id.item_playlist_like_count);
            artwork = (ImageView) itemView.findViewById(R.id.item_playlist_album);
            imgLikesCount = (ImageView) itemView.findViewById(R.id.item_playlist_image_like);
            imgMoreDot = (ImageView) itemView.findViewById(R.id.item_playlist_image_more);
        }

        public void bind(final Playlist playlist, final PlaylistAdapter.RecyclerItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClickListener(playlist, getLayoutPosition());
                }
            });
        }
    }

    public interface RecyclerItemClickListener {
        void onClickListener(Playlist playlist, int position);
    }

}
