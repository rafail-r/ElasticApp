package gr.ntua.ece.elasticapp.elasticapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MainCustomAdapter extends ArrayAdapter<LiveResult> {

    LayoutInflater vi;

    public MainCustomAdapter(Context context, int textViewResourceId, LayoutInflater layoutInflater) {
        super(context, textViewResourceId);
        vi = layoutInflater;
    }

    private class ViewHolder {
        TextView code;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = vi.inflate(R.layout.live_result_item, parent, false);
            holder = new ViewHolder();
            holder.code = (TextView) convertView.findViewById(R.id.textView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        LiveResult result = this.getItem(position);
        holder.code.setText(result.getName());

        return convertView;
    }
}