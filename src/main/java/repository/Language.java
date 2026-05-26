package repository;

public record Language(String key, String humanReadable) {
    @Override
    public String toString() {
        return humanReadable;
    }
}
