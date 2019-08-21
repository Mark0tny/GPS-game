package com.example.kotu9.gpsgame.fragment.eventCreation;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.activity.CreateEventActivity;
import com.example.kotu9.gpsgame.model.Event;
import com.example.kotu9.gpsgame.model.EventType;
import com.example.kotu9.gpsgame.utils.EventDifficulty;
import com.example.kotu9.gpsgame.utils.EventTypes;


public class CreateEventInfo extends Fragment implements View.OnFocusChangeListener, View.OnClickListener {

    private boolean checkFildsFilled = false;
    public Spinner spinnerEvent;
    SpinnerListener spinnerListener;
    private Event event;
    private EditText eventName, eventDescription;
    private SeekBar seekBarDiff;
    private TextView difficulty;
    public Button btnSubmit;
    private static int spinnerPosition;


    public CreateEventInfo() {
    }

    public static CreateEventInfo newInstance() {
        CreateEventInfo fragment = new CreateEventInfo();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        event = new Event();
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
        btnSubmit = view.findViewById(R.id.submitInfo);
        btnSubmit.setOnClickListener(this);
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void updateEventType(int position) {
        if (position != 0) spinnerPosition = position;
        switch (position) {
            case 1:
                event.setEventType(new EventType(EventTypes.Location));
                break;
            case 2:
                event.setEventType(new EventType(EventTypes.QRcode));
                break;
            case 3:
                event.setEventType(new EventType(EventTypes.Quiz));
                break;
            case 4:
                event.setEventType(new EventType(EventTypes.PhotoCompare));
                break;
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

    @Override
    public void onClick(View v) {
        checkIfFildsAreCompleted();
        Toast.makeText(getContext(), event.toString(), Toast.LENGTH_SHORT).show();
        Bundle eventBundle = new Bundle();
        eventBundle.putSerializable(String.valueOf(R.string.eventBundle), event);
        if (checkFildsFilled) {
            spinnerListener.onSpinnerEventSelected(spinnerPosition, eventBundle);
        }
    }

    public interface SpinnerListener {
        void onSpinnerEventSelected(Integer position, Bundle bundle);
    }


    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void checkIfFildsAreCompleted() {
        event.setName(eventName.getText().toString().trim());
        event.setDescription(eventDescription.getText().toString().trim());
        int i = 0;

        if (seekBarDiff.getProgress() == 0) event.setDifficulty(EventDifficulty.Easy);
        if (spinnerPosition == 0) {
            ((TextView) spinnerEvent.getSelectedView()).setError("Please select event type");
            i++;
            return;
        }
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
        if (spinnerPosition == 0) {
            ((TextView) spinnerEvent.getSelectedView()).setError("Please select event type");
            i++;
            return;
        }
        if (i % 4 == 0) checkFildsFilled = true;
    }
}





