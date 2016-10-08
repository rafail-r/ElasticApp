package gr.ntua.ece.elasticapp.elasticapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

public class ResultsCustomAdapter extends ArrayAdapter<Place> {
    LayoutInflater vi;

    public ResultsCustomAdapter(Context context, int textViewResourceId, LayoutInflater layoutInflater) {
        super(context, textViewResourceId);
        vi = layoutInflater;
    }

    private class ViewHolder {
        TextView name;
        TextView address;
        TextView type;
        RatingBar ratingBar;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = vi.inflate(R.layout.result_item, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.TextName);
            holder.address = (TextView) convertView.findViewById(R.id.TextAddress);
            holder.type = (TextView) convertView.findViewById(R.id.TextType);
            holder.ratingBar = (RatingBar) convertView.findViewById(R.id.RatingBar);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Place result = this.getItem(position);
        holder.name.setText(result.getName());
        holder.address.setText(result.getAddress());
        holder.type.setText(result.getType());
        holder.ratingBar.setRating(result.getRating());
        return convertView;
    }
}
