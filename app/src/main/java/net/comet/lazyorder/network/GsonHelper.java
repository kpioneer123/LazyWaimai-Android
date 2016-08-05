package net.comet.lazyorder.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import net.comet.lazyorder.model.bean.Gender;
import net.comet.lazyorder.model.bean.OrderStatus;
import net.comet.lazyorder.model.bean.PayMethod;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by comet on 15/8/24.
 */
public class GsonHelper {

    private static final SimpleDateFormat JSON_STRING_DATE = new SimpleDateFormat("yyy-MM-dd", Locale.CHINA);
    private static final TimeZone TRAKT_TIME_ZONE = TimeZone.getTimeZone("GMT-8:00");
    private static final long SECOND_IN_MILLISECONDS = 1000L;

    public static Gson builderGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Integer.class, new JsonDeserializer() {
            @Override
            public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                try {
                    return Integer.valueOf(json.getAsInt());
                } catch (NumberFormatException var5) {
                    return null;
                }
            }
        });
        builder.registerTypeAdapter(Date.class, new JsonDeserializer() {
            @Override
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                try {
                    long outer = json.getAsLong();
                    Calendar date = Calendar.getInstance(TRAKT_TIME_ZONE);
                    date.setTimeInMillis(outer * SECOND_IN_MILLISECONDS);
                    return date.getTime();
                } catch (NumberFormatException var8) {
                    try {
                        return JSON_STRING_DATE.parse(json.getAsString());
                    } catch (ParseException var7) {
                        throw new JsonParseException(var8);
                    }
                }
            }
        });
        builder.registerTypeAdapter(OrderStatus.class, new JsonDeserializer<OrderStatus>() {
            @Override
            public OrderStatus deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return OrderStatus.valueOf(json.getAsInt());
            }
        });
        builder.registerTypeAdapter(PayMethod.class, new JsonDeserializer<PayMethod>() {
            @Override
            public PayMethod deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return PayMethod.valueOf(json.getAsInt());
            }
        });
        builder.registerTypeAdapter(Gender.class, new JsonDeserializer<Gender>() {
            @Override
            public Gender deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return Gender.valueOf(json.getAsInt());
            }
        });
        return builder.create();
    }
}
