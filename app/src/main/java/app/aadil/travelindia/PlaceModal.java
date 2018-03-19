package app.aadil.travelindia;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class PlaceModal implements com.squareup.picasso.Target {
    private String mName;
    private String place_id;
    private Bitmap mImage;
    private Context context;
    private RecyclerView.Adapter adapter;

    public PlaceModal(Context context, RecyclerView.Adapter adapter, String mName, Bitmap mImage, String place_id){
        this.mName = mName;
        this.mImage = mImage;
        this.context = context;
        this.adapter = adapter;
        this.place_id = place_id;
    }

    public String getName(){
        return mName;
    }

    public Bitmap getImage(){
        return mImage;
    }

    public String getPlace_id(){
        return place_id;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        mImage = bitmap;
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBitmapFailed(Exception e,Drawable errorDrawable) {
        mImage = ((BitmapDrawable)errorDrawable).getBitmap();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
        mImage = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        adapter.notifyDataSetChanged();
    }
}
