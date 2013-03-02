package utah.cs5957.a1;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Converter {

	private Map<String, Map> unit_types = new HashMap<String, Map>();
	private Map<String, Double> distance = new HashMap<String, Double>();
	private Map<String, Double> volume = new HashMap<String, Double>();
	private Map<String, Double> mass = new HashMap<String, Double>();
	private Map<String, Double> temperature = new HashMap<String, Double>();
	private Map<String, Double> speed = new HashMap<String, Double>();
	private Map<String, Double> time = new HashMap<String, Double>();

	public Converter() {

		unit_types.put("distance", distance);
		unit_types.put("volume", volume);
		unit_types.put("mass", mass);
		unit_types.put("temperature", temperature);
		unit_types.put("speed", speed);
		unit_types.put("time", time);

		time.put("millisecond", 0.001);
		time.put("second", 1.0);
		time.put("minute", 60.0);
		time.put("hour", 3600.0);
		time.put("day", 86400.0);
		time.put("month", 2.63e6);

		speed.put("mph", 1.0);
		speed.put("kph", 1.60934);
		speed.put("fps", 1.46667);
		speed.put("m/s", 0.44704);
		speed.put("Knot", 0.868976);
			
		volume.put("liter", 1.0);
		volume.put("milliliter", 0.001);
		volume.put("cubic meter", 1000.0);
		volume.put("to oz", 33.814);
		volume.put("to gallon", 0.264172);
		volume.put("to quart", 1.05669);
		volume.put("to pint", 2.11338);
		volume.put("to cup", 4.22675);
		volume.put("to tbsp", 67.628);
		volume.put("to tsp", 202.884);

		volume.put("oz", 1.0);
		volume.put("gallon", 0.0078125);
		volume.put("quart", 0.03125);
		volume.put("pint", 0.0625);
		volume.put("cup", 0.125);
		volume.put("tbsp", 2.0);
		volume.put("tsp", 6.0);
		volume.put("to liter", 0.0295735);
		volume.put("to milliliter", 29.5735);
		volume.put("to cubic meter", 2.9574e-5);

		mass.put("pound", 1.0);
		mass.put("ounce", 0.0625);
		mass.put("stone", 14.0);
		mass.put("to kilogram", 0.453592);
		mass.put("to gram", 453.592);
		mass.put("to milligram", 453592.0);
		mass.put("to metric ton", 0.000453592);

		mass.put("gram", 0.001);
		mass.put("kilogram", 1.0);
		mass.put("milligram", 1e-6);
		mass.put("metric ton", 1000.0);
		mass.put("to pound", 2.2);
		mass.put("to ounce", 2.2);
		mass.put("to stone", 2.2);

		distance.put("mile", 5280.0);
		distance.put("foot", 1.0);
		distance.put("inch", 0.083333);
		distance.put("yard", 3.0);
		distance.put("nautical mile", 6076.12);
		distance.put("to meter", 0.3048);
		distance.put("to kilometer", 0.0003048);
		distance.put("to centimeter", 30.48);
		distance.put("to millimeter", 304.8);

		distance.put("kilometer", 1000.0);
		distance.put("meter", 1.0);
		distance.put("millimeter", 0.001);
		distance.put("centimeter", 0.01);
		distance.put("to foot", 3.28084);
		distance.put("to inch", 39.3701);
		distance.put("to yard", 1.09361);
		distance.put("to mile", 0.000621371);
		distance.put("to nautical mile", 0.000539957);

	}

	private boolean isMetric(String unit) {
		Pattern p = Pattern.compile("(?i).*gram|.*liter|.*meter");
		Matcher m = p.matcher(unit);

		return m.matches();
	}

	public String convert(String format, double in, String in_unit,
			String out_unit) {
		double intermediate, res;
		try {

			if (format.toLowerCase().equals("temperature")) {
				if (out_unit.equals("to celsius")) {
					return convert_celsius(in, in_unit).toString();
				} else if (out_unit.equals("to farenheit")) {
					return convert_farenheit(in, in_unit).toString();
				} else if (out_unit.equals("to kelvin")) {
					return convert_kelvin(in, in_unit).toString();
				}
			}
			
			MathContext mc = new MathContext(7, RoundingMode.UP);

			BigDecimal b_intermediate, b_value, b_input, b_dicIn, b_dicOut, b_output;
			BigDecimal b_res = new BigDecimal(0);

			boolean input_isMetric = isMetric(in_unit);
			boolean output_isMetric = isMetric(out_unit);
			@SuppressWarnings("unchecked")
			Map<String, Double> dic = unit_types.get(format.toLowerCase());
			Double value = in * dic.get(in_unit);

			// ========== BIG DECIMAL STUFF ===========//
			b_input = new BigDecimal(in);
			b_dicIn = new BigDecimal(dic.get(in_unit));
			b_value = b_input.multiply(b_dicIn, mc);
			// =========================================


			if (format.toLowerCase().equals("speed")) {
				b_intermediate = new BigDecimal(dic.get(out_unit.split(" ")[1]));
				b_res = b_value.multiply(b_intermediate, mc);
			}
			else if (input_isMetric != output_isMetric) {
				intermediate = dic.get(out_unit);
				res = value * intermediate;

				// ====== BIG DECIMAL STUFF =====//
				b_intermediate = new BigDecimal(dic.get(out_unit));
				b_res = b_value.multiply(b_intermediate, mc);

			} else if (input_isMetric == output_isMetric){
				intermediate = dic.get(out_unit.split(" ")[1]);
				res = value / intermediate;

				// ====== BIG DECIMAL STUFF =====//
				b_intermediate = new BigDecimal(dic.get(out_unit.split(" ")[1]));
				b_res = b_value.divide(b_intermediate, mc);
			}

			// return String.format("%s/t%s/t%s", format, in_unit, out_unit);
			// return Double.toString(res);
			return b_res.toEngineeringString();

		} catch (Exception e) {
			return String.format("An error occurred while calculating");
		}

	}

	private Double convert_celsius(Double in, String in_units) {
		Double res = in;
		if (in_units.equals("farenheit")) {
			res =  (5.0d/9.0d) * (in - 32.0d);
		} else if (in_units.equals("kelvin")) {
			res = ((5.0d/9.0d) * (in - 32.0f)) - 273.0d;
		}

		return res;
	}

	private Double convert_farenheit(Double in, String in_units) {
		Double res = (9.0d / 5.0d) * (in + 32.0d);
		if (in_units.equals("celsius")) {
			return res;
		} else if (in_units.equals("kelvin")) {
			return res + 273.0d;
		}

		return res;
	}

	private Double convert_kelvin(Double in, String in_units) {
		Double res = in;
		if (in_units.equals("celsius")) {
			res = in + 273.0d;
		} else if (in_units.equals("farenheit")) {
			res = (5.0d / 9.0d) * (in - 32.0d);
			res = res + 273.0d;
		}
		return res;
	}

}
