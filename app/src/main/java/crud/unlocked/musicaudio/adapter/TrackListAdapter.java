package crud.unlocked.musicaudio.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import crud.unlocked.learnvolley.R;
import crud.unlocked.musicaudio.model.Track;
import crud.unlocked.musicaudio.util.Utility;

/**
 * Created by Irelia on 12/16/2016.
 */

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.TrackViewHolder> {
    private Context mContext;
    private List<Track> mTrackList;
    private RecyclerItemClickListener mListener;
    private int mSelectedPosition;
    private AnimationDrawable mAnimationDrawable;

    private boolean isPause;
    private boolean isClickable = true;

    public TrackListAdapter(Context context, List<Track> tracks, RecyclerItemClickListener listener) {
        this.mContext = context;
        this.mTrackList = tracks;
        this.mListener = listener;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.mSelectedPosition = selectedPosition;
    }

    public void setPause(boolean pause) {
        isPause = pause;
    }

    public void setClickable(boolean clickable) {
        isClickable = clickable;
    }

    @Override
    public TrackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_track, parent, false);
        return new TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrackViewHolder holder, int position) {
        Track track = mTrackList.get(position);
        if (track != null) {
            //When track is playing or not
            if (mSelectedPosition == position) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorItemSelected));
                holder.duration.setVisibility(View.INVISIBLE);
                //Playing, Pause
                if (!isPause) {
                    //Playing
                    //Start image anim
                    holder.musicPlaying.setVisibility(View.VISIBLE);
                    holder.musicPlaying.setBackgroundResource(R.drawable.ic_item_play_on_anim);
                    mAnimationDrawable = (AnimationDrawable) holder.musicPlaying.getBackground();
                    mAnimationDrawable.start();
                    //holder.musicPlaying.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.rotate_indefinitely));
                } else {
                    //Pause
                    holder.musicPlaying.setVisibility(View.VISIBLE);
                    holder.musicPlaying.setBackgroundResource(R.drawable.ic_item_play_on_first);
                }
            } else {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorTransparent));
                holder.duration.setVisibility(View.VISIBLE);
                holder.musicPlaying.setVisibility(View.GONE);
                String duration = Utility.convertDuration(track.getDuration());
                holder.duration.setText(duration);
            }

            //Set value to item
            holder.title.setText(track.getTitle());
            holder.artist.setText(track.getArtist());
            holder.like.setText(String.valueOf(track.getFavoritingsCount()));
            holder.play.setText(String.valueOf(track.getPlaybackCount()));
            Picasso.with(mContext)
                    .load(track.getArtworkUrl())
                    .placeholder(R.drawable.music_placeholder)
                    .into(holder.artwork);

            //Item is clickable or not
            if (isClickable) {
                if (mSelectedPosition != position) {
                    holder.bind(track, mListener);
                }
            } else {
                holder.itemView.setClickable(false);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mTrackList.size();
    }

    public class TrackViewHolder extends RecyclerView.ViewHolder {

        private TextView title, artist, duration, play, like;
        private ImageView artwork, musicPlaying;

        public TrackViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.item_music_title);
            artist = (TextView) itemView.findViewById(R.id.item_music_artist);
            duration = (TextView) itemView.findViewById(R.id.item_music_duration);
            play = (TextView) itemView.findViewById(R.id.item_music_play_count);
            like = (TextView) itemView.findViewById(R.id.item_music_like_count);
            artwork = (ImageView) itemView.findViewById(R.id.item_music_album);
            musicPlaying = (ImageView) itemView.findViewById(R.id.item_music_play_rotate);
        }

        public void bind(final Track track, final RecyclerItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClickListener(track, getLayoutPosition());
                }
            });
        }
    }

    public interface RecyclerItemClickListener {
        void onClickListener(Track track, int position);
    }
}
