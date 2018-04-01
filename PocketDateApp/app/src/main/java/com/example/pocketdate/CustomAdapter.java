package com.example.pocketdate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.stupidcupid.R;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Austin on 3/20/2018.
 */

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    //Creating an arraylist of POJO objects
    private ArrayList<CustomPojo> list_members=new ArrayList<>();
    private final LayoutInflater inflater;
    private final int SENDER = 1, RECEIVER = 2;

    // profile information that is stored for your current match that will be displayed in the message view
    private String profileLocation;
    private String matchFirstName;
    private String matchLastName;

    View view;
    MyViewHolder holder;
    private Context context;

    public CustomAdapter(Context context){
        this.context=context;
        inflater=LayoutInflater.from(context);
    }
    //This method inflates view present in the RecyclerView
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch(viewType){
            case SENDER:
                view = inflater.inflate(R.layout.item_message_sent, parent, false);
                holder = new MyViewHolder(view);
                break;
            case RECEIVER:
                view = inflater.inflate(R.layout.item_message_received, parent, false);
                holder = new MyViewHolder(view);
                break;
        }

        //view=inflater.inflate(R.layout.item_message_received, parent, false);
        //holder=new MyViewHolder(view);
        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        if(list_members.get(position).getType() == 1){
            return SENDER;
        }
        else if(list_members.get(position).getType() == 2){
            return RECEIVER;
        }
        else
            return -1;
    }

    //Binding the data using get() method of POJO object
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        CustomPojo list_items=list_members.get(position);

        switch(holder.getItemViewType()){
            case SENDER:
                holder.content.setText(list_items.getContent());
                holder.time.setText(list_items.getTime());
                break;
            case RECEIVER:
                holder.user_name.setText(this.matchFirstName);
                holder.content.setText(list_items.getContent());
                holder.time.setText(list_items.getTime());
                new DownloadImageTask(holder.profImg).execute(getProfileLocation());
                break;
        }
    }

    //Setting the arraylist
    public void setListContent(ArrayList<CustomPojo> list_members){
        this.list_members=list_members;
        notifyItemRangeChanged(0,list_members.size());

    }

    @Override
    public int getItemCount() {
        return list_members.size();
    }

    public String getProfileLocation() {
        return profileLocation;
    }

    public void setProfileLocation(String profileLocation) {
        this.profileLocation = profileLocation;
    }

    public String getMatchFirstName() {
        return matchFirstName;
    }

    public void setMatchFirstName(String matchFirstName) {
        this.matchFirstName = matchFirstName;
    }

    public String getMatchLastName() {
        return matchLastName;
    }

    public void setMatchLastName(String matchLastName) {
        this.matchLastName = matchLastName;
    }

    //View holder class, where all view components are defined
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView user_name,content,time;
        ImageView profImg;
        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            user_name=(TextView)itemView.findViewById(R.id.text_message_name);
            content=(TextView)itemView.findViewById(R.id.text_message_body);
            time=(TextView)itemView.findViewById(R.id.text_message_time);
            profImg = (ImageView)itemView.findViewById(R.id.image_message_profile);
        }
        @Override
        public void onClick(View v) {

        }
    }
    public void removeAt(int position) {
        list_members.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(0, list_members.size());
    }

    // asynchronous task that handles setting the user's profile image in the navigation bar
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}