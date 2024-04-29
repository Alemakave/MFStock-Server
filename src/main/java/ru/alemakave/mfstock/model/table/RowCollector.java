package ru.alemakave.mfstock.model.table;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static org.apache.poi.ss.usermodel.CellType.NUMERIC;

@Slf4j
public class RowCollector<T, A, R> implements Collector<T, A, R> {
    private static final Set<Collector.Characteristics> CH_ID
            = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH));

    private final Supplier<A> supplier;
    private final BiConsumer<A, T> accumulator;
    private final BinaryOperator<A> combiner;
    private final Function<A, R> finisher;
    private final Set<Characteristics> characteristics;

    private RowCollector(Supplier<A> supplier, BiConsumer<A, T> accumulator, BinaryOperator<A> combiner, Function<A,R> finisher, Set<Characteristics> characteristics) {
        this.supplier = supplier;
        this.accumulator = accumulator;
        this.combiner = combiner;
        this.finisher = finisher;
        this.characteristics = characteristics;
    }

    private RowCollector(Supplier<A> supplier, BiConsumer<A, T> accumulator, BinaryOperator<A> combiner, Set<Characteristics> characteristics) {
        this(supplier, accumulator, combiner, castingIdentity(), characteristics);
    }

    @Override
    public BiConsumer<A, T> accumulator() {
        return accumulator;
    }

    @Override
    public Supplier<A> supplier() {
        return supplier;
    }

    @Override
    public BinaryOperator<A> combiner() {
        return combiner;
    }

    @Override
    public Function<A, R> finisher() {
        return finisher;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return characteristics;
    }

    @SuppressWarnings("unchecked")
    private static <I, R> Function<I, R> castingIdentity() {
        return i -> (R) i;
    }

    public static <T extends Row, R> RowCollector<T, ?, List<R>> instance() {
        return new RowCollector<>((Supplier<List<TableRow>>) ArrayList::new, (tableRows, t) -> {
            TableRow row = new TableRow();
            for (Cell cell : t) {
                if (cell.getCellType() == NUMERIC
                        && !DateUtil.isCellDateFormatted(cell)) {
                    DecimalFormat df = new DecimalFormat("#.##");
                    row.addCell(new TableCell(df.format(cell.getNumericCellValue()), NUMERIC));
                } else {
                    row.addCell(new TableCell(cell.toString(), cell.getCellType()));
                }
            }
            tableRows.add(row);
        },
                (left, right) -> {
                    if (left.size() < right.size()) {
                        right.addAll(left); return right;
                    } else {
                        left.addAll(right); return left;
                    }
                },
                CH_ID);
    }
}
