package utah.cs5957.a1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.ViewGroup;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Converter c = new Converter();
	private List<String> distance, mass, volume, temperature, speed;
	private Map<String, List<String>> units_list;
	private RelativeLayout parent;
	private RecognitionListener listener;
	private SpeechRecognizer sr;
	private EditText inText;
	private TextView outText;
	private Button speakButton;
	Button calculate;
	Spinner unitType;
	Spinner inUnits;
	Spinner outUnits;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {

		buildLists();
		
		super.onCreate(savedInstanceState);

		parent = new RelativeLayout(this);
		parent.setPadding(16, 16, 16, 16);

		speakButton = new Button(this);
		speakButton.setText("Voice");
		// listener = setUpVoice();
		sr = SpeechRecognizer.createSpeechRecognizer(this);
		sr.setRecognitionListener(setUpVoice());

		inText = new EditText(this);
		inText.setHint("INPUT UNITS");
		inText.setInputType(InputType.TYPE_CLASS_NUMBER);
		inText.setTextSize(16.0f);
		inText.setId(1);

		outText = new TextView(this);
		outText.setText("Output Unit Equivalent");
		outText.setAlpha(20f);
		outText.setTextSize(16.0f);
		outText.setId(2);

		unitType = new Spinner(this);
		unitType.setAdapter(unitsSpinnerAdapter());

		unitType.setId(3);

		inUnits = new Spinner(this);
		inUnits.setAdapter(updateUnitsList(""));
		inUnits.setId(4);

		outUnits = new Spinner(this);
		outUnits.setAdapter(updateUnitsList(""));
		outUnits.setId(5);

		calculate = new Button(this);
		calculate.setText("Convert");
		calculate.setId(6);
		calculate.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (inText.length() > 0) {
					String s = inText.getText().toString();
					String __out = String.format("to %s", outUnits
							.getSelectedItem().toString());
					double d = Double.parseDouble(s);
					String result = c.convert(unitType.getSelectedItem()
							.toString(), d, inUnits.getSelectedItem()
							.toString(), __out);
					outText.setText(result);

				}
			}
		});

		RelativeLayout.LayoutParams input_one = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		RelativeLayout.LayoutParams out_text = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		RelativeLayout.LayoutParams unit_type = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		RelativeLayout.LayoutParams inU = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		RelativeLayout.LayoutParams outU = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		RelativeLayout.LayoutParams calcButt = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		RelativeLayout.LayoutParams speakButt = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);

		unit_type.addRule(RelativeLayout.ALIGN_PARENT_TOP, parent.getId());
		unit_type.addRule(RelativeLayout.CENTER_VERTICAL, parent.getId());
		unit_type.addRule(RelativeLayout.ALIGN_PARENT_LEFT, parent.getId());
		// unitType.setOnItemSelectedListener(new SpinnerActivity());
		unitType.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {

				// Spinner a = (Spinner)parent.getChildAt(4);
				// Spinner b = (Spinner)parent.getChildAt(5);
				String s = parent.getItemAtPosition(pos).toString()
						.toLowerCase();
				// Toast toast =
				// Toast.makeText(parent.getContext(),"You've chosen: " + s, 2);
				// toast.show();
				inUnits.setAdapter(updateUnitsList(s));
				outUnits.setAdapter(updateUnitsList(s));
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}

		});
		unitType.setLayoutParams(unit_type);
		parent.addView(unitType);

		input_one.addRule(RelativeLayout.BELOW, unitType.getId());
		input_one.addRule(RelativeLayout.ALIGN_PARENT_LEFT, parent.getId());
		input_one.addRule(RelativeLayout.ALIGN_RIGHT, inUnits.getId());
		inText.setLayoutParams(input_one);
		parent.addView(inText);

		out_text.addRule(RelativeLayout.BELOW, inText.getId());
		outText.setLayoutParams(out_text);
		parent.addView(outText);

		inU.addRule(RelativeLayout.BELOW, unitType.getId());
		inU.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, parent.getId());
		inUnits.setLayoutParams(inU);
		parent.addView(inUnits);

		outU.addRule(RelativeLayout.BELOW, inUnits.getId());
		outU.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, parent.getId());
		outUnits.setLayoutParams(outU);
		parent.addView(outUnits);

		calcButt.addRule(RelativeLayout.BELOW, outUnits.getId());
		calculate.setLayoutParams(calcButt);

		speakButt.addRule(RelativeLayout.BELOW, calculate.getId());
		// parent.addView(speakButton);
		parent.addView(calculate);

		setContentView(parent);
	}

	private RecognitionListener setUpVoice() {

		RecognitionListener l = new RecognitionListener() {

			public void onReadyForSpeech(Bundle params) {
				// Log.d(TAG, "onReadyForSpeech");
			}

			public void onBeginningOfSpeech() {
				// Log.d(TAG, "onBeginningOfSpeech");
			}

			public void onRmsChanged(float rmsdB) {
				// Log.d(TAG, "onRmsChanged");
			}

			public void onBufferReceived(byte[] buffer) {
				// Log.d(TAG, "onBufferReceived");
			}

			public void onEndOfSpeech() {
				// Log.d(TAG, "onEndofSpeech");
			}

			public void onError(int error) {
				// Log.d(TAG, "error " + error);
				inText.setText("error " + error);
			}

			public void onResults(Bundle results) {
				String str = new String();
				// Log.d(TAG, "onResults " + results);
				ArrayList data = results
						.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
				for (int i = 0; i < data.size(); i++) {
					// Log.d(TAG, "result " + data.get(i));
					str += data.get(i);
				}
				inText.setText("results: " + String.valueOf(data.size()));
			}

			public void onPartialResults(Bundle partialResults) {
				// Log.d(TAG, "onPartialResults");
			}

			public void onEvent(int eventType, Bundle params) {
				// Log.d(TAG, "onEvent " + eventType);
			}
		};
		return l;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void addListenerOnSpinnerItemSelection() {

		unitType.setOnItemSelectedListener(new CustomOnItemSelectedListener());
	}

	/**
	 * HELPER METHODS
	 * 
	 */

	public Spinner createSpinner() {
		Spinner s = new Spinner(this);
		return s;
	}

	public List<String> createList() {
		List<String> items = new ArrayList<String>();
		items.add("Distance");
		items.add("Mass");
		items.add("Volume");
		items.add("Temperature");
		items.add("Speed");
		return items;
	}

	public ArrayAdapter<String> unitsSpinnerAdapter() {
		List<String> list = createList();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapter.notifyDataSetChanged();
		return adapter;
	}

	@SuppressLint("NewApi")
	public ArrayAdapter<String> updateUnitsList(String list) {

		if (!list.isEmpty()) {
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, units_list.get(list));
			return adapter;
		} else {
			List<String> l = new ArrayList<String>();
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, l);
			return adapter;
		}
	}

	public void setLists(String type) {
		outUnits.setAdapter(updateUnitsList(type));
	}

	private void buildLists() {
		units_list = new HashMap<String, List<String>>();
		distance = new ArrayList<String>();
		mass = new ArrayList<String>();
		volume = new ArrayList<String>();
		temperature = new ArrayList<String>();
		speed = new ArrayList<String>();

		distance.add("millimeter");
		distance.add("centimeter");
		distance.add("meter");
		distance.add("kilometer");

		distance.add("inch");
		distance.add("foot");
		distance.add("yard");
		distance.add("mile");
		distance.add("nautical mile");

		mass.add("milligram");
		mass.add("gram");
		mass.add("kilogram");
		mass.add("metric ton");
		mass.add("ounce");
		mass.add("pound");
		mass.add("stone");

		volume.add("milliliter");
		volume.add("liter");
		volume.add("cubic meter");
		volume.add("tsp");
		volume.add("tbsp");
		volume.add("oz");
		volume.add("cup");
		volume.add("pint");
		volume.add("quart");
		volume.add("gallon");

		temperature.add("farenheit");
		temperature.add("celsius");
		temperature.add("kelvin");

		speed.add("mph");
		speed.add("kph");
		speed.add("fps");
		speed.add("m/s");
		speed.add("knot");


		units_list.put("distance", distance);
		units_list.put("mass", mass);
		units_list.put("volume", volume);
		units_list.put("temperature", temperature);
		units_list.put("speed", speed);
	}

}
