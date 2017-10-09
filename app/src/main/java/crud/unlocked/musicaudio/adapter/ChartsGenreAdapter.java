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
import crud.unlocked.musicaudio.model.Genre;

/**
 * Created by Irelia on 12/14/2016.
 */

public class ChartsGenreAdapter extends RecyclerView.Adapter<ChartsGenreAdapter.ChartGenreHolder> {
    private List<Genre> mGenres;
    private Context mContext;
    private MyRecyclerItemClickListener mListener;
    private boolean isClickable;

    public void setClickable(boolean clickable) {
        isClickable = clickable;
    }

    public ChartsGenreAdapter(Context context, List<Genre> mGenres, MyRecyclerItemClickListener listener) {
        this.mContext = context;
        this.mGenres = mGenres;
        this.mListener = listener;
    }

    @Override
    public ChartGenreHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_genre, parent, false);
        return new ChartGenreHolder(view);
    }

    @Override
    public void onBindViewHolder(ChartGenreHolder holder, int position) {
        Genre genre = mGenres.get(position);
        holder.title.setText(genre.getTitle());
        Picasso.with(mContext)
                .load(genre.getPhotoFirst())
                .placeholder(R.drawable.music_placeholder)
                .into(holder.photoFirst);
        Picasso.with(mContext)
                .load(genre.getPhotoSecond())
                .placeholder(R.drawable.music_placeholder)
                .into(holder.photoSecond);
        Picasso.with(mContext)
                .load(genre.getPhotoThird())
                .placeholder(R.drawable.music_placeholder)
                .into(holder.photoThird);
        if (isClickable) {
            holder.bind(genre, mListener);
        }
    }

    @Override
    public int getItemCount() {
        return mGenres.size();
    }

    public class ChartGenreHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private ImageView photoFirst;
        private ImageView photoSecond;
        private ImageView photoThird;

        public ChartGenreHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.txtGenreTitle);
            photoFirst = (ImageView) itemView.findViewById(R.id.imgFirstGenre);
            photoSecond = (ImageView) itemView.findViewById(R.id.imgSecondGenre);
            photoThird = (ImageView) itemView.findViewById(R.id.imgThirdGenre);
        }

        public void bind(final Genre genre, final MyRecyclerItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClickListener(genre, getLayoutPosition());
                }
            });
        }
    }

    public interface MyRecyclerItemClickListener {
        void onClickListener(Genre genre, int position);
    }
}
