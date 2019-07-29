package com.example.kotu9.gpsgame.Fragment.EventCreation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kotu9.gpsgame.Model.Event;
import com.example.kotu9.gpsgame.Model.Hint;
import com.example.kotu9.gpsgame.R;

import java.util.ArrayList;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;


public class CreateEventHints extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {


	public CreateEventHints() {

	}

	private Button btnSubmitHints, btnAddHint;
	private EditText editTexthints;
	private Hint hintList;
	private ListView listView;
	private Event event;
	private ArrayAdapter<String> adapter;
	private NavController navController;


	public static CreateEventHints newInstance() {
		CreateEventHints fragment = new CreateEventHints();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		hintList.hints = new ArrayList<>();
		event = ((Event) getArguments().get(String.valueOf(R.string.eventBundle)));
		Toast.makeText(getContext(), event.toString(), Toast.LENGTH_LONG).show();
		if (getArguments() != null) {
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_create_event_hints, container, false);

		adapter = new ArrayAdapter<String>(getContext(),
				android.R.layout.simple_list_item_1, hintList.hints);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		setupViews(view);
		return view;
	}


	private void setupViews(View view) {
		btnSubmitHints = view.findViewById(R.id.submitHints);
		btnAddHint = view.findViewById(R.id.buttonAddHint);
		editTexthints = view.findViewById(R.id.editTextHint);
		listView = view.findViewById(R.id.hintList);
		navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
		btnAddHint.setOnClickListener(this);
		btnSubmitHints.setOnClickListener(this);
	}


	private void addHintToList() {
		hintList.hints.add(getHintFromEditText());
		Toast.makeText(getContext(), hintList.toString(), Toast.LENGTH_LONG).show();
		adapter.notifyDataSetChanged();
		editTexthints.setText("");
	}

	private String getHintFromEditText() {
		checkHintField();
		return editTexthints.getText().toString().trim();
	}

	private void checkHintField() {
		if (TextUtils.isEmpty(editTexthints.getText().toString())) {
			editTexthints.setError("Please enter hint");
			editTexthints.requestFocus();
			return;
		}
		if (editTexthints.getText().length() > 1 && editTexthints.getText().length() < 25) {
			editTexthints.setError("Hint should be longer");
			editTexthints.requestFocus();
			return;
		}
	}

	private void submitEventHints() {
		setEventHints();
		Bundle eventBundle = new Bundle();
		eventBundle.putSerializable(String.valueOf(R.string.eventBundle), event);
		navController.navigate(R.id.createEventMarker, eventBundle);
	}

	private void setEventHints() {
		event.setHintList(hintList);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.submitHints:
				submitEventHints();
				break;

			case R.id.buttonAddHint:
				addHintToList();
				break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		hintList.hints.remove(position);
		adapter.notifyDataSetChanged();
	}
}
