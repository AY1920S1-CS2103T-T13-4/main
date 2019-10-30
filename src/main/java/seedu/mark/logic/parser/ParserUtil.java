package seedu.mark.logic.parser;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import seedu.mark.commons.core.index.Index;
import seedu.mark.commons.util.StringUtil;
import seedu.mark.logic.commands.AnnotationCommand;
import seedu.mark.logic.parser.exceptions.ParseException;
import seedu.mark.model.annotation.ParagraphIdentifier;
import seedu.mark.model.bookmark.Folder;
import seedu.mark.model.bookmark.Name;
import seedu.mark.model.bookmark.Remark;
import seedu.mark.model.bookmark.Url;
import seedu.mark.model.reminder.Note;
import seedu.mark.model.tag.Tag;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index is not a non-zero unsigned integer.";
    public static final String MESSAGE_INVALID_BOOL = "Boolean options should be spelt either true or false.";

    private static final String DATE_FORMATTER = "dd/MM/yyyy HHmm";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws ParseException if the specified index is invalid (not non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String folder} into a {@code Folder}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code folder} is invalid.
     */
    public static Folder parseFolder(String folder) throws ParseException {
        requireNonNull(folder);
        String trimmedFolder = folder.trim();
        if (!Folder.isValidFolder(trimmedFolder)) {
            throw new ParseException(Folder.MESSAGE_CONSTRAINTS);
        }
        return new Folder(trimmedFolder);
    }

    /**
     * Parses a {@code String remark} into an {@code Remark}.
     * Leading and trailing whitespaces will be trimmed.
     * Empty remarks will be replaced by the default {@code Remark}.
     *
     * @throws ParseException if the given {@code remark} is invalid.
     */
    public static Remark parseRemark(String remark) throws ParseException {
        requireNonNull(remark);
        String trimmedRemark = remark.trim();

        if (Remark.isEmptyRemark(trimmedRemark)) {
            return Remark.getDefaultRemark();
        } else if (!Remark.isValidRemark(trimmedRemark)) {
            throw new ParseException(Remark.MESSAGE_CONSTRAINTS);
        }

        return new Remark(trimmedRemark);
    }

    /**
     * Parses a {@code String url} into a {@code Url}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code url} is invalid.
     */
    public static Url parseUrl(String url) throws ParseException {
        requireNonNull(url);
        String trimmedUrl = url.trim();
        if (!Url.isValidUrl(trimmedUrl)) {
            throw new ParseException(Url.MESSAGE_CONSTRAINTS);
        }
        return new Url(trimmedUrl);
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code tag} is invalid.
     */
    public static Tag parseTag(String tag) throws ParseException {
        requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (!Tag.isValidTagName(trimmedTag)) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Tag(trimmedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws ParseException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(parseTag(tagName));
        }
        return tagSet;
    }

    /**
     * Parses a {@code String note} into a {@code Note}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code note} is invalid.
     */
    public static Note parseNote(String note) throws ParseException {
        requireNonNull(note);
        String trimmedNote = note.trim();

        if (!Note.isValidNote(note)) {
            throw new ParseException(Note.MESSAGE_CONSTRAINTS);
        }
        return new Note(trimmedNote);
    }

    /**
     * Parses a {@code String time} into a {@code LocalDateTime}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code time} is invalid.
     */
    public static LocalDateTime parseTime(String time) throws ParseException {
        requireNonNull(time);
        String trimmedTime = time.trim();
        LocalDateTime getTime;

        try {
            getTime = LocalDateTime.parse(trimmedTime, formatter);
        } catch (DateTimeParseException e) {
            throw new ParseException("Invalid time format! Please use the following format: " + DATE_FORMATTER);
        }

        return getTime;
    }

    /**
     * Parses to get the formatted time from {@code time}.
     *
     * @param time the time to parse.
     * @return the formatted time.
     */
    public static String getFormattedTime(LocalDateTime time) {
        requireNonNull(time);
        String formatTime = time.format(formatter);
        return formatTime;
    }

    /**
     * Parses an offline paragraph identifier string {@code pidString} into a {@code ParagraphIdentifier}.
     * @param pidString String containing only the identifier
     * @return The {@code ParagraphIdentifier}
     * @throws ParseException if the given {@code pidString} is given in an incorrect format.
     */
    public static ParagraphIdentifier parseParagraphIdentifier(String pidString) throws ParseException {
        requireNonNull(pidString);
        if (pidString.trim().length() < 1) {
            throw new ParseException(AnnotationCommand.MESSAGE_CONSTRAINTS);
        }

        ParagraphIdentifier.ParagraphType t;
        switch (pidString.charAt(0)) {
        case 's':
            //fallthrough
        case 'S':
            t = ParagraphIdentifier.ParagraphType.STRAY;
            break;
        case 'p':
            //fallthrough
        case 'P':
            t = ParagraphIdentifier.ParagraphType.EXIST;
            break;
        default:
            throw new ParseException(AnnotationCommand.MESSAGE_CONSTRAINTS);
        }

        Index idx;
        try {
            idx = ParserUtil.parseIndex(pidString.substring(1));
        } catch (ParseException pe) {
            throw new ParseException(AnnotationCommand.MESSAGE_CONSTRAINTS);
        }

        return new ParagraphIdentifier(idx, t);
    }

    /**
     * Converts a {@code String boolStr}containing only "true" or "false" into true and false respectively.
     * @throws ParseException if {@code boolStr} is neither "true" nor "false" case-insensitively.
     */
    public static boolean strToBool(String boolStr) throws ParseException {
        switch (boolStr.toLowerCase()) {
        case "true":
            return true;
        case "false":
            return false;
        default:
            throw new ParseException(MESSAGE_INVALID_BOOL);
        }
    }

}
