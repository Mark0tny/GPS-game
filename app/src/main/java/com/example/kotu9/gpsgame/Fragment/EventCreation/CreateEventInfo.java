package com.example.kotu9.gpsgame.Fragment.EventCreation;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.kotu9.gpsgame.Activity.CreateEventActivity;
import com.example.kotu9.gpsgame.Model.Event;
import com.example.kotu9.gpsgame.Model.EventType;
import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.Utils.EventDifficulty;
import com.example.kotu9.gpsgame.Utils.EventTypes;


public class CreateEventInfo extends Fragment implements View.OnFocusChangeListener {
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";


	private String mParam1;
	private String mParam2;
	private boolean checkFildsFilled = false;
	public Spinner spinnerEvent;
	SpinnerListener spinnerListener;
	private Event event;
	private EditText eventName, eventDescription;
	private SeekBar seekBarDiff;
	private TextView difficulty;


	public CreateEventInfo() {
	}

	public static CreateEventInfo newInstance(String param1, String param2) {
		CreateEventInfo fragment = new CreateEventInfo();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		event = new Event();
		if (getArguments() != null) {
			mParam1 = getArguments().getString(ARG_PARAM1);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_create_event_info, container, false);

		setUpComponents(view);

		seekBarDiff.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				updateDifficulty(progress);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});
		return view;
	}

	private void updateDifficulty(int progress) {
		if (progress >= 0 && progress < 3) {
			difficulty.setText(EventDifficulty.Easy.name());
			difficulty.setTextColor(EventDifficulty.Easy.getColor());
			event.setDifficulty(EventDifficulty.Easy);
		} else if (progress >= 3 && progress < 5) {
			difficulty.setText(EventDifficulty.Medium.name());
			difficulty.setTextColor(EventDifficulty.Medium.getColor());
			event.setDifficulty(EventDifficulty.Medium);
		} else {
			difficulty.setText(EventDifficulty.Hard.name());
			difficulty.setTextColor(EventDifficulty.Hard.getColor());
			event.setDifficulty(EventDifficulty.Hard);
		}
	}

	private void setUpComponents(View view) {
		spinnerEvent = view.findViewById(R.id.spinnerEvent);
		eventName = view.findViewById(R.id.editNameEvent);
		eventName.setOnFocusChangeListener(this);
		eventDescription = view.findViewById(R.id.editEventDescription);
		eventDescription.setOnFocusChangeListener(this);

		difficulty = view.findViewById(R.id.seekBarDiffChange);

		seekBarDiff = view.findViewById(R.id.seekBarDifficulty);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				R.layout.support_simple_spinner_dropdown_item,
				getResources().getStringArray(R.array.Types));
		adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
		spinnerEvent.setAdapter(adapter);
		spinnerEvent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				updateEventType(position);
				checkIfFildsAreCompleted();
				if (checkFildsFilled){
					spinnerListener.onSpinnerEventSelected(position);
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}

	private void updateEventType(int position) {
		switch (position) {
			case 1:
				event.setEventType(new EventType(EventTypes.Location));
			case 2:
				event.setEventType(new EventType(EventTypes.QRcode));
			case 3:
				event.setEventType(new EventType(EventTypes.Quiz));
			case 4:
				event.setEventType(new EventType(EventTypes.PhotoCompare));
		}
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof CreateEventActivity)
			spinnerListener = ((CreateEventActivity) context);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		spinnerListener = null;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (!hasFocus) hideKeyboard(v);
	}

	public interface SpinnerListener {
		void onSpinnerEventSelected(Integer position);
	}


	public void hideKeyboard(View view) {
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public void checkIfFildsAreCompleted() {
		event.setName(eventName.getText().toString().trim());
		event.setDescription(eventDescription.getText().toString().trim());
		int i = 0;

		if (TextUtils.isEmpty(event.getName())) {
			eventName.setError("Please enter event name");
			eventName.requestFocus();
			i++;
			return;
		}
		if (TextUtils.isEmpty(event.getDescription())) {
			eventDescription.setError("Please enter event description");
			eventDescription.requestFocus();
			i++;
			return;
		}
		if (TextUtils.isEmpty(event.getDifficulty().name())) {
			difficulty.setError("Please select event difficulty");
			difficulty.requestFocus();
			i++;
			return;
		}
		if (i % 3 == 0) checkFildsFilled = true;
	}
}





