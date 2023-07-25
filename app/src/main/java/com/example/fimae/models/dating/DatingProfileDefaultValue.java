package com.example.fimae.models.dating;

public class DatingProfileDefaultValue {
    private DatingProfileDefaultValue() {
    }
    public static final int minAge = 18;
    public static final int maxAge = 60;
    public static final int minHeight = 120;
    public static final int maxHeight = 200;
    public static final int distance = 1000;
    public static final GenderOptions gender = GenderOptions.UNIMPORTANT;
    public static final RelationshipType relationshipType = RelationshipType.UNIMPORTANT;
    public static final EducationalLevel educationalLevel = EducationalLevel.UNIMPORTANT;
}
