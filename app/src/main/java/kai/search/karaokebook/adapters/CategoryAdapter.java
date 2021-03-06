package kai.search.karaokebook.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import kai.search.karaokebook.R;
import kai.search.karaokebook.db.FavouriteCategory;

/**
 * Created by kjwon15 on 2014. 7. 16..
 */
public class CategoryAdapter extends ArrayAdapter<FavouriteCategory> {
    private List<FavouriteCategory> list;
    private LayoutInflater inflater;

    public CategoryAdapter(Context context, List<FavouriteCategory> list) {
        super(context, R.layout.listitem_favourite_category, list);
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public FavouriteCategory getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.listitem_favourite_category, null);
        }
        FavouriteCategory category = list.get(position);
        if (category != null) {
            TextView name = (TextView) view.findViewById(R.id.text1);

            name.setText(category.getCategoryName());
        }
        return view;
    }
}
