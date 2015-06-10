package com.ii.mobile.flow.types;

import java.lang.reflect.Type;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.ii.mobile.flow.types.GetActionStatus.Targets;

public class TargetsDeserializer implements JsonDeserializer<Targets[]>
{
	@Override
	public Targets[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException
	{
		if (json instanceof JsonArray)
		{
			// L.out("TargetDeserializer found an array");
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE);
			Gson gson = gsonBuilder.create();
			return gson.fromJson(json, Targets[].class);
		}
		// L.out("TargetDeserializer found an element!");
		Targets targets = context.deserialize(json, Targets.class);
		return new Targets[] { targets };
	}

}