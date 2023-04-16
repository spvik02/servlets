package org.clevertec.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeAdapter extends TypeAdapter<LocalTime> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH-mm-ss");


    @Override
    public void write(final JsonWriter jsonWriter, final LocalTime localTime ) throws IOException {
        jsonWriter.value(formatter.format(localTime));
    }

    @Override
    public LocalTime read( final JsonReader jsonReader ) throws IOException {
        return LocalTime.parse(jsonReader.nextString(), formatter);
    }
}
