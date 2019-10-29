package seedu.mark.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import javafx.collections.ObservableList;
import seedu.mark.commons.exceptions.IllegalValueException;
import seedu.mark.model.Mark;
import seedu.mark.model.ReadOnlyMark;
import seedu.mark.model.bookmark.Bookmark;
import seedu.mark.model.bookmark.Folder;
import seedu.mark.model.bookmark.Url;
import seedu.mark.model.folderstructure.FolderStructure;
import seedu.mark.model.reminder.Reminder;
import seedu.mark.model.reminder.ReminderAssociation;

/**
 * An Immutable Mark that is serializable to JSON format.
 */
@JsonRootName(value = "mark")
class JsonSerializableMark {

    public static final String MESSAGE_DUPLICATE_BOOKMARK = "Bookmark list contains duplicate bookmark(s).";
    public static final String MESSAGE_DUPLICATE_FOLDER = "There are duplicate folder(s).";
    public static final String MESSAGE_NO_ROOT_FOLDER = "The root folder is missing.";
    public static final String MESSAGE_DUPLICATE_OR_NOT_EXIST_REMINDER =
            "Cannot find the bookmark for reminder or bookmark has duplicate reminder.";

    private final List<JsonAdaptedBookmark> bookmarks = new ArrayList<>();
    private final JsonAdaptedFolderStructure folderStructure;
    private final List<JsonAdaptedReminder> reminders = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableMark} with the given bookmarks.
     */
    @JsonCreator
    public JsonSerializableMark(@JsonProperty("bookmarks") List<JsonAdaptedBookmark> bookmarks,
        @JsonProperty("folderStructure") JsonAdaptedFolderStructure folderStructure,
                                @JsonProperty("reminders") List<JsonAdaptedReminder> reminders) {
        this.bookmarks.addAll(bookmarks);
        this.folderStructure = folderStructure;
        this.reminders.addAll(reminders);
    }

    /**
     * Converts a given {@code ReadOnlyMark} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code
     *               JsonSerializableMark}.
     */
    public JsonSerializableMark(ReadOnlyMark source) {
        bookmarks.addAll(source.getBookmarkList().stream().map(JsonAdaptedBookmark::new)
            .collect(Collectors.toList()));
        folderStructure = new JsonAdaptedFolderStructure(source.getFolderStructure());
        reminders.addAll(source.getReminderList().stream()
                .map(JsonAdaptedReminder::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this Mark into the model's {@code Mark} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public Mark toModelType() throws IllegalValueException {
        Mark mark = new Mark();
        for (JsonAdaptedBookmark jsonAdaptedBookmark : bookmarks) {
            Bookmark bookmark = jsonAdaptedBookmark.toModelType();
            if (mark.hasBookmark(bookmark)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_BOOKMARK);
            }
            mark.addBookmark(bookmark);
        }
        FolderStructure modelFolderStructure = folderStructure.toModelType();
        if (!modelFolderStructure.getFolder().equals(Folder.ROOT_FOLDER)) {
            throw new IllegalValueException(MESSAGE_NO_ROOT_FOLDER);
        }
        if (!FolderStructure.isValidFolderStructure(modelFolderStructure)) {
            throw new IllegalValueException(MESSAGE_DUPLICATE_FOLDER);
        }
        mark.setFolderStructure(modelFolderStructure);

        List<Reminder> reminderList = new ArrayList<>();
        for (JsonAdaptedReminder jsonAdaptedReminder : reminders) {
            Reminder reminder = jsonAdaptedReminder.toModelType();
            reminderList.add(reminder);
        }

        setReminderAssociation(mark, reminderList);
        return mark;
    }

    private void setReminderAssociation(Mark mark, List<Reminder> reminders) throws IllegalValueException {
        ObservableList<Bookmark> bookmarks = mark.getBookmarkList();
        HashMap<Url, Bookmark> bookmarkHashMap = new HashMap<>();
        ReminderAssociation reminderAssociation = new ReminderAssociation();
        for (int i = 0; i < bookmarks.size(); i++) {
            Bookmark bookmark = bookmarks.get(i);
            Url url = bookmark.getUrl();

            bookmarkHashMap.put(url, bookmark);
        }

        for (int i = 0; i < reminders.size(); i++) {
            Reminder reminder = reminders.get(i);
            Url url = reminder.getUrl();
            if (!bookmarkHashMap.containsKey(url)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_OR_NOT_EXIST_REMINDER);
            }

            Bookmark bookmark = bookmarkHashMap.get(url);
            reminderAssociation.addReminder(bookmark, reminder);
            bookmarkHashMap.remove(url);
        }

        mark.setReminderAssociation(reminderAssociation);
    }

}
