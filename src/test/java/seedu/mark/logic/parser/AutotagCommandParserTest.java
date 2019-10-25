package seedu.mark.logic.parser;

import static seedu.mark.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.mark.logic.parser.AutotagCommandParser.DEFAULT_PREDICATE;
import static seedu.mark.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.mark.logic.parser.CliSyntax.PREFIX_NOT_NAME;
import static seedu.mark.logic.parser.CliSyntax.PREFIX_NOT_URL;
import static seedu.mark.logic.parser.CliSyntax.PREFIX_URL;
import static seedu.mark.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.mark.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Arrays;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import seedu.mark.logic.commands.AutotagCommand;
import seedu.mark.model.autotag.SelectiveBookmarkTagger;
import seedu.mark.model.bookmark.Bookmark;
import seedu.mark.model.predicates.NameContainsKeywordsPredicate;
import seedu.mark.model.predicates.UrlContainsKeywordsPredicate;
import seedu.mark.model.tag.Tag;

public class AutotagCommandParserTest {

    private static final String VALID_TAG = "myTag";
    private static final String VALID_NAME_1 = "website";
    private static final String VALID_NAME_2 = "question";
    private static final String VALID_URL_1 = "facebook.com";

    private static final String NAME_DESC_1 = " " + PREFIX_NAME + VALID_NAME_1;
    private static final String NAME_DESC_2 = " " + PREFIX_NAME + VALID_NAME_2;
    private static final String NOT_NAME_DESC_1 = " " + PREFIX_NOT_NAME + VALID_NAME_1;
    private static final String URL_DESC_1 = " " + PREFIX_URL + VALID_URL_1;
    private static final String NOT_URL_DESC_1 = " " + PREFIX_NOT_URL + VALID_URL_1;

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, AutotagCommand.MESSAGE_USAGE);

    private AutotagCommandParser parser = new AutotagCommandParser();

    @Test
    public void parse_missingParts_failure() {
        // no tag name specified
        assertParseFailure(parser, NAME_DESC_1, MESSAGE_INVALID_FORMAT);

        // no condition specified
        assertParseFailure(parser, VALID_TAG, AutotagCommand.MESSAGE_NO_CONDITION_SPECIFIED);

        // no tag name and no condition specified
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid tag name
        assertParseFailure(parser, "multiple words not allowed" + NAME_DESC_1, Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "non_alphanumeric_characters" + NAME_DESC_1, Tag.MESSAGE_CONSTRAINTS);

        // TODO: Reject invalid names/URLs as conditions in AutotagCommandParser
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        String userInput = VALID_TAG + NAME_DESC_1 + URL_DESC_1;

        NameContainsKeywordsPredicate namePredicate = new NameContainsKeywordsPredicate(Arrays.asList(VALID_NAME_1));
        UrlContainsKeywordsPredicate urlPredicate = new UrlContainsKeywordsPredicate(Arrays.asList(VALID_URL_1));
        AutotagCommand expectedCommand = new AutotagCommand(new SelectiveBookmarkTagger(new Tag(VALID_TAG),
                DEFAULT_PREDICATE.and(namePredicate).and(urlPredicate)));

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_oneFieldSpecified_success() {
        // url
        String userInput = VALID_TAG + URL_DESC_1;
        Predicate<Bookmark> predicate = new UrlContainsKeywordsPredicate(Arrays.asList(VALID_URL_1));
        AutotagCommand expectedCommand = new AutotagCommand(
                new SelectiveBookmarkTagger(new Tag(VALID_TAG), DEFAULT_PREDICATE.and(predicate)));

        assertParseSuccess(parser, userInput, expectedCommand);

        // name
        userInput = VALID_TAG + NAME_DESC_1;
        predicate = new NameContainsKeywordsPredicate(Arrays.asList(VALID_NAME_1));
        expectedCommand = new AutotagCommand(
                new SelectiveBookmarkTagger(new Tag(VALID_TAG), DEFAULT_PREDICATE.and(predicate)));

        assertParseSuccess(parser, userInput, expectedCommand);

        // not name
        userInput = VALID_TAG + NOT_NAME_DESC_1;
        predicate = new NameContainsKeywordsPredicate(Arrays.asList(VALID_NAME_1)).negate();
        expectedCommand = new AutotagCommand(
                new SelectiveBookmarkTagger(new Tag(VALID_TAG), DEFAULT_PREDICATE.and(predicate)));

        assertParseSuccess(parser, userInput, expectedCommand);

        // not url
        userInput = VALID_TAG + NOT_URL_DESC_1;
        predicate = new UrlContainsKeywordsPredicate(Arrays.asList(VALID_URL_1)).negate();
        expectedCommand = new AutotagCommand(
                new SelectiveBookmarkTagger(new Tag(VALID_TAG), DEFAULT_PREDICATE.and(predicate)));

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_repeatedFields_success() {
        String userInput = VALID_TAG + NAME_DESC_1 + NAME_DESC_2;

        NameContainsKeywordsPredicate namePredicate =
                new NameContainsKeywordsPredicate(Arrays.asList(VALID_NAME_1, VALID_NAME_2));
        AutotagCommand expectedCommand = new AutotagCommand(
                new SelectiveBookmarkTagger(new Tag(VALID_TAG), DEFAULT_PREDICATE.and(namePredicate)));

        assertParseSuccess(parser, userInput, expectedCommand);
    }
}
