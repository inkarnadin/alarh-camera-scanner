package scanner.stat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enumeration kind of error and statistic strings for screen saving.
 *
 * @author inkarnadin
 */
@Getter
@RequiredArgsConstructor
public enum ScreenStatItem {

    ALL { @Override public String toString() { return "All"; } },
    SUCCESS { @Override public String toString() { return "Success"; } },
    FAILURE { @Override public String toString() { return "Failure"; } },
    INVALID_DATA_FOUND { @Override public String toString() { return "Invalid data found"; } },
    WRONG_AUTH_ERROR { @Override public String toString() { return "Wrong auth error"; } },
    NOT_FOUND_CODEC_ERROR { @Override public String toString() { return "Not found codec error"; } },
    STREAM_NOT_FOUND { @Override public String toString() { return "Stream not found"; } },
    BAD_REQUEST { @Override public String toString() { return "Bad request"; } },
    PROTOCOL_NOT_SUPPORTED { @Override public String toString() { return "Protocol not supported"; } },
    PARAMETER_NOT_UNDERSTOOD { @Override public String toString() { return "Parameter not understood"; } },
    UNEXPECTED_ERROR { @Override public String toString() { return "Unexpected error"; } },
    OTHER { @Override public String toString() { return "Other"; } }

}