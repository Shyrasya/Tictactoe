package tictactoe.datasource.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Конвертер для преобразования набора ролей {@link Role} в строковое представление и обратно.
 * <p>
 * Используется для сохранения и извлечения значений ролей в базе данных в виде строки,
 * где роли разделены запятыми.
 * </p>
 */
@Converter
public class RoleConverter implements AttributeConverter<Set<Role>, String> {

    /**
     * Преобразует набор ролей в строку для сохранения в базе данных.
     *
     * @param roles Набор ролей {@link Role} для сохранения.
     * @return Строка, содержащая имена ролей, разделённые запятыми, или {@code null}, если набор пустой или {@code null}.
     */
    @Override
    public String convertToDatabaseColumn(Set<Role> roles) {
        if (roles == null || roles.isEmpty())
            return null;
        return roles.stream()
                .map(Enum::name)
                .collect(Collectors.joining(","));
    }

    /**
     * Преобразует строковое представление ролей из базы данных в набор ролей.
     *
     * @param dbData Строка с именами ролей, разделёнными запятыми.
     * @return Набор ролей {@link Role} или {@code null}, если входные данные {@code null} или пусты.
     */
    @Override
    public Set<Role> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty())
            return null;
        return Arrays.stream(dbData.split(","))
                .map(Role::valueOf)
                .collect(Collectors.toSet());
    }
}
