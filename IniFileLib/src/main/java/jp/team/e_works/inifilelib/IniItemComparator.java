package jp.team.e_works.inifilelib;

import java.util.Comparator;

public class IniItemComparator implements Comparator<IniItem> {
    @Override
    public int compare(IniItem i1, IniItem i2) {
        String i1Section = i1.getSection();
        if (i1Section == null) {
            // sectionがnull(グローバルセクション)同士
            if (i2.getSection() == null) {
                // keyの順序で判定する
                String i1Key = i1.getKey();
                if (i1Key != null) {
                    return i1Key.compareToIgnoreCase(i2.getKey());
                } else {
                    return -1;
                }
            }
            // i2がグローバルセクションでない場合はi1が小
            else {
                return -1;
            }
        } else {
            if (i2.getSection() == null) {
                return 1;
            } else {
                String i1Key = i1.getKey();
                if (i1Key != null) {
                    return i1Key.compareToIgnoreCase(i2.getKey());
                } else {
                    return -1;
                }
            }
        }
    }
}
