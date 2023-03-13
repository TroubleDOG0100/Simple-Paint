package utility;

import java.util.HashMap;

public class Validator {

	public boolean success = true;
	public String message = "";
	public HashMap<String, String> fields = new HashMap<>();
	
	public Validator() {
		
	}
	
	public Validator(HashMap<String, String> fields) {
		fields.forEach((k, v) -> {
			this.addField(k, v);
		});
	}
	
	public void addField(String key, String v) {
		System.out.println(v);
		
		// Remove any extra white spaces between.
		v = v.trim();
		v = v.replaceAll(" ", "");
		
		System.out.println(v);
		
		fields.put(key, v);
	}

	public void appendError(String message) {
		this.message  += message + "\n";
		success = false;
	}

	public String getField(String key) {
		return fields.get(key);
	}

}
