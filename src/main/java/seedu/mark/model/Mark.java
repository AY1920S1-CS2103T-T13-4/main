package seedu.mark.model;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.collections.ObservableList;

import seedu.mark.model.autotag.AutotagController;
import seedu.mark.model.autotag.BookmarkTagger;
import seedu.mark.model.autotag.SelectiveBookmarkTagger;
import seedu.mark.model.bookmark.Bookmark;
import seedu.mark.model.bookmark.Folder;
import seedu.mark.model.bookmark.UniqueBookmarkList;
import seedu.mark.model.folderstructure.FolderStructure;
import seedu.mark.model.reminder.Reminder;
import seedu.mark.model.reminder.ReminderAssociation;
import seedu.mark.model.tag.Tag;

/**
 * Wraps all data at the bookmark-manager level
 * Duplicates are not allowed (by .isSameBookmark comparison)
 */
public class Mark implements ReadOnlyMark {

    private final UniqueBookmarkList bookmarks;

    private final FolderStructure folderStructure;

    private final ReminderAssociation reminderAssociation;


    private final ObservableList<Reminder> reminders;

    private final AutotagController autotagController;


    public Mark() {
        bookmarks = new UniqueBookmarkList();
        folderStructure = new FolderStructure(Folder.ROOT_FOLDER, new ArrayList<>());
        reminderAssociation = new ReminderAssociation();

        reminders = reminderAssociation.getReminderList();

        autotagController = new AutotagController(new ArrayList<>());

    }

    /**
     * Creates an instance of Mark using the Bookmarks in the {@code toBeCopied}
     */
    public Mark(ReadOnlyMark toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the bookmark list with {@code bookmarks}.
     * {@code bookmarks} must not contain duplicate bookmarks.
     */
    public void setBookmarks(List<Bookmark> bookmarks) {
        this.bookmarks.setBookmarks(bookmarks);
    }

    /**
     * Resets the existing data of this {@code Mark} with {@code newData}.
     */
    public void resetData(ReadOnlyMark newData) {
        requireNonNull(newData);

        setBookmarks(newData.getBookmarkList());
        setFolderStructure(newData.getFolderStructure().clone());

        setReminderAssociation(newData.getReminderAssociation());

        setAutotagController(newData.getAutotagController());
    }

    //// bookmark-level operations

    /**
     * Returns true if a bookmark with the same identity as {@code bookmark} exists in Mark.
     */
    public boolean hasBookmark(Bookmark bookmark) {
        requireNonNull(bookmark);
        return bookmarks.contains(bookmark);
    }

    /**
     * Adds a bookmark to Mark.
     * The bookmark must not already exist in Mark.
     */
    public void addBookmark(Bookmark p) {
        bookmarks.add(p);
    }

    /**
     * Replaces the given bookmark {@code target} in the list with {@code editedBookmark}.
     * {@code target} must exist in Mark.
     * The bookmark identity of {@code editedBookmark} must not be the same as another
     * existing bookmark in Mark.
     */
    public void setBookmark(Bookmark target, Bookmark editedBookmark) {
        requireNonNull(editedBookmark);

        bookmarks.setBookmark(target, editedBookmark);
        reminderAssociation.editBookmark(target, editedBookmark);
        setReminders();
    }

    /**
     * Removes {@code key} from this {@code Mark} instance.
     * {@code key} must exist in Mark.
     */
    public void removeBookmark(Bookmark key) {
        bookmarks.remove(key);
        reminderAssociation.removeBookmark(key);
        setReminders();
    }

    //// folder operations

    /**
     * Replaces the folder structure with the specified {@code folderStructure}.
     */
    public void setFolderStructure(FolderStructure folderStructure) {
        this.folderStructure.getSubfolders().clear();
        this.folderStructure.getSubfolders().addAll(folderStructure.getSubfolders());
    }

    /**
     * Creates a new folder with name {@code folder} under {@code parentFolder}.
     * {@code folder} must not exist.
     * {@code parentFolder} must exist.
     */
    public void addFolder(Folder folder, Folder parentFolder) {
        this.folderStructure.addFolder(folder, parentFolder);
    }

    //// reminder operations

    /**
     * Replaces the association of reminder association with the specified {@code association}.
     *
     * @param reminderAssociation the specified association that is used.
     */
    public void setReminderAssociation(ReminderAssociation reminderAssociation) {
        this.reminderAssociation.setAssociation(reminderAssociation.getAssociation());
        reminders.clear();
        reminders.addAll(reminderAssociation.getReminderList());
    }

    /**
     * Gets a list of all reminders in time ascending order.
     *
     * @return a list of reminder in time ascending order.
     */
    public ObservableList<Reminder> getReminders() {
        return this.reminderAssociation.getReminderList();
    }

    /**
     * Adds a reminder that opens a specific bookmark.
     *
     * @param bookmark the bookmark that is opened by the reminder.
     * @param reminder the reminder to be added.
     */
    public void addReminder(Bookmark bookmark, Reminder reminder) {
        this.reminderAssociation.addReminder(bookmark, reminder);
        setReminders();
    }

    /**
     * Removes a specific reminder.
     *
     * @param reminder the reminder to be removed.
     */
    public void removeReminder(Reminder reminder) {
        this.reminderAssociation.deleteReminder(reminder);
        setReminders();
    }

    /**
     * Edits a specific reminder.
     *
     * @param targetReminder the reminder to be edited.
     * @param replaceReminder the edited reminder.
     */
    public void editReminder(Reminder targetReminder, Reminder replaceReminder) {
        this.reminderAssociation.setReminder(targetReminder, replaceReminder);
        setReminders();
    }

    public void setReminders() {
        reminders.clear();
        ObservableList<Reminder> newReminders = reminderAssociation.getReminderList();
        for (int i = 0; i < newReminders.size(); i++) {
            reminders.add(newReminders.get(i));
        }
    }

    //// autotag controller operations

    /**
     * Replaces the taggers in the current {@code AutotagController} with
     * taggers from {@code autotagController}.
     */
    public void setAutotagController(AutotagController autotagController) {
        requireNonNull(autotagController);

        this.autotagController.removeAllTaggers();
        autotagController.getTaggers().forEach(this.autotagController::addTagger);
    }

    /**
     * Checks whether Mark contains the given tagger.
     */
    public boolean hasTagger(SelectiveBookmarkTagger tagger) {
        return autotagController.hasTagger(tagger);
    }

    /**
     * Adds a {@link SelectiveBookmarkTagger} to Mark.
     *
     * @param tagger {@link SelectiveBookmarkTagger} to be added.
     */
    public void addTagger(SelectiveBookmarkTagger tagger) {
        autotagController.addTagger(tagger);
    }

    /**
     * Removes the given tagger from Mark.
     *
     * @param taggerName name of the tagger to be removed.
     * @return false if the tagger is not found.
     */
    public boolean removeTagger(String taggerName) {
        return autotagController.removeTagger(taggerName);
    }

    /**
     * Applies all {@link SelectiveBookmarkTagger}s to the bookmarks in Mark.
     */
    public void applyAllTaggers() {
        setBookmarks(autotagController.applyTaggersToList(getBookmarkList()));
    }


    //// favorite operations

    /**
     * Tags the specified bookmark as a favorite bookmark.
     *
     * @param bookmark the bookmark to be added to the favorites
     */
    public void favoriteBookmark(Bookmark bookmark) {
        BookmarkTagger favoriteTagger = new BookmarkTagger(new Tag("Favorite"));
        setBookmark(bookmark, favoriteTagger.applyTag(bookmark));
    }

    //// util methods

    @Override
    public String toString() {
        return bookmarks.asUnmodifiableObservableList().size() + " bookmarks";
        // TODO: refine later
    }

    @Override
    public ObservableList<Bookmark> getBookmarkList() {
        return bookmarks.asUnmodifiableObservableList();
    }

    @Override
    public FolderStructure getFolderStructure() {
        return folderStructure;
    }

    @Override
    public ReminderAssociation getReminderAssociation() {
        return reminderAssociation;
    }

    @Override
    public ObservableList<Reminder> getReminderList() {
        return reminders;
    }

    @Override
    public AutotagController getAutotagController() {
        return autotagController;
    }


    public boolean hasFolder(Folder folder) {
        return getFolderStructure().hasFolder(folder);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Mark // instanceof handles nulls
                && bookmarks.equals(((Mark) other).bookmarks)
                && folderStructure.equals(((Mark) other).folderStructure));
    }

    /**
     * Checks if the bookmark already has reminder.
     *
     * @param bookmark the bookmark to check.
     * @return whether the bookmark already has a reminder.
     */
    public boolean isBookmarkHasReminder(Bookmark bookmark) {
        return reminderAssociation.isBookmarkHasReminder(bookmark);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookmarks, folderStructure);
    }
}
