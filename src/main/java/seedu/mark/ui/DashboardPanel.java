package seedu.mark.ui;

import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import seedu.mark.commons.core.LogsCenter;
import seedu.mark.logic.Logic;

/**
 * The Dashboard panel of Mark.
 */
public class DashboardPanel extends UiPart<Region> {

    private static final String FXML = "DashboardPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(getClass());

    @FXML
    private StackPane folderStructurePlaceholder;
    @FXML
    private StackPane reminderListPlaceholder;

    public DashboardPanel(Logic logic) {
        super(FXML);
        FolderStructureTreeView folderStructureTreeView = new FolderStructureTreeView(
                logic.getFolderStructure(), logic.getFilteredBookmarkList());
        ReminderListPanel reminderListPanel = new ReminderListPanel(logic.getReminderList());
        folderStructurePlaceholder.getChildren().add(folderStructureTreeView.getRoot());
        reminderListPlaceholder.getChildren().add(reminderListPanel.getRoot());
    }
}
