package kai.search.karaokebook;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kai.search.karaokebook.adapters.SongAdapter;
import kai.search.karaokebook.db.DbAdapter;
import kai.search.karaokebook.db.Song;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements
        TextView.OnEditorActionListener, AdapterView.OnItemSelectedListener {
    private OnFragmentInteractionListener mListener;

    private Spinner spinnerVendor;
    private Spinner spinnerSearchCategory;
    private EditText editSearchString;
    private ListView listSearchResult;

    private ArrayList<Song> list;
    private SongAdapter adapter;

    private DbAdapter dbAdapter;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        list = new ArrayList<Song>();
        adapter = new SongAdapter(getActivity(), list);
        dbAdapter = new DbAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        spinnerVendor = (Spinner) view.findViewById(R.id.spinnerVendor);
        spinnerSearchCategory = (Spinner) view.findViewById(R.id.spinnerSearchCategory);
        editSearchString = (EditText) view.findViewById(R.id.editSearchString);
        listSearchResult = (ListView) view.findViewById(R.id.listSearchResult);

        editSearchString.setOnEditorActionListener(this);
        spinnerSearchCategory.setOnItemSelectedListener(this);
        spinnerVendor.setOnItemSelectedListener(this);

        setupSpinner(spinnerVendor, R.array.vendor);
        setupSpinner(spinnerSearchCategory, R.array.searchCategory);
        listSearchResult.setAdapter(adapter);

        return view;
    }

    private void setupSpinner(Spinner spinner, int array) {
        ArrayAdapter<CharSequence> adapter;
        adapter = ArrayAdapter.createFromResource(getActivity(), array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((Main) activity).onSectionAttached(getString(R.string.title_search));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void search() {
        String query = editSearchString.getText().toString();
        int vendorPosition = spinnerVendor.getSelectedItemPosition();
        String vendor = spinnerVendor.getSelectedItem().toString();
        int category = spinnerSearchCategory.getSelectedItemPosition();

        String queryVendor = null;
        String queryTitle = null;
        String querySinger = null;
        String queryNumber = null;

        if (vendorPosition > 0) {
            queryVendor = vendor;
        }

        switch (category) {
            case 0:
                queryTitle = query;
                break;
            case 1:
                querySinger = query;
                break;
            case 2:
                queryNumber = query;
                break;
        }

        List<Song> songs = dbAdapter.getSongs(queryVendor, queryTitle, queryNumber, querySinger);
        list.clear();
        list.addAll(songs);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            search();
            return true;
        }
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        search();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
