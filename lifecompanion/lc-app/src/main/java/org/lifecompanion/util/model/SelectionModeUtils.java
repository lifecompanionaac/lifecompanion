package org.lifecompanion.util.model;

import org.lifecompanion.model.api.configurationcomponent.*;
import org.lifecompanion.model.api.selectionmode.ComponentToScanI;
import org.lifecompanion.model.impl.selectionmode.ComponentToScan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SelectionModeUtils {
    /**
     * To check if a grid part is empty.<br>
     * Subclass that want to skip empty part should call this method to do the part checking on scanning generation.<br>
     * This will check is the part is empty only if the "skip empty element" parameter is on true.
     *
     * @param gridPartComponent the part to test
     * @return true if the part is considered as empty
     */
    public static boolean isPartConsideredEmptyForScanning(final GridPartComponentI gridPartComponent) {
        if (gridPartComponent instanceof GridPartKeyComponentI) {
            GridPartKeyComponentI key = (GridPartKeyComponentI) gridPartComponent;
            boolean empty = key.textContentProperty().get() == null || key.textContentProperty().get().isEmpty();
            empty &= key.imageVTwoProperty().get() == null;
            empty &= key.getActionManager().countAllActions() <= 0;
            empty |= key.keyOptionProperty().get().considerKeyEmptyProperty().get();
            return empty;
        }
        return gridPartComponent instanceof WriterDisplayerI;
    }

    public static List<ComponentToScanI> getRowColumnScanningComponents(final GridComponentI grid, boolean byPassEmptyCheck) {
        List<List<GridPartComponentI>> rows = new ArrayList<>();
        ComponentGridI compGrid = grid.getGrid();
        for (int row = 0; row < compGrid.getRow(); row++) {
            ArrayList<GridPartComponentI> rowComponents = new ArrayList<>();
            rows.add(rowComponents);
            for (int column = 0; column < compGrid.getColumn(); column++) {
                rowComponents.add(compGrid.getComponent(row, column));
            }
        }
        return generateComponentToScan(rows, byPassEmptyCheck);
    }

    public static List<ComponentToScanI> getColumnRowScanningComponents(final GridComponentI grid, boolean byPassEmptyCheck) {
        List<List<GridPartComponentI>> columns = new ArrayList<>();
        ComponentGridI compGrid = grid.getGrid();
        for (int column = 0; column < compGrid.getColumn(); column++) {
            ArrayList<GridPartComponentI> columnComponents = new ArrayList<>();
            columns.add(columnComponents);
            for (int row = 0; row < compGrid.getRow(); row++) {
                columnComponents.add(compGrid.getComponent(row, column));
            }
        }
        return generateComponentToScan(columns, byPassEmptyCheck);
    }

    /**
     * To generate a list of component to scan from a double list of part.
     *
     * @param components       the list of components (raw)
     * @param byPassEmptyCheck if this parameter is true, the empty check will not be done
     * @return the list of component to scan, generated with the needed parameters.
     */
    private static List<ComponentToScanI> generateComponentToScan(final List<List<GridPartComponentI>> components, final boolean byPassEmptyCheck) {
        HashSet<GridPartComponentI> scannedSet = new HashSet<>();
        ArrayList<ComponentToScanI> groupsToScan = new ArrayList<>();
        for (int i = 0; i < components.size(); i++) {
            List<GridPartComponentI> currentPart = components.get(i);
            ArrayList<GridPartComponentI> rowsComponents = new ArrayList<>();
            scannedSet.clear();//Unique just on the same line
            for (GridPartComponentI current : currentPart) {
                if ((byPassEmptyCheck || !isPartConsideredEmptyForScanning(current)) && !scannedSet.contains(current)) {
                    rowsComponents.add(current);
                    scannedSet.add(current);
                }
            }
            //Check if previous line contains exactly the same component
            if (!groupsToScan.isEmpty() && groupsToScan.get(groupsToScan.size() - 1).containsAllComponents(rowsComponents)) {
                ComponentToScanI previousColumn = groupsToScan.get(groupsToScan.size() - 1);
                previousColumn.increaseSpan();
            } else if (!rowsComponents.isEmpty()) {
                groupsToScan.add(new ComponentToScan(i, rowsComponents, 1));
            }
        }
        return groupsToScan;
    }

}
