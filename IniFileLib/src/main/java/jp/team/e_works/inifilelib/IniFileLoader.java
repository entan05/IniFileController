package jp.team.e_works.inifilelib;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class IniFileLoader {
    // HashMap<section, HashMap<key, value>>とする
    private HashMap<String, HashMap<String, String>> mDataMap;

    // ファイルパス
    private String mFilePath;

    // 文字コード
    private Charset mCharSet;

    // load完了しているか
    private boolean mIsLoaded = false;


    public IniFileLoader() {
        mDataMap = new HashMap<>();
    }

    /**
     * 指定したファイルをUTF-8のiniファイルとしてロードする
     * @param filePath ファイルパス
     * @return ロードに成功したかどうか
     */
    public boolean load(String filePath) {
        return loadProcess(filePath, StandardCharsets.UTF_8);
    }

    /**
     * 指定したファイルを指定した文字コードのiniファイルとしてロードする
     * @param filePath ファイルパス
     * @param cs 文字コード
     * @return ロードに成功したかどうか
     */
    public boolean load(String filePath, Charset cs) {
        mFilePath = filePath;
        mCharSet = cs;
        return loadProcess(mFilePath, mCharSet);
    }

    /**
     * 前回ロードに成功したファイルをもう一度ロードする
     * @return ロードに成功したかどうか
     */
    public boolean reload() {
        if(mIsLoaded) {
            return loadProcess(mFilePath, mCharSet);
        }
        return false;
    }

    private boolean loadProcess(String filePath, Charset cs) {
        try {
            // セクション名
            String section = null;
            // キーバリュー
            HashMap<String, String> map = new HashMap<>();
            for(String line : Files.readAllLines(Paths.get(filePath), cs)) {
                // BOM取り除き処理
                // todo

                // 行頭、行末の空白を取り除く
                line = line.trim();

                // 空行、コメント行
                if(isEmpty(line) || line.charAt(0) == '#') {
                    // no process
                    continue;
                }
                // セクション行
                if (line.charAt(0) == '[' && line.charAt(line.length()-1) == ']') {
                    section = line.substring(1, line.length()-1);
                    map = new HashMap<>();
                    continue;
                }
                // パラメータ行
                if (line.length() >= 3 && line.contains("=") && line.length() > line.indexOf("=")+1) {
                    String key = line.substring(0, line.indexOf("=")-1);
                    String value = line.substring(line.indexOf("=") +1);

                    map.put(key, value);
                    mDataMap.put(section, map);
                }
            }
        } catch (IOException e) {
            mIsLoaded = false;
            return false;
        }
        mIsLoaded = true;
        return true;
    }

    public HashMap<String, HashMap<String, String>> getAllDatas() {
        if(mIsLoaded) {
            return mDataMap;
        }
        throw new NotLoadedException();
    }

    public HashMap<String, String> getSectionDatas(String section) {
        if (mIsLoaded) {
            return mDataMap.get(section);
        }
        throw new NotLoadedException();
    }

    public String getValue(String section, String key) {
        if(mIsLoaded) {
            HashMap<String, String> map = mDataMap.get(section);
            if(map != null) {
                return map.get(key);
            }
            return null;
        }
        throw new NotLoadedException();
    }

    public boolean containsSection(String section) {
        if(mIsLoaded) {
            return mDataMap.containsKey(section);
        }
        throw new NotLoadedException();
    }

    public boolean containsKey(String section, String key) {
        if(mIsLoaded) {
            HashMap<String, String> map = mDataMap.get(section);
            return map != null && map.containsKey(key);
        }
        throw new NotLoadedException();
    }

    private boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
}
