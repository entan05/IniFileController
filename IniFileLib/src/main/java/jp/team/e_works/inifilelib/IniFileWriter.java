package jp.team.e_works.inifilelib;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IniFileWriter {
    public enum MODE {
        ADD,
        NEW
    }

    private ArrayList<IniItem> mItems;

    public IniFileWriter() {
        mItems = new ArrayList<>();
    }

    public void add(IniItem item) {
        mItems.add(item);
    }

    public void add(List<IniItem> items) {
        mItems.addAll(items);
    }

    public boolean write(String filePath) {
        return write(filePath, MODE.ADD);
    }

    public boolean write(String filePath, MODE mode) {
        return writeProcess(filePath, mode);
    }

    public boolean writeProcess(String filePath, MODE mode) {
        File file = new File(filePath);
        if(mode == MODE.ADD) {
            if(file.exists()) {
                IniFileLoader loader = new IniFileLoader();
                loader.load(filePath);
                mItems.addAll(loader.getAllDataList());

                if(!file.delete()) {
                    return false;
                }
            }
        }
        Collections.sort(mItems, new IniItemComparator());

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));

            String nowSection = null;
            boolean isFirstLine = true;
            for(IniItem item : mItems) {
                // セクション行
                if(nowSection == null && item.getSection() != null) {
                    if(!isFirstLine) {
                        bw.newLine();
                    }
                    bw.write("[" + item.getSection() + "]");
                    bw.newLine();

                    nowSection = item.getSection();
                } else if(nowSection != null && !nowSection.equals(item.getSection())) {
                    bw.newLine();
                    bw.write("[" + item.getSection() + "]");
                    bw.newLine();

                    nowSection = item.getSection();
                }

                // コメント行
                if(item.getComment() != null) {
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
