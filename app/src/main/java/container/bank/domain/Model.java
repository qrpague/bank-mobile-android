package container.bank.domain;

import java.io.Serializable;
import java.util.HashMap;

import com.google.gson.Gson;

import org.json.JSONException;

import container.bank.tools.Tools;


public abstract class Model implements Serializable {

	public String toJson() {
		Gson gson = new Gson();
		String gsonString = gson.toJson(this);
		return gsonString;
	}

	public HashMap<String, String> getMapJson() {
		try {
			return Tools.jsonToMap(toJson());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}



}
