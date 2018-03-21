package jp.team.e_works.inifilelib;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
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
        return write(filePath, Charset.defaultCharset(), MODE.ADD);
    }

    public boolean write(String filePath, Charset cs) {
        return write(filePath, cs, MODE.ADD);
    }

    public boolean write(String filePath, MODE mode) {
        return write(filePath, Charset.defaultCharset(), mode);
    }

    public boolean write(String filePath, Charset cs, MODE mode) {
        if (mode == MODE.ADD) {
            if (Files.exists(Paths.get(filePath))) {
                IniFileLoader loader = new IniFileLoader();
                loader.load(filePath);
                mItems.addAll(loader.getAllDataList());

                try {
                    Files.delete(Paths.get(filePath));
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        Collections.sort(mItems, new IniItemComparator());

        StringBuilder sb = new StringBuilder();
        String nowSecttion = null;
        for (IniItem item : mItems) {
            if (nowSecttion == null && item.getSection() != null) {
                if (sb.length() != 0) {
                    sb.append("\n");
                }
                sb.append("[");
                sb.append(item.getSection());
                sb.append("]\n");

                nowSecttion = item.getSection();
            } else if (nowSecttion != null && !nowSecttion.equals(item.getSection())) {
                sb.append("\n[");
                sb.append(item.getSection());
                sb.append("]\n");

                nowSecttion = item.getSection();
            }

            // コメント行
            if (item.getComment() != null) {
                sb.append("# ");
                sb.append(item.getComment());
                sb.append("\n");
            }
            // key
            sb.append(item.getKey());
            sb.append("=");
            // value
            sb.append(item.getValue());
            sb.append("\n");
        }

        try {
            Files.createFile(Paths.get(filePath));
            Files.write(Paths.get(filePath), Arrays.asList(sb.toString()), cs, StandardOpenOption.WRITE);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
