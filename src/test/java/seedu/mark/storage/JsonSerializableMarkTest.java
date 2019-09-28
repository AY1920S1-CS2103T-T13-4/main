package seedu.mark.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.mark.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import seedu.mark.commons.exceptions.IllegalValueException;
import seedu.mark.commons.util.JsonUtil;
import seedu.mark.model.Mark;
import seedu.mark.testutil.TypicalBookmarks;

public class JsonSerializableMarkTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableMarkTest");
    private static final Path TYPICAL_BOOKMARKS_FILE = TEST_DATA_FOLDER.resolve("typicalBookmarksMark.json");
    private static final Path INVALID_BOOKMARK_FILE = TEST_DATA_FOLDER.resolve("invalidBookmarkMark.json");
    private static final Path DUPLICATE_BOOKMARK_FILE = TEST_DATA_FOLDER.resolve("duplicateBookmarkMark.json");

    @Test
    public void toModelType_typicalBookmarksFile_success() throws Exception {
        JsonSerializableMark dataFromFile = JsonUtil.readJsonFile(TYPICAL_BOOKMARKS_FILE,
                JsonSerializableMark.class).get();
        Mark markFromFile = dataFromFile.toModelType();
        Mark typicalBookmarksMark = TypicalBookmarks.getTypicalMark();
        assertEquals(markFromFile, typicalBookmarksMark);
    }

    @Test
    public void toModelType_invalidBookmarkFile_throwsIllegalValueException() throws Exception {
        JsonSerializableMark dataFromFile = JsonUtil.readJsonFile(INVALID_BOOKMARK_FILE,
                JsonSerializableMark.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicateBookmarks_throwsIllegalValueException() throws Exception {
        JsonSerializableMark dataFromFile = JsonUtil.readJsonFile(DUPLICATE_BOOKMARK_FILE,
                JsonSerializableMark.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableMark.MESSAGE_DUPLICATE_BOOKMARK,
                dataFromFile::toModelType);
    }

}
