package utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;

public class Utility {

    public static final ObjectMapper obm = new ObjectMapper();

    static {
        obm.setDateFormat(new SimpleDateFormat(""));
    }

}
