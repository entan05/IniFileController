package jp.team.e_works.inifilelib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class IniFileLoader {
    // HashMap<section, HashMap<key, value>>とする
    private HashMap<String, HashMap<String, String>> mDataMap;

    // ファイルパス
    private String mFilePath;

    // load完了しているか
    private boolean mIsLoaded = false;


    public IniFileLoader() {
        mDataMap = new HashMap<>();
    }

    /**
     * 指定したファイルをUTF-8のiniファイルとしてロードする
     *
     * @param filePath ファイルパス
     * @return ロードに成功したかどうか
     */
    public boolean load(String filePath) {
        mFilePath = filePath;
        return loadProcess(filePath);
    }

    /**
     * 前回ロードを試みたファイルをもう一度ロードする
     *
     * @return ロードに成功したかどうか
     */
    public boolean reload() {
        return isEmpty(mFilePath) && loadProcess(mFilePath);
    }

    private boolean loadProcess(String filePath) {
        try {
            FileReader fileReader = new FileReader(new File(filePath));
            BufferedReader br = new BufferedReader(fileReader);
            String line = br.readLine();

            // セクション名
            String section = null;
            // コメント
            String comment = null;
            // キーバリュー
            HashMap<String, String> map = new HashMap<>();
            while (line != null) {
                // BOM取り除き処理
                // todo

                // 行頭、行末の空白を取り除く
                line = line.trim();

                // 空行
                if (isEmpty(line)) {
                    // no process
                    line = br.readLine();
                    continue;
                }
                // コメント行
                if (line.charAt(0) == '#') {
                    String commentLine = line.substring(1).trim();
                    if (isEmpty(comment)) {
                        comment = commentLine;
                    } else {
                        comment += "\n";
                        comment += commentLine;
                    }
                    line = br.readLine();
                    continue;
                }
                // セクション行
                if (line.charAt(0) == '[' && line.charAt(line.length() - 1) == ']') {
                    section = line.substring(1, line.length() - 1);
                    map = new HashMap<>();
                    line = br.readLine();
                    continue;
                }
                // パラメータ行
                if (line.length() >= 3 && line.contains("=") && line.length() > line.indexOf("=") + 1) {
                    String key = line.substring(0, line.indexOf("=") - 1);
                    String value = line.substring(line.indexOf("=") + 1);

                    map.put(key, value);
                    mDataMap.put(section, map);
                }

                line = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            mIsLoaded = false;
            return false;
        }
        mIsLoaded = true;
        return true;
    }

    /**
     * 読み込んだ結果を (section, (key, value)) の {@link HashMap} で返す
     *
     * @return 読み込んだ結果の {@link HashMap} 、読み込んだ項目がない場合 {@code null} を返す
     * @throws NotLoadedException ロード処理({@link #load(String)} )を行っていない場合、
     *                            もしくはIniファイルのロードに成功していない場合にthrowする
     */
    public HashMap<String, HashMap<String, String>> getAllDataMap() {
        if (mIsLoaded) {
            return mDataMap;
        }
        throw new NotLoadedException();
    }

    /**
     * 読み込んだ結果を {@link IniItem} の {@link List} で返す
     *
     * @return 読み込んだ結果の {@link List} 、読み込んだ項目がない場合 {@code null} を返す
     * @throws NotLoadedException ロード処理({@link #load(String)} )を行っていない場合、
     *                            もしくはIniファイルのロードに成功していない場合にthrowする
     */
    public List<IniItem> getAllDataList() {
        if (mIsLoaded) {
            ArrayList<IniItem> list = new ArrayList<>();
            for (String section : mDataMap.keySet()) {
                HashMap<String, String> map = mDataMap.get(section);
                for (String key : map.keySet()) {
                    list.add(new IniItem(section, key, map.get(key)));
                }
            }
            if (list.size() <= 0) {
                list = null;
            } else {
                Collections.sort(list, new IniItemComparator());
            }
            return list;
        }
        throw new NotLoadedException();
    }

    /**
     * 読み込んだ結果の指定したセクションを(key, value)の {@link HashMap} で返す
     *
     * @param section セクション指定
     * @return 読み込んだ結果の指定したセクション部、読み込んだ項目がない場合 {@code null} を返す
     * @throws NotLoadedException ロード処理({@link #load(String)} )を行っていない場合、
     *                            もしくはIniファイルのロードに成功していない場合にthrowする
     */
    public HashMap<String, String> getSectionDataMap(String section) {
        if (mIsLoaded) {
            return mDataMap.get(section);
        }
        throw new NotLoadedException();
    }

    /**
     * 読み込んだ結果の指定したセクションを {@link IniItem} の {@link List} で返す
     *
     * @param section セクション指定
     * @return 読み込んだ結果の指定したセクション部、読み込んだ項目がない場合 {@code null} を返す
     * @throws NotLoadedException ロード処理({@link #load(String)} )を行っていない場合、
     *                            もしくはIniファイルのロードに成功していない場合にthrowする
     */
    public List<IniItem> getSectionDataList(String section) {
        if (mIsLoaded) {
            ArrayList<IniItem> list = null;
            HashMap<String, String> map = mDataMap.get(section);
            if (map != null) {
                list = new ArrayList<>();
                for (String key : map.keySet()) {
                    list.add(new IniItem(section, key, map.get(key)));
                }
            }
            if (list != null) {
                Collections.sort(list, new IniItemComparator());
            }
            return list;
        }
        throw new NotLoadedException();
    }

    /**
     * 指定したセクション、指定したキーの値を返す
     *
     * @param section セクション指定
     * @param key     キー指定
     * @return 指定したセクション、指定したキーの値
     * @throws NotLoadedException ロード処理({@link #load(String)} )を行っていない場合、
     *                            もしくはIniファイルのロードに成功していない場合にthrowする
     */
    public String getValue(String section, String key) {
        if (mIsLoaded) {
            HashMap<String, String> map = mDataMap.get(section);
            if (map != null) {
                return map.get(key);
            }
            return null;
        }
        throw new NotLoadedException();
    }

    /**
     * 指定したセクションが読み込んだ結果にあるかを返す
     *
     * @param section セクション指定
     * @return 存在する場合は {@code true}
     */
    public boolean containsSection(String section) {
        if (mIsLoaded) {
            return mDataMap.containsKey(section);
        }
        throw new NotLoadedException();
    }

    /**
     * 指定したセクションの、指定したキーが読み込んだ結果にあるかを返す
     *
     * @param section セクション指定
     * @param key     キー指定
     * @return 存在する場合は {@code true}
     */
    public boolean containsKey(String section, String key) {
        if (mIsLoaded) {
            HashMap<String, String> map = mDataMap.get(section);
            return map != null && map.containsKey(key);
        }
        throw new NotLoadedException();
    }

    /**
     * {@code String} が空か判定する
     *
     * @param str 判定対象
     * @return {@code String} が空ならば {@code true}
     */
    private boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
}
