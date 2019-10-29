package seedu.mark.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.mark.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.mark.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.mark.logic.parser.CliSyntax.PREFIX_NOT_NAME;
import static seedu.mark.logic.parser.CliSyntax.PREFIX_NOT_URL;
import static seedu.mark.logic.parser.CliSyntax.PREFIX_URL;

import seedu.mark.logic.commands.AutotagCommand;
import seedu.mark.logic.parser.exceptions.ParseException;
import seedu.mark.model.autotag.SelectiveBookmarkTagger;
import seedu.mark.model.predicates.BookmarkPredicate;
import seedu.mark.model.tag.Tag;

/**
 * Parses input arguments and creates a new AutotagCommand object
 */
public class AutotagCommandParser implements Parser<AutotagCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AutotagCommand
     * and returns an AutotagCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public AutotagCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_URL, PREFIX_NOT_NAME, PREFIX_NOT_URL);

        if (argMultimap.getPreamble().isEmpty() || argMultimap.getPreamble().split(" ").length > 1) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AutotagCommand.MESSAGE_USAGE));
        }

        Tag tagToApply = ParserUtil.parseTag(argMultimap.getPreamble());

        BookmarkPredicate predicate = new BookmarkPredicate();

        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            predicate = predicate.withNameKeywords(argMultimap.getAllValues(PREFIX_NAME));
        }
        if (argMultimap.getValue(PREFIX_URL).isPresent()) {
            predicate = predicate.withUrlKeywords(argMultimap.getAllValues(PREFIX_URL));
        }
        if (argMultimap.getValue(PREFIX_NOT_NAME).isPresent()) {
            predicate = predicate.withoutNameKeywords(argMultimap.getAllValues(PREFIX_NOT_NAME));
        }
        if (argMultimap.getValue(PREFIX_NOT_URL).isPresent()) {
            predicate = predicate.withoutUrlKeywords(argMultimap.getAllValues(PREFIX_NOT_URL));
        }

        if (predicate.isEmpty()) {
            throw new ParseException(AutotagCommand.MESSAGE_NO_CONDITION_SPECIFIED);
        }

        SelectiveBookmarkTagger tagger = new SelectiveBookmarkTagger(tagToApply, predicate);

        return new AutotagCommand(tagger);
    }

}
