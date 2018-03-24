package jp.team.e_works.inifilelib;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * IniFileに書き込むライター
 */
public class IniFileWriter {
    /**
     * 書き込みモード
     */
    public enum MODE {
        /**
         * 追加
         */
        ADD,
        /**
         * 新規<br>
         * 既にファイルが存在する場合、上書きする
         */
        NEW
    }

    // データリスト
    private ArrayList<IniItem> mItems;

    /**
     * コンストラクタ
     */
    public IniFileWriter() {
        mItems = new ArrayList<>();
    }

    /**
     * 書き込むデータを追加する
     *
     * @param item データ
     */
    public void add(IniItem item) {
        mItems.add(item);
    }

    /**
     * 書き込むデータを追加する
     *
     * @param items データ
     */
    public void add(List<IniItem> items) {
        mItems.addAll(items);
    }

    /**
     * データを新規モードで書き込む
     *
     * @param filePath ファイルパス
     * @return 書き込み成否
     */
    public boolean write(String filePath) {
        return write(filePath, MODE.NEW);
    }

    /**
     * データを書き込む
     *
     * @param filePath ファイルパス
     * @param mode     書き込みモード
     * @return 書き込み成否
     */
    public boolean write(String filePath, MODE mode) {
        return writeProcess(filePath, mode);
    }

    /**
     * 書き込み処理
     *
     * @param filePath ファイルパス
     * @param mode     書き込みモード
     * @return 書き込み成否
     */
    private boolean writeProcess(String filePath, MODE mode) {
        File file = new File(filePath);
        if (mode == MODE.ADD) {
            if (file.exists()) {
                IniFileLoader loader = new IniFileLoader();
                loader.load(filePath);
                mItems.addAll(loader.getAllDataList());

                if (!file.delete()) {
                    return false;
                }
            }
        } else if (mode == MODE.NEW) {
            if (file.exists()) {
                if (!file.delete()) {
                    return false;
                }
            }
        }
        Collections.sort(mItems, new IniItemComparator());

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));

            String nowSection = null;
            boolean isFirstLine = true;
            for (IniItem item : mItems) {
                // セクション行
                if (nowSection == null && item.getSection() != null) {
                    if (!isFirstLine) {
                        bw.newLine();
                    }
                    bw.write("[" + item.getSection() + "]");
                    bw.newLine();

                    nowSection = item.getSection();
                } else if (nowSection != null && !nowSection.equals(item.getSection())) {
                    bw.newLine();
                    bw.write("[" + item.getSection() + "]");
                    bw.newLine();

                    nowSection = item.getSection();
                }

                // コメント行
                if (item.getComment() != null) {
                    bw.write("# " + item.getComment());
                    bw.newLine();
                }
                // key & value
                bw.write(item.getKey() + "=" + item.getValue());
                bw.newLine();

                isFirstLine = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
