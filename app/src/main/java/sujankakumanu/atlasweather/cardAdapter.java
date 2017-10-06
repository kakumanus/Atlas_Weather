
package sujankakumanu.atlasweather;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Sujan on 8/3/2016.
 */

public class cardAdapter extends RecyclerView.Adapter<cardAdapter.MyViewHolder>{

    private List<wxInfo>weather;
    Context context;

    public cardAdapter(Context context, List<wxInfo>weather){
        this.weather=weather;
        this.context=context;
    }


    @Override
    public int getItemCount() {
        return weather.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.card_layout, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        wxInfo wx = weather.get(position);
        holder.validTxt.setText(wx.valid);
        holder.tempTxt.setText(wx.temp);
        holder.wxTxt.setText(wx.weather);
        holder.wxExtTxt.setText(wx.text);

        //IF it is day
        if (wx.wxImgDay) {
            if (wx.wxImgRes.equalsIgnoreCase("Storm")) {
                holder.wxImg.setImageResource(R.drawable.weather_storm_day);
            } else if (wx.wxImgRes.equalsIgnoreCase("Sun")) {
                        holder.wxImg.setImageResource(R.drawable.weather_clear);
            } else {
                holder.wxImg.setImageResource(R.drawable.weather_few_clouds);
            }
        } else {
            if (wx.wxImgRes.equalsIgnoreCase("Storm")) {
                holder.wxImg.setImageResource(R.drawable.weather_storm);
            } else if (wx.wxImgRes.equalsIgnoreCase("Clear")) {
                holder.wxImg.setImageResource(R.drawable.weather_clear_night);
            } else {
                holder.wxImg.setImageResource(R.drawable.weather_few_clouds_night);
            }
        }


    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        protected TextView validTxt;
        protected TextView tempTxt;
        protected TextView wxTxt;
        protected ImageView wxImg;
        protected TextView wxExtTxt;

        public MyViewHolder(View itemView) {
            super(itemView);
            validTxt= (TextView) itemView.findViewById(R.id.card_valid);
            tempTxt= (TextView) itemView.findViewById(R.id.card_temper);
            wxTxt= (TextView) itemView.findViewById(R.id.card_wx);
            wxImg = (ImageView) itemView.findViewById(R.id.card_wxImage);
            wxExtTxt = (TextView) itemView.findViewById(R.id.wxText);


        }
    }


}
