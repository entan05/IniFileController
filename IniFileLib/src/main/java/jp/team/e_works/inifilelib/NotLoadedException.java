package jp.team.e_works.inifilelib;

/**
 * iniファイル未ロード時例外
 */
public class NotLoadedException extends RuntimeException {
    public NotLoadedException() {
        super("ini file isn't loaded.");
    }
}
