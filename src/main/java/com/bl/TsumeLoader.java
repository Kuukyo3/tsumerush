package com.bl;

import java.io.BufferedInputStream;
import java.io.File;

import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.json.simple.*;
import org.json.simple.parser.*;

public class TsumeLoader {

    public ArrayList<TsumePosition> getTsume(int moveCount) {
        JSONParser parser = new JSONParser();
        ArrayList<TsumePosition> tsume1te = new ArrayList<>();
        try {
            // File file = new
            // File(getClass().getClassLoader().getResource("sfen_jsons/1te_sfens.json").getFile());

            InputStream input = getInputStream("sfen_jsons/" + moveCount + "te_sfens.json");
            JSONArray tsumeArray = (JSONArray) parser.parse(new InputStreamReader(input));

            for (Object object : tsumeArray) {
                JSONArray array = (JSONArray) object;
                String sfen = (String) array.toArray()[0];
                JSONArray moveArray = (JSONArray) array.toArray()[1];
                ArrayList<String> tmpArr = new ArrayList<>();
                for (Object o : moveArray) {
                    tmpArr.add((String) o);
                }

                TsumePosition tmpTsume = new TsumePosition(sfen, tmpArr.toArray(new String[0]));
                tsume1te.add(tmpTsume);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tsume1te;
    }

    private static BufferedInputStream getInputStream(String resource) {
        InputStream input = AudioHelper.class.getResourceAsStream("/res/" + resource);
        if (input == null) {
            // this is how we load file within editor (eg eclipse)
            input = AudioHelper.class.getClassLoader().getResourceAsStream(resource);
        }

        return new BufferedInputStream(input);
    }

}