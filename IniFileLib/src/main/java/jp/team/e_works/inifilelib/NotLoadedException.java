package jp.team.e_works.inifilelib;

public class NotLoadedException extends RuntimeException {
    public NotLoadedException() {
        super("ini file isn't loaded.");
    }
}
