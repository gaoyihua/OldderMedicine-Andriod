package com.gary.olddermedicine.view.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class HttpUtil {

    public static String is2String(InputStream is){

        //连接后，创建一个输入流来读取response
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new
                    InputStreamReader(is,"utf-8"));
            String line = "";
            StringBuilder stringBuilder = new StringBuilder();
            String response = "";
            //每次读取一行，若非空则添加至 stringBuilder
            while((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }
            return stringBuilder.toString().trim();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
