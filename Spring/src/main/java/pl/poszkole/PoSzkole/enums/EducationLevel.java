package pl.poszkole.PoSzkole.enums;

public enum EducationLevel {
    PRIMARY("Podstawowa"),
    JUNIOR_HIGH("Gimnazjum"),
    HIGH_SCHOOL("Średnia"),
    POST_SECONDARY("Policealna"),
    TERTIARY("Wyższa");

    String educationLevel;

    EducationLevel(String educationLevel) {}
}
