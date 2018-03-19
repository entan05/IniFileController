package jp.team.e_works.inifilelib;

public class IniItem {
    private String section;
    private String key;
    private String value;

    public IniItem() {
        this(null, null, null);
    }

    public IniItem(String section, String key, String value) {
        this.section = section;
        this.key = key;
        this.value = value;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getSection() {
        return section;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[section: ");
        sb.append(this.section);
        sb.append(", key: ");
        sb.append(this.key);
        sb.append(", value: ");
        sb.append(this.value);
        sb.append("]");
        return sb.toString();
    }
}
