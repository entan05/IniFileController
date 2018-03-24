package jp.team.e_works.inifilelib;

/**
 * Iniファイルの1データ
 */
public class IniItem {
    private String section;
    private String key;
    private String value;
    private String comment;

    /**
     * コンストラクタ<br>
     * 空のデータを作成する
     */
    public IniItem() {
        this(null, null, null, null);
    }

    /**
     * コンストラクタ<br>
     * コメントが空のデータを作成する
     *
     * @param section セクション
     * @param key     キー
     * @param value   値
     */
    public IniItem(String section, String key, String value) {
        this(section, key, value, null);
    }

    /**
     * コンストラクタ
     *
     * @param section セクション
     * @param key     キー
     * @param value   値
     * @param comment コメント
     */
    public IniItem(String section, String key, String value, String comment) {
        this.section = section;
        this.key = key;
        this.value = value;
        this.comment = comment;
    }

    /**
     * セクションを設定する
     *
     * @param section セクション、グローバルセクションを設定する際は {@code null} を設定する
     */
    public void setSection(String section) {
        this.section = section;
    }

    /**
     * セクションを取得する
     *
     * @return セクション
     */
    public String getSection() {
        return section;
    }

    /**
     * キーを設定する
     *
     * @param key キー
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * キーを取得する
     *
     * @return キー
     */
    public String getKey() {
        return key;
    }

    /**
     * 値を設定する
     *
     * @param value 値
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * 値を取得する
     *
     * @return 値
     */
    public String getValue() {
        return value;
    }

    /**
     * コメントを設定する
     *
     * @param comment コメント
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * コメントを取得する
     *
     * @return コメント
     */
    public String getComment() {
        return comment;
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
        sb.append(", comment: ");
        sb.append(this.comment);
        sb.append("]");
        return sb.toString();
    }
}
