package works.quiet.cli.command;

final class ExitCode {
    private int code;

    public ExitCode(int code) {
        this.code = code;
    }

    public void set(int i) {
        code = i;
    }

    public int get() {
        return code;
    }
}
