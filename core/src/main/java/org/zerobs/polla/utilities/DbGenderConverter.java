package org.zerobs.polla.utilities;

import org.zerobs.polla.entities.Gender;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.EnumAttributeConverter;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class DbGenderConverter implements AttributeConverter<Gender> {
    private static final EnumAttributeConverter<Gender> converter = EnumAttributeConverter.create(Gender.class);

    @Override
    public AttributeValue transformFrom(Gender input) {
        return converter.transformFrom(input);
    }

    @Override
    public Gender transformTo(AttributeValue input) {
        return converter.transformTo(input);
    }

    @Override
    public EnhancedType<Gender> type() {
        return converter.type();
    }

    @Override
    public AttributeValueType attributeValueType() {
        return converter.attributeValueType();
    }
}
