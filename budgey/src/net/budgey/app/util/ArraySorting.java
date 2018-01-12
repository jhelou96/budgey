package net.budgey.app.util;

import java.util.Comparator;

import net.budgey.app.models.Operation;

/**
 * Comparator used to sort arraylists of operations by date
 * From most recent to oldest
 */
public class ArraySorting implements Comparator<Operation> {
    @Override
    public int compare(Operation o1, Operation o2) {
           return o2.getDate().compareTo(o1.getDate());
   }
}
