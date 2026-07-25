package me.usainsrht.itemapi.yamlitem;

/**
 * Thrown when YAML item parsing fails.
 */
public final class YamlParseException extends RuntimeException {

    private final String path;

    public YamlParseException(String path, String message) {
        super(path == null || path.isEmpty() ? message : path + ": " + message);
        this.path = path == null ? "" : path;
    }

    public YamlParseException(String path, String message, Throwable cause) {
        super(path == null || path.isEmpty() ? message : path + ": " + message, cause);
        this.path = path == null ? "" : path;
    }

    public String path() {
        return path;
    }
}
