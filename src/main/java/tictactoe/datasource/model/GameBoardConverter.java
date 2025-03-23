package tictactoe.datasource.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.stream.Collectors;

@Converter
public class GameBoardConverter implements AttributeConverter<int[][], String> {

    /**
     * Преобразование матрицы в строку
     * @param board Двумерный целочисленный массив игрового поля
     * @return Строка в базе данных
     */
    @Override
    public String convertToDatabaseColumn(int[][] board) {
        if (board == null)
            return null;
        return Arrays.stream(board)
                .map(row -> "{" + Arrays.stream(row)
                        .mapToObj(String::valueOf)
                        .collect(Collectors.joining(",")) + "}")
                .collect(Collectors.joining(","));
    }

    /**
     * Преобразование строки обратно в матрицу
     * @param dbData Строка из базы данных
     * @return Двумерный целочисленный массив игрового поля
     */
    @Override
    public int[][] convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return new int[0][0];
        }
        return Arrays.stream(dbData.split("},\\{"))
                .map(row -> row.replace("{", "").replace("}", ""))
                .map(row -> Arrays.stream(row.split(","))
                        .mapToInt(Integer::parseInt)
                        .toArray())
                .toArray(int[][]::new);
    }
}


